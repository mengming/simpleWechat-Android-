package com.example.samsung.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendList extends ActionBarActivity {

    private PullToRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        //create friendlist
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        listView.getRefreshableView().setDivider(null);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        SimpleAdapter listAdapter = new SimpleAdapter(this, getData(), R.layout.friend,
                new String[]{"name", "message","image"},
                new int[]{R.id.name, R.id.message,R.id.image});
        listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object attentionList, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && attentionList instanceof Bitmap){
                    ImageView iv=(ImageView)view;
                    iv.setImageBitmap((Bitmap) attentionList);
                    return true;
                }else{
                    return false;
                }
            }
        });
        listView.setAdapter(listAdapter);

        //friendlist select
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(FriendList.this, ChatView.class));
            }
        });

        //friendlist pull down to refresh
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    //friendlist data
    private List<Map<String,Object>> getData(){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>> ();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name","爸爸");
        map.put("message","123");
        map.put("image",R.drawable.touxiang1);
        list.add(map);
        map = new HashMap<String,Object>();
        map.put("name","妈妈");
        map.put("message","321");
        map.put("image",R.drawable.touxiang2);
        list.add(map);
        return list;
    }
}