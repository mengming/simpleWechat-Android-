package com.example.samsung.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.samsung.Data.JudgeTime;
import com.example.samsung.Data.MessageBean;
import com.example.samsung.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class ChatViewAdapter  extends BaseAdapter{

    private LayoutInflater mInflater;
    private MessageBean messageBean;
    private String account,friendAccount,selfAvatar,friendAvatar,time;
    public ArrayList<MessageBean> messageBeans;
    private Uri uri;

    public ChatViewAdapter(Context context,ArrayList<MessageBean> messageBeans
            ,String account,String friendAccount,String selfAvatar,String friendAvatar){
        mInflater = LayoutInflater.from(context);
        this.messageBeans = messageBeans;
        this.account = account;
        this.friendAccount = friendAccount;
        this.selfAvatar = selfAvatar;
        this.friendAvatar = friendAvatar;
    }

    @Override
    public int getCount() {
        return messageBeans.size();
    }

    @Override
    public Object getItem(int index) {
        return messageBeans.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder1 = null;
        ViewHolder holder2 = null;
        messageBean = messageBeans.get(position);
        //if sender is self then otherOrSelf is true,else is false;
        Boolean otherOrSelf = false;
        if (messageBean.getSender().equals(account)) otherOrSelf = true;
        if (otherOrSelf) {
            holder1 = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chat_view_right, null);
            holder1.messageText = (TextView) convertView.findViewById(R.id.chat_right_message);
            holder1.timeText = (TextView) convertView.findViewById(R.id.chat_right_time);
            holder1.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.chat_right_pic);
        }
        else {
            holder2 = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chat_view_left, null);
            holder2.messageText = (TextView) convertView.findViewById(R.id.chat_left_message);
            holder2.timeText = (TextView) convertView.findViewById(R.id.chat_left_time);
            holder2.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.chat_left_pic);
        }
        if (position != 0) {
            JudgeTime judgeTime = new JudgeTime(messageBean.getTime(),1);
            judgeTime.setLastTime(messageBeans.get(position-1).getTime());
            time = judgeTime.returnTime();
        }
        else time = messageBeans.get(position).getTime();
        if (otherOrSelf) {
            holder1.messageText.setText(messageBean.getMessage());
            if (time != null) holder1.timeText.setText(time);
            else holder1.timeText.setVisibility(View.GONE);
            if (selfAvatar != null) uri = Uri.parse(selfAvatar);
            holder1.simpleDraweeView.setImageURI(uri);
        }
        else {
            holder2.messageText.setText(messageBean.getMessage());
            if (time != null) holder2.timeText.setText(time);
            else holder2.timeText.setVisibility(View.GONE);
            if (friendAvatar != null) uri = Uri.parse(friendAvatar);
            holder2.simpleDraweeView.setImageURI(uri);
        }
        return convertView;
    }

    static class ViewHolder{
        public TextView messageText;
        public TextView timeText;
        public SimpleDraweeView simpleDraweeView;
    }

}
