package com.example.samsung.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.samsung.Data.MessageBean;
import com.example.samsung.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class ChatViewAdapter  extends BaseAdapter{

    private LayoutInflater mInflater;
    private MessageBean messageBean;
    private String account,friendAccount;
    public ArrayList<MessageBean> messageBeans;

    public ChatViewAdapter(Context context,ArrayList<MessageBean> messageBeans
            ,String account,String friendAccount){
        mInflater = LayoutInflater.from(context);
        this.messageBeans = messageBeans;
        this.account = account;
        this.friendAccount = friendAccount;
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
        Uri uri = Uri.parse("res:///"+R.drawable.chat_pic);
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
        if (otherOrSelf) {
            holder1.messageText.setText(messageBean.getMessage());
            holder1.timeText.setText(messageBean.getTime());
            holder1.simpleDraweeView.setImageURI(uri);
        }
        else {
            holder2.messageText.setText(messageBean.getMessage());
            holder2.timeText.setText(messageBean.getTime());
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
