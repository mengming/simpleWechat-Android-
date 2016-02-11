package com.example.samsung.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.Adapter.FriendListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
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
    private String name,account;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>() , data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(name);
        textView.append(account);
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

}