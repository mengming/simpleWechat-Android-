package com.example.samsung.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.samsung.Data.CommunityBean;
import com.example.samsung.myapplication.R;

import java.util.ArrayList;

public class CommunityAdapter extends BaseAdapter{

    private ArrayList<CommunityBean> communityBeans;
    private LayoutInflater mInflater;

    public CommunityAdapter(Context context,ArrayList<CommunityBean> communityBeans){
        mInflater = LayoutInflater.from(context);
        this.communityBeans = communityBeans;
    }

    @Override
    public int getCount() {
        return communityBeans.size();
    }

    @Override
    public Object getItem(int index) {
        return communityBeans.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CommunityBean communityBean = communityBeans.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.posting, parent,false);
            holder.nameText = (TextView) convertView.findViewById(R.id.posting_name);
            holder.messageText =  (TextView)convertView.findViewById(R.id.posting_message);
            holder.timeText = (TextView)convertView.findViewById(R.id.posting_time);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();
        holder.nameText.setText(communityBean.getPosterName());
        holder.messageText.setText(communityBean.getMessage());
        holder.timeText.setText(communityBean.getTime());
        return convertView;
    }


    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
    }
}
