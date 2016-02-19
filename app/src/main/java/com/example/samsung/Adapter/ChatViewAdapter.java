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
        ViewHolder holder = null;
        messageBean = messageBeans.get(position);
//        //if sender is self then otherOrSelf is true,else is false;
//        Boolean otherOrSelf = false;
//        if (messageBean.getSender().equals(account)) otherOrSelf = true;
//        if (otherOrSelf) System.out.println("true");
        if(convertView == null)
        {
            holder = new ViewHolder();
            /*if (otherOrSelf) */{
                convertView = mInflater.inflate(R.layout.chat_view_right, parent,false);
                holder.nameText = (TextView) convertView.findViewById(R.id.chat_right_name);
                holder.messageText = (TextView) convertView.findViewById(R.id.chat_right_name);
                holder.timeText = (TextView) convertView.findViewById(R.id.chat_right_time);
            }
//            else {
//                convertView = mInflater.inflate(R.layout.chat_view_left, parent,false);
//                holder.nameText = (TextView) convertView.findViewById(R.id.chat_left_name);
//                holder.messageText = (TextView) convertView.findViewById(R.id.chat_left_message);
//                holder.timeText = (TextView) convertView.findViewById(R.id.chat_left_time);
//            }
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.messageText.setText(messageBean.getMessage());
        holder.timeText.setText(messageBean.getTime());
        /*if (otherOrSelf) holder.nameText.setText(account);
        else */holder.nameText.setText(friendAccount);
        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
    }

}
