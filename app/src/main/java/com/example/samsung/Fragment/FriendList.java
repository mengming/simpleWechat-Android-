package com.example.samsung.Fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.samsung.Adapter.FriendListAdapter;
import com.example.samsung.Data.FriendBean;
import com.example.samsung.Data.FriendListEvent;
import com.example.samsung.myapplication.Main;
import com.example.samsung.myapplication.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FriendList extends Fragment {

    private String name,account,friendAccount,friendName,getUnsignedUrlString, agreeUrlString,askMessage,
            disagreeUrlString,getLatestMessagesUrlString, sendMessageUrlString,countUrlString;
    static String baseUrl = "http://119.29.186.49/wechatInterface/index.php?";
    private int unsignedNumber,count;
    private PullToRefreshListView friendListView;
    private ArrayList<FriendBean> friendBeans,newFriendBeans;
    private FriendListAdapter friendListAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlist,container,false);
        initFriendListView(view);
        return view;
    }

    public void onEventMainThread(FriendListEvent event) {
        newFriendBeans = new ArrayList<>();
        JSONArray latestMessagesArray = event.latestMessagesArray;
        JSONArray unsignedArray = event.unsignedArray;
        if (unsignedArray != null) newFriendBeans.addAll(unsignedArrayHandle(unsignedArray));
        newFriendBeans.addAll(latestArrayHandle(latestMessagesArray));
        friendBeans.clear();
        friendBeans.addAll(newFriendBeans);
        friendListAdapter.notifyDataSetChanged();
        System.out.println("friendList");
    }

    private void initFriendListView(View view){
        Bundle data = getArguments();
        account = data.getString("account");
        name = data.getString("name");
        friendBeans = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getActivity(),friendBeans,account);
        friendListView = (PullToRefreshListView) view.findViewById(R.id.ptr_friend);
        friendListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        friendListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        friendListView.getRefreshableView().setDivider(new ColorDrawable(Color.GRAY));
        friendListView.getRefreshableView().setDividerHeight(1);
        friendListView.setAdapter(friendListAdapter);
        //friendList select
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendBean friendBean = friendBeans.get(position - 1);
                if (friendBean.getSign() == 0) {
                    if (friendBean.getFriendResponse().equals(account))
                        addFriendResponseDialog(position - 1);
                    else
                        Toast.makeText(getView().getContext(), "等待对方同意", Toast.LENGTH_SHORT).show();
                } else {
                    friendAccount = judgeFriendAccount(friendBean);
                    friendName = friendBean.getFriendName();
                    sharedPreferences = getActivity().getSharedPreferences("IDList", Activity.MODE_PRIVATE);
                    ((Main)getActivity()).openChat(friendAccount,friendBean.getFriendPic(),friendName,friendBean.getFriendPhone());
                    sharedPreferences.edit().remove(friendAccount).putInt(friendAccount, friendBean.getID()).commit();
                }
            }
        });

        //friendList pull down to refresh
        friendListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getFriendList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void getFriendList(){
        getFriendListUnsigned();
        getFriendListSigned();
    }



    private String getFriendListParam(){
        return "table=friendList&method=get&identification=" + account;
    }

    private String friendResponse(){
        return "{%22friendRequest%22:%22"+friendAccount+"%22,%22friendResponse%22:%22"+account
                +"%22,%22sign%22:%221%22}";
    }

    private String friendDisagree(){
        return "{%22friendRequest%22:%22"+friendAccount+"%22,%22friendResponse%22:%22"+account+"%22}";
    }

    private String judgeFriendAccount(FriendBean friendBean){
        String result = new String();
        if (friendBean.getSender().equals(account)) result = friendBean.getReceiver();
        else result = friendBean.getSender();
        return result;
    }

    private String sendMessageParam(){
        return "table=messageList&method=save&data="+ "{%22sender%22:%22" + account +
                "%22,%22receiver%22:%22" + friendAccount +
                "%22,%22message%22:%22"+ name +"已添加你为好友%22}";
    }

    private void agreeUnsignedFriend(FriendBean friendBean){
        friendAccount = friendBean.getFriendRequest();
        agreeUrlString = baseUrl + "table=friendList&method=update&data=" + friendResponse();
        askMessage = friendBean.getMessage();
        AsyncHttpClient agreeHttpClient = new AsyncHttpClient();
        agreeHttpClient.get(agreeUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity().getApplicationContext(),"添加好友成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        sendMessageUrlString = baseUrl + sendMessageParam();
        AsyncHttpClient sendMessageHttpClient = new AsyncHttpClient();
        sendMessageHttpClient.get(sendMessageUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getFriendList();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void disagreeUnsignedFriend(FriendBean friendBean){
        friendAccount = friendBean.getFriendRequest();
        disagreeUrlString = baseUrl + "table=friendList&method=delete&data=" + friendDisagree();
        AsyncHttpClient disagreeHttpClient = new AsyncHttpClient();
        disagreeHttpClient.get(disagreeUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity().getApplicationContext(), "你已拒绝对方申请", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void addFriendResponseDialog(int index){
        final int position = index;
        LayoutInflater dialogInflater = LayoutInflater.from(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                agreeUnsignedFriend(friendBeans.get(position));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("不同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disagreeUnsignedFriend(friendBeans.get(position));
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private String getLatestMessagesParam(){
        return "method=getLatestMessages&identification=" + account;
    }

    private void getFriendListUnsigned(){
        newFriendBeans = new ArrayList<>();
        AsyncHttpClient getFriendListUnsignedClient = new AsyncHttpClient();
        getUnsignedUrlString = baseUrl + getFriendListParam();
        getFriendListUnsignedClient.get(getUnsignedUrlString,unsignedHandler);
    }

    private void getFriendListSigned(){
        getLatestMessages();
    }

    public void getLatestMessages(){
        AsyncHttpClient getLatestMessagesClient = new AsyncHttpClient();
        getLatestMessagesUrlString = baseUrl + getLatestMessagesParam();
        getLatestMessagesClient.get(getLatestMessagesUrlString,latestHandler);
    }

    JsonHttpResponseHandler unsignedHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            if (response != null)newFriendBeans.addAll(unsignedArrayHandle(response));
        }
    };

    JsonHttpResponseHandler latestHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            newFriendBeans.addAll(latestArrayHandle(response));
            friendBeans.clear();
            friendBeans.addAll(newFriendBeans);
            friendListAdapter.notifyDataSetChanged();
            friendListView.onRefreshComplete();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            friendBeans.clear();
            friendBeans.addAll(newFriendBeans);
            friendListAdapter.notifyDataSetChanged();
            friendListView.onRefreshComplete();
        }
    };

    private ArrayList<FriendBean> latestArrayHandle(JSONArray response){
        ArrayList<FriendBean> result = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences("IDList", Activity.MODE_PRIVATE);
        int length = response.length();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i <length; i++) {
            JSONObject friendJsonObject = null;
            try {
                friendJsonObject = response.getJSONObject(i);
                Gson gson = new Gson();
                FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                friendBean.setSign(1);
                friendAccount = judgeFriendAccount(friendBean);
                int gotID = sharedPreferences.getInt(friendAccount+"got",0);
                int ID = friendBean.getID();
                if (gotID != ID) {
                    if (isBackground(getActivity())) initNotification(friendBean.getFriendName(),friendBean.getMessage());
                    gotID = ID;
                    editor.remove(friendAccount+"got").putInt(friendAccount+"got",ID);
                }
//                friendBean.setJudgeNew(false);
//                int oldID = sharedPreferences.getInt(friendAccount, 0);
//                int newID = sharedPreferences.getInt(friendAccount+"newID",0);
//                int ID = friendBean.getID();
//                String sender = friendBean.getSender();
//                if (oldID==0) {
//                    if (sender.equals(friendAccount)) editor.putInt(friendAccount+"newID",ID);
//                    else break;
//                }
//                else if (sender.equals(friendAccount)) {
//                    if (oldID!=ID) {
//                        if (newID != ID) {
//                            editor.remove(friendAccount + "newID").putInt(friendAccount + "newID", ID);
////                            initNotification(friendAccount, friendBean.getMessage());
//                        }
//                        friendBean.setJudgeNew(true);
//                    }
//                }
                result.add(friendBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.commit();
        return result;
    }

    private ArrayList<FriendBean> unsignedArrayHandle(JSONArray response){
        unsignedNumber = 0;
        ArrayList<FriendBean> result = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject friendJsonObject = response.getJSONObject(i);
                if (friendJsonObject.getInt("sign") == 0) {
                    Gson gson = new Gson();
                    FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                    result.add(friendBean);
                    unsignedNumber++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initNotification(String friendName,String message){
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Main.NOTIFICATION_SERVICE);
        int icon = R.drawable.default_avatar;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(icon)
                .setContentTitle(friendName)
                .setTicker(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setDefaults(Notification.FLAG_AUTO_CANCEL)
                .setContentIntent(getDefalutIntent(friendName))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    public PendingIntent getDefalutIntent(String friendAccount){
        Intent notificationIntent = new Intent(getActivity(),Main.class);
        notificationIntent.putExtra("account",account);
        notificationIntent.putExtra("friendAccount", friendAccount);
        PendingIntent pendingIntent= PendingIntent.getActivity(getActivity(), 1, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
