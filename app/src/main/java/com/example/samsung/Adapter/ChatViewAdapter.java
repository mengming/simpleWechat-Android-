package com.example.samsung.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.samsung.Data.MessageBean;
import com.example.samsung.myapplication.R;

import java.util.ArrayList;

public class ChatViewAdapter  extends BaseAdapter{

    private LayoutInflater mInflater;
    private MessageBean messageBean;
    private Context context;
    private String account,friendAccount;
    public ArrayList<MessageBean> messageBeans;
    private int count = 10;

    public ChatViewAdapter(Context context,ArrayList<MessageBean> messageBeans
            ,String account,String friendAccount){
        mInflater = LayoutInflater.from(context);
        this.messageBeans = messageBeans;
        this.account = account;
        this.friendAccount = friendAccount;
    }

    public void setCount(int count){
        this.count = count;
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
            holder1.nameText = (TextView) convertView.findViewById(R.id.chat_right_name);
            holder1.messageText = (TextView) convertView.findViewById(R.id.chat_right_message);
            holder1.timeText = (TextView) convertView.findViewById(R.id.chat_right_time);
        }
        else {
            holder2 = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chat_view_left, null);
            holder2.nameText = (TextView) convertView.findViewById(R.id.chat_left_name);
            holder2.messageText = (TextView) convertView.findViewById(R.id.chat_left_message);
            holder2.timeText = (TextView) convertView.findViewById(R.id.chat_left_time);
            convertView.setTag(holder2);
        }
        if (otherOrSelf) {
            holder1.messageText.setText(messageBean.getMessage());
            holder1.timeText.setText(messageBean.getTime());
            holder1.nameText.setText(account);
        }
        else {
            holder2.messageText.setText(messageBean.getMessage());
            holder2.timeText.setText(messageBean.getTime());
            holder2.nameText.setText(friendAccount);
        }
        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
    }

}
