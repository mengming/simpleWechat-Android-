package com.example.samsung.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.samsung.Adapter.FriendListAdapter;
import com.example.samsung.Data.FriendBean;
import com.example.samsung.Data.FriendListEvent;
import com.example.samsung.Service.FriendListService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class FriendList extends ActionBarActivity {

    private PullToRefreshListView friendListView;
    private String name,account,friendAccount,getUnsignedUrlString, agreeUrlString,askMessage,
            disagreeUrlString,getLatestMessagesUrlString, sendMessageUrlString,countUrlString;
    static String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
    private ArrayList<FriendBean> friendBeans,newFriendBeans;
    private FriendListAdapter friendListAdapter;
    private int unsignedNumber,count;
    private boolean isCreate,isStart;
    private Intent intent;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.friendlist);

        System.out.println("onCreateF");
        EventBus.getDefault().register(this);
        getExtra();
        initFriendListView();
        isCreate = true;
        getFriendList();
//        intent = new Intent(this,FriendListService.class);
//        intent.putExtra("account", account);
//        startService(intent);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("onServiceDisconnected");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStartF");
        isStart = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResumeF");
        if (!isCreate && !isStart) getFriendList();
        isCreate = false;
        isStart = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStopF");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        EventBus.getDefault().unregister(this);
        System.out.println("onDestroyF");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionbar_add_friend: 
                checkFriendDialog();
                break;
            case R.id.actionbar_logout:
                logout();
                break;
            case R.id.actionbar_self_information:
                checkSelfInformation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onEventMainThread(FriendListEvent event) {
        newFriendBeans = new ArrayList<>();
        JSONArray latestMessagesArray = event.latestMessagesArray;
        JSONArray unsignedArray = event.unsignedArray;
        newFriendBeans.addAll(unsignedArrayHandle(unsignedArray));
        newFriendBeans.addAll(latestArrayHandle(latestMessagesArray));
        friendBeans.clear();
        friendBeans.addAll(newFriendBeans);
        friendListAdapter.notifyDataSetChanged();
    }

    private void initNotification(String name,String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.chat_pic;
        String tickerText = message;
        long when = System.currentTimeMillis();
        Context context = getApplicationContext();
        CharSequence contentTitle = name;
        CharSequence contentText = message;
        Notification notification = new Notification(icon, tickerText, when);
        Intent notificationIntent = new Intent(this,ChatView.class);
        notificationIntent.putExtra("account",account);
        notificationIntent.putExtra("friendAccount",name);
        notification.defaults = notification.DEFAULT_SOUND;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        notificationManager.notify(1,notification);
    }

    private void checkSelfInformation() {
        Intent selfInformationIntent = new Intent();
        selfInformationIntent.setClass(FriendList.this,AccountInformation.class);
        selfInformationIntent.putExtra("statusCode",1);
        selfInformationIntent.putExtra("account",account);
        startActivity(selfInformationIntent);
    }

    private void logout() {
        sharedPreferences = getSharedPreferences("login",Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        finish();
    }

    private void checkFriendDialog() {
        final EditText accountInput = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
        builder.setMessage("请输入对方账号：").setView(accountInput);
        builder.setPositiveButton("查找", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent friendInformationIntent = new Intent();
                friendAccount = accountInput.getText().toString();
                friendInformationIntent.setClass(FriendList.this, AccountInformation.class);
                friendInformationIntent.putExtra("statusCode", 0);
                friendInformationIntent.putExtra("friendAccount", friendAccount);
                friendInformationIntent.putExtra("account", account);
                startActivity(friendInformationIntent);
                dialog.dismiss();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }

    private void getExtra(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
    }

    private void getFriendList(){
        getFriendListUnsigned();
        getFriendListSigned();
    }

    private void initFriendListView(){
        //create friendlist
        friendBeans = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(FriendList.this,friendBeans,account);
        friendListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        friendListView.getRefreshableView().setDivider(null);
        friendListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        friendListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        friendListView.getRefreshableView().setDivider(new ColorDrawable(Color.GRAY));
        friendListView.getRefreshableView().setDividerHeight(1);
        friendListView.setAdapter(friendListAdapter);
        //friendlist select
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendBean friendBean = friendBeans.get(position-1);
                if (friendBean.getSign() == 0) {
                    if (friendBean.getFriendResponse().equals(account)) addFriendResponseDialog(position - 1);
                    else Toast.makeText(getApplicationContext(),"等待对方同意",Toast.LENGTH_SHORT).show();
                }
                else {
                    friendAccount = judgeFriendAccount(friendBeans.get(position - 1));
                    Intent chatViewIntent = new Intent();
                    sharedPreferences = getSharedPreferences("IDList", Activity.MODE_PRIVATE);
                    chatViewIntent.setClass(FriendList.this, ChatView.class);
                    chatViewIntent.putExtra("account", account);
                    chatViewIntent.putExtra("friendAccount",friendAccount);
                    startActivity(chatViewIntent);
                    sharedPreferences.edit().remove(friendAccount).putInt(friendAccount,friendBean.getID())
                            .remove(friendAccount+"Num").putInt(friendAccount+"Num",0).commit();
                }
            }
        });

        //friendlist pull down to refresh
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
                Toast.makeText(getApplicationContext(),"添加好友成功",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "您已拒绝对方申请", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void addFriendResponseDialog(int index){
        final int position = index;
        LayoutInflater dialogInflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
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
            newFriendBeans.addAll(unsignedArrayHandle(response));
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
        sharedPreferences = getSharedPreferences("IDList", Activity.MODE_PRIVATE);
        int length = response.length();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            for (int i = 0; i <length; i++) {
                JSONObject friendJsonObject = response.getJSONObject(i);
                Gson gson = new Gson();
                FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                friendBean.setSign(1);
                friendBean.setMessage(friendBean.getSender() + ":" + friendJsonObject.getString("message"));
                friendAccount = judgeFriendAccount(friendBean);
                //最新已读消息的ID为oldID
                int oldID = sharedPreferences.getInt(friendAccount, 0);
                int unReadNum = sharedPreferences.getInt(friendAccount + "Num", 0);
                System.out.println(oldID);
                System.out.println(unReadNum);
                //如果为新聊天无oldID则保存ID及未读消息数目置0
                if (oldID==0) {
                    editor.putInt(friendAccount, friendBean.getID());
                    editor.putInt(friendAccount + "Num", 0);
                }
                //有oldID判断消息源,如果是自己发的则设置已读和未读数目为0
                else if (friendBean.getSender().equals(account)) {
                    editor.remove(friendAccount+"Num");
                    editor.putInt(friendAccount+"Num",0);
                }//如果是朋友发的则判断上一条是否已读
                else if (unReadNum==0) {
                    //如果上一条已读，则判断数据是否有刷新
                    if (oldID!=friendBean.getID()) {
                        //如果刷新则更新oldID以及unReadNum
                        editor.remove(friendAccount);
                        editor.putInt(friendAccount,friendBean.getID());
                        editor.remove(friendAccount+"Num");
                        editor.putInt(friendAccount+"Num",countUnReadNum(oldID,friendAccount));
                    }}//如果上一条未读，则判断unReadNum是否需要刷新
                else {
                    int newUnReadNum = countUnReadNum(oldID,friendAccount);
                    if (newUnReadNum!=unReadNum) {
                        editor.remove(friendAccount);
                        editor.putInt(friendAccount + "Num", newUnReadNum);
                    }}
                friendBean.setUnReadNum(sharedPreferences.getInt(friendAccount+"Num",0));
                result.add(friendBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private int countUnReadNum(final int oldID,String friendAccount) {
        AsyncHttpClient client = new AsyncHttpClient();
        count = 0;
        countUrlString = baseUrl + "table=messageList&method=get&data="+
                "{%22sender%22:%22" + account + "%22,%22receiver%22:%22" + friendAccount + "%22}";
        client.get(countUrlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        int ID = response.getJSONObject(i).getInt("ID");
                        if (ID >= oldID) count++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return count;
    }

}