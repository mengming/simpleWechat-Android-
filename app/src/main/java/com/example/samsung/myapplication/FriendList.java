package com.example.samsung.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
            disagreeUrlString,getLatestMessagesUrlString, sendMessageUrlString;
    static String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
    private ArrayList<FriendBean> friendBeans,newFriendBeans;
    private FriendListAdapter friendListAdapter;
    private int unsignedNumber;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.friendlist);

        getExtra();
        initFriendListView();
        getFriendList();
//        Intent intent = new Intent();
//        intent.setClass(this,FriendListService.class);
//        startService(intent);
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFriendList();
    }

    public void onEventMainThread(FriendListEvent event) {
        String msg = "onEventMainThread收到了消息：" + event.getMsg();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

//    public void onEventMainThread(FriendListEvent event) {
//        ArrayList<FriendBean> newFriendBeans = event.getFriendBeans();
//        friendBeans.clear();
//        friendBeans.addAll(newFriendBeans);
//        friendListAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                    Intent chatViewIntent = new Intent();
                    friendBean.setUnRead(false);
                    chatViewIntent.setClass(FriendList.this, ChatView.class);
                    chatViewIntent.putExtra("account", account);
                    chatViewIntent.putExtra("friendAccount", judgeFriendAccount(friendBeans.get(position - 1)));
                    startActivity(chatViewIntent);
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
        unsignedNumber = 0;
        AsyncHttpClient getFriendListUnsignedClient = new AsyncHttpClient();
        getUnsignedUrlString = baseUrl + getFriendListParam();
        getFriendListUnsignedClient.get(getUnsignedUrlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject friendJsonObject = response.getJSONObject(i);
                        if (friendJsonObject.getInt("sign") == 0) {
                            Gson gson = new Gson();
                            FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                            friendBean.setNewJudge(false);
                            newFriendBeans.add(friendBean);
//                            unsignedNumber++;
                            System.out.println("");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getFriendListSigned(){
        getLatestMessages();
    }

    public void getLatestMessages(){
        AsyncHttpClient getLatestMessagesClient = new AsyncHttpClient();
        getLatestMessagesUrlString = baseUrl + getLatestMessagesParam();
        getLatestMessagesClient.get(getLatestMessagesUrlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                sharedPreferences = getSharedPreferences("IDList", Activity.MODE_PRIVATE);
                int length = response.length();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    for (int i = 0; i <length; i++) {
                        JSONObject friendJsonObject = response.getJSONObject(i);
                        Gson gson = new Gson();
//                        FriendBean friendBean1 = friendBeans.get(i+unsignedNumber);
                        FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                        friendBean.setSign(1);
                        friendBean.setMessage(friendBean.getSender() + ":" + friendJsonObject.getString("message"));
                        friendAccount = judgeFriendAccount(friendBean);
                        int oldID = sharedPreferences.getInt(friendAccount, 0);
                        if (oldID==0) editor.putInt(friendAccount, friendBean.getID());
                        else if (oldID!=friendBean.getID()) {
                            friendBean.setNewJudge(true);
                            friendBean.setUnRead(true);
                            editor.remove(friendAccount);
                            editor.putInt(friendAccount, friendBean.getID());
                        }
                        else if (!friendBean.getUnRead()) friendBean.setNewJudge(false);
                        newFriendBeans.add(friendBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.commit();
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
        });
    }

}