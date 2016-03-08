package com.example.samsung.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.Data.FriendBean;
import com.example.samsung.myapplication.FriendList;
import com.example.samsung.myapplication.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.LinkedList;

public class FriendListAdapter extends BaseAdapter {
    public ArrayList<FriendBean> friendBeans;
    private LayoutInflater mInflater;
    private Context context;
    private String account,name;
    private int sign,unReadNum;

    public FriendListAdapter(Context context,ArrayList<FriendBean> friendBeans,String account)
    {
        mInflater = LayoutInflater.from(context);
        this.friendBeans = friendBeans;
        this.context = context;
        this.account = account;
    }
    @Override
    public int getCount() {
        return friendBeans.size();
    }

    @Override
    public Object getItem(int index) {
        return friendBeans.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        FriendBean friendBean = friendBeans.get(position);
        sign = friendBean.getSign();
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.friend, parent,false);
            holder.nameText = (TextView) convertView.findViewById(R.id.name);
            holder.messageText = (TextView)convertView.findViewById(R.id.message);
            holder.timeText = (TextView)convertView.findViewById(R.id.time);
            holder.avator = (SimpleDraweeView) convertView.findViewById(R.id.pic);
            holder.newMessageNumText = (TextView) convertView.findViewById(R.id.newMessageNum);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        if (sign==0) {
            if (friendBean.getFriendResponse().equals(account)) {
                name = friendBean.getFriendRequest();
                holder.nameText.setText(name);
                holder.messageText.setText("请求添加您为好友:" + friendBean.getMessage());
                holder.timeText.setText(friendBean.getTime());
            }
            else {
                name = friendBean.getFriendResponse();
                holder.nameText.setText(name);
                holder.messageText.setText("等待对方同意您的请求");
                holder.timeText.setText(friendBean.getTime());
            }
        }
        else {
            if (friendBean.getReceiver().equals(account)) name = friendBean.getSender();
            else name = friendBean.getReceiver();
            holder.nameText.setText(name);
            holder.messageText.setText(friendBean.getMessage());
            holder.timeText.setText(friendBean.getTime());
            unReadNum = friendBean.getUnReadNum();
            if (unReadNum==0) holder.newMessageNumText.setText("");
            else holder.newMessageNumText.setText(unReadNum);
        }
        Uri uri = Uri.parse("res:///"+R.drawable.chat_pic);
        holder.avator.setImageURI(uri);
        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
        public TextView newMessageNumText;
        public SimpleDraweeView avator;
    }

}
