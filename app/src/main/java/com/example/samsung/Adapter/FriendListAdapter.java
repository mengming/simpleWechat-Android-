package com.example.samsung.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.Data.FriendBean;
import com.example.samsung.myapplication.FriendList;
import com.example.samsung.myapplication.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class FriendListAdapter extends BaseAdapter {
    public ArrayList<FriendBean> friendBeans;
    private LayoutInflater mInflater;
    private Context context;
    private String account,name;

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
        //如果缓存convertView为空，则需要创建View
        if(convertView == null)
        {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.friend, parent,false);
            holder.nameText = (TextView) convertView.findViewById(R.id.name);
            holder.messageText = (TextView)convertView.findViewById(R.id.message);
            holder.timeText = (TextView)convertView.findViewById(R.id.time);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        if (friendBean.getSign()==0) {
            if (friendBean.getFriendresponse().equals(account)) {
                name = friendBean.getFriendrequest();
                holder.nameText.setText(name);
                holder.messageText.setText("请求添加您为好友:" + friendBean.getMessage());
                holder.timeText.setText(friendBean.getTime());
            }
            else {
                name = friendBean.getFriendresponse();
                holder.nameText.setText(name);
                holder.messageText.setText("等待对方同意您的请求");
                holder.timeText.setText(friendBean.getTime());
            }
        }
        else {
            if (friendBean.getFriendresponse().equals(account)) name = friendBean.getFriendrequest();
            else name = friendBean.getFriendresponse();
            holder.nameText.setText(name);
            holder.messageText.setText(friendBean.getMessage());
            holder.timeText.setText(friendBean.getTime());
        }
        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
    }

}
