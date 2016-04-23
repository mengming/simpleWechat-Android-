package com.example.samsung.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.samsung.Adapter.CommunityAdapter;
import com.example.samsung.Data.CommunityBean;
import com.example.samsung.myapplication.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Community extends Fragment{

    private PullToRefreshListView communityListView;
    static String baseUrl = "http://119.29.186.49/wechatInterface/index.php?";
    private String getUrl,saveUrl;
    private ArrayList<CommunityBean> communityBeans,newCommunityBeans;
    private CommunityAdapter adapter;
    private Button btnSendText,btnSendPicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        btnSendText = (Button) view.findViewById(R.id.btn_send_text);
        btnSendText.setOnClickListener(listener);
        btnSendPicture = (Button) view.findViewById(R.id.btn_send_picture);
        btnSendPicture.setOnClickListener(listener);
        communityBeans = new ArrayList<>();
        communityListView = (PullToRefreshListView) view.findViewById(R.id.ptr_community);
        adapter = new CommunityAdapter(getActivity(),communityBeans);
        communityListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        communityListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        communityListView.getRefreshableView().setDivider(new ColorDrawable(Color.GRAY));
        communityListView.getRefreshableView().setDividerHeight(0);
        communityListView.setAdapter(adapter);
        communityListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPostings();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        communityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        getPostings();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send_picture :
                    Toast.makeText(getActivity().getApplicationContext(),"图片",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_send_text :
                    Toast.makeText(getActivity().getApplicationContext(),"文字",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void getPostings(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        getUrl = baseUrl + "table=community&method=get";
        asyncHttpClient.get(getUrl,getHandler);
    }

    private JsonHttpResponseHandler getHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            newCommunityBeans = new ArrayList<>();
            for (int i=0;i<response.length();i++) {
                try {
                    JSONObject jsonObject = (JSONObject) response.get(i);
                    Gson gson = new Gson();
                    CommunityBean communityBean = gson.fromJson(jsonObject.toString(),CommunityBean.class);
                    newCommunityBeans.add(communityBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            communityBeans.clear();
            communityBeans.addAll(newCommunityBeans);
            adapter.notifyDataSetChanged();
            communityListView.onRefreshComplete();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    };

}
