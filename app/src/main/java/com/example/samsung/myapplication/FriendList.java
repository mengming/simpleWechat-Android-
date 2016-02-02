package com.example.samsung.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendList extends ActionBarActivity {

    private PullToRefreshListView listView;
    private List<Map<String, Object>> data;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        //create friendlist
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        listView.getRefreshableView().setDivider(null);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        data = getData();
        baseAdapter Adapter = new baseAdapter(this);
        listView.setAdapter(Adapter);

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
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private class baseAdapter extends BaseAdapter{
        private LayoutInflater mInflater = null;
        private baseAdapter(Context context)
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if(convertView == null)
            {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.friend, null);
                holder.nameText = (TextView) convertView.findViewById(R.id.name);
                holder.messageText = (TextView)convertView.findViewById(R.id.message);
                holder.timeText = (TextView)convertView.findViewById(R.id.time);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.imageView.setBackgroundResource((Integer)data.get(position).get("image"));
            holder.nameText.setText((String)data.get(position).get("name"));
            holder.messageText.setText((String)data.get(position).get("message"));

            return convertView;
        }
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
        public ImageView imageView;
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
        map.put("time","2分钟前");
        list.add(map);
        return list;
    }
}