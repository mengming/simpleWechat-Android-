package com.example.samsung.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.samsung.Data.FriendBean;
import com.example.samsung.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {
    public ArrayList<FriendBean> friendBeans;
    private LayoutInflater mInflater;
    private Context context;
    private String account,name,avatarUrl,message;
    private int sign;

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
        Uri uri = null;
        FriendBean friendBean = friendBeans.get(position);
        sign = friendBean.getSign();
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.friend, parent,false);
            holder.nameText = (TextView) convertView.findViewById(R.id.name);
            holder.messageText =  (TextView)convertView.findViewById(R.id.message);
            holder.timeText = (TextView)convertView.findViewById(R.id.time);
            holder.avatar = (SimpleDraweeView) convertView.findViewById(R.id.pic);
            holder.newMessageText = (TextView) convertView.findViewById(R.id.newMessageNum);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        //如果为未添加好友
        if (sign==0) {
            if (friendBean.getFriendResponse().equals(account))
                message = "请求添加您为好友:" + friendBean.getMessage();
            else message = "等待对方同意您的请求";
            name = friendBean.getFriendName();
            avatarUrl = friendBean.getFriendPic();
            if (avatarUrl != null) uri = Uri.parse(avatarUrl);
        }
        else {
            name = friendBean.getFriendName();
            if (friendBean.getSender().equals(account)) message = "我:"+friendBean.getMessage();
            else message = name + ":" + friendBean.getMessage();
            uri = Uri.parse(friendBean.getFriendPic());
            if (friendBean.getJudgeNew()) holder.newMessageText.setText("new");
            else holder.newMessageText.setText("");
        }
        holder.nameText.setText(name);
        holder.messageText.setText(message);
        holder.timeText.setText(friendBean.getTime());
        holder.avatar.setImageURI(uri);
        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
        public TextView newMessageText;
        public SimpleDraweeView avatar;
    }

}
