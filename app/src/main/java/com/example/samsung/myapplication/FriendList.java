package com.example.samsung.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.Adapter.FriendListAdapter;
import com.example.samsung.Data.FriendBean;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FriendList extends ActionBarActivity {

    private PullToRefreshListView listView;
    private String name,account,friendAccount,message,saveUrlString,getUrlString,
            agreeUrlString,friend,askMessage,disagreeUrlString;
    private String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
    private ArrayList<FriendBean> friendBeans = new ArrayList<>();
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        getExtra();
        getFriendList();
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
            case R.id.action_addfriend:
                addFriendDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getExtra(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
    }

    private void getFriendList(){
        AsyncHttpClient getFriendListClient = new AsyncHttpClient();
        getUrlString = baseUrl + "table=friendList&method=get&data=" + friendListRequestAndResponse();
        getFriendListClient.get(getUrlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject friendJsonObject = response.getJSONObject(i);
                        Gson gson = new Gson();
                        FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                        friendBeans.add(friendBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                friendListAdapter = new FriendListAdapter(FriendList.this, friendBeans, account);
                initFriendListView();
            }
        });
    }

    private void initFriendListView(){
        //create friendlist
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        listView.getRefreshableView().setDivider(null);
        listView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        listView.setAdapter(friendListAdapter);

        //friendlist select
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (friendBeans.get(position - 1).getSign() == 0) addFriendResponseDialog(position-1);
                else {
                    Intent chatViewIntent = new Intent();
                    chatViewIntent.setClass(FriendList.this,ChatView.class);
                    chatViewIntent.putExtra("account",account);
                    chatViewIntent.putExtra("friendAccount",judgeFriendAccount(friendBeans.get(position-1)));
                    startActivity(chatViewIntent);
                }
            }
        });

        //friendlist pull down to refresh
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initFriendListView();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    protected void addFriendDialog(){
        LayoutInflater dialogInflater = LayoutInflater.from(this);
        final View dialogView = dialogInflater.inflate(R.layout.alert_dialog_view,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
        builder.setMessage("请输入朋友账号");
        builder.setView(dialogView);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText dialogAccount = (EditText) dialogView.findViewById(R.id.friend_account);
                friendAccount = dialogAccount.getText().toString();
                EditText dialogRequestMessage = (EditText) dialogView.findViewById(R.id.friend_request_message);
                message = dialogRequestMessage.getText().toString();
                saveRequestToServer();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void saveRequestToServer(){
        saveUrlString = baseUrl + "table=friendList&method=save&data=" + friendRequest();
        AsyncHttpClient saveHttpClient = new AsyncHttpClient();
        saveHttpClient.get(saveUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), "发送请求成功，等待对方确认", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "对方账号不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String friendListRequestAndResponse(){
        String result = new String();
        result = "{%22friendRequest%22:%22"+account+"%22,%22friendResponse%22:%22"+account+"%22}";
        return result;
    }

    private String friendRequest(){
        String result = new String();
        result = "{%22friendRequest%22:%22"+account+"%22,%22friendResponse%22:%22"+friendAccount
                +"%22,%22sign%22:%220%22,%22message%22:%22"+message+"%22}";
        return result;
    }

    private String friendResponse(){
        String result = new String();
        result = "{%22friendRequest%22:%22"+friend+"%22,%22friendResponse%22:%22"+account
                +"%22,%22sign%22:%221%22}";
        return result;
    }

    private String friendDisagree(){
        String result = new String();
        result = "{%22friendRequest%22:%22"+friend+"%22,%22friendResponse%22:%22"+account+"%22}";
        return result;
    }

    private String judgeFriendAccount(FriendBean friendBean){
        String result = new String();
        if (friendBean.getFriendrequest().equals(account)) result = friendBean.getFriendresponse();
        else result = friendBean.getFriendrequest();
        return result;
    }

    private void agreeUnsignedFriend(FriendBean friendBean){
        friend = friendBean.getFriendrequest();
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
    }

    private void disagreeUnsignedFriend(FriendBean friendBean){
        friend = friendBean.getFriendrequest();
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

}