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
    private TextView textView;
    private String name,account,friendAccount,message,baseUrl,saveUrlString,getUrlString,
            agreeUrlString,friend,askMessage;
    private ArrayList<FriendBean> friendBeans;
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        friendBeans = new ArrayList<>();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        baseUrl = "http://8.sundoge.applinzi.com/index.php?";
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
                friendListAdapter = new FriendListAdapter(FriendList.this,friendBeans);
                initFriendListView();
            }
        });

    }

    private void initFriendListView(){
//        //create friendlist
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        listView.getRefreshableView().setDivider(null);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        listView.setAdapter(friendListAdapter);

        //friendlist select
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (friendBeans.get(position - 1).getSign() == 0) addFriendResponseDialog(position-1);
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
        result = "{%22Friendrequest%22:%22"+account+"%22,%22Friendresponse%22:%22"+account+"%22}";
        return result;
    }

    private String friendRequest(){
        String result = new String();
        result = "{%22Friendrequest%22:%22"+account+"%22,%22Friendresponse%22:%22"+friendAccount
                +"%22,%22Sign%22:%220%22,%22Message%22:%22"+message+"%22}";
        return result;
    }

    private String friendResponse(){
        String result = new String();
        result = "{%22Friendrequest%22:%22"+friend+"%22,%22Friendresponse%22:%22"+account
                +"%22,%22Sign%22:%221%22}";
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
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}