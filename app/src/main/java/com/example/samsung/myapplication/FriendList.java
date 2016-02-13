package com.example.samsung.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.Adapter.FriendListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FriendList extends ActionBarActivity {

    private PullToRefreshListView listView;
    private TextView textView;
    private String name,account,friendAccount,message,baseUrl,saveUrlString,getUrlString;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>() , data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        baseUrl = "http://8.sundoge.applinzi.com/index.php?";
        AsyncHttpClient getFriendListClient = new AsyncHttpClient();
        getUrlString = baseUrl + "table=friendList&method=get&data=" + friendListRequest();
        System.out.println(getUrlString);
        getFriendListClient.get(getUrlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                textView.setText(response.toString());
                Toast.makeText(getApplicationContext(), "chenggong", Toast.LENGTH_SHORT).show();
            }
        });

        //create friendlist
//        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
//        listView.getRefreshableView().setDivider(null);
//        listView.setMode(PullToRefreshBase.Mode.BOTH);
//        listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
//        data = getData();
//        FriendListAdapter friendListAdapter = new FriendListAdapter();
//        listView.setAdapter(friendListAdapter);

        //friendlist select
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(FriendList.this, ChatView.class));
//            }
//        });

        //friendlist pull down to refresh
//        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                getData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//        });

    }

    private List<Map<String, Object>> getData()
    {
        Map<String, Object> map;
        map = new HashMap<String, Object>();
         map.put("image", R.drawable.touxiang1);
        map.put("name", "爸爸");
        map.put("message", "123");
        map.put("time","1分钟前");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("image", R.drawable.touxiang2);
        map.put("name", "妈妈");
        map.put("message", "321");
        map.put("time", "2分钟前");
        list.add(map);
        return list;
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

    private String friendListRequest(){
        String result = new String();
        result = "{%22Friendrequest%22:%22"+account+"%22}";
        return result;
    }

    private String friendRequest(){
        String result = new String();
        result = "{%22Friendrequest%22:%22"+account+"%22,%22Friendresponce%22:%22"+friendAccount
                +"%22,%22Sign%22:%220%22,%22Message%22:%22"+message+"%22}";
        return result;
    }
}