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
    public ArrayList<MessageBean> messageBeans;

    public ChatViewAdapter(Context context,ArrayList<MessageBean> messageBeans){
        mInflater = LayoutInflater.from(context);
        this.messageBeans = messageBeans;
    }

    @Override
    public int getCount() {
        return 0;
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
            convertView = mInflater.inflate(R.layout.chat_view_left, null);
            holder.nameText = (TextView) convertView.findViewById(R.id.chat_left_name);
            holder.messageText = (TextView) convertView.findViewById(R.id.chat_left_message);
            holder.timeText = (TextView) convertView.findViewById(R.id.chat_left_time);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
//        holder.nameText.setText((String)data.get(position).get("name"));
//        holder.messageText.setText((String)data.get(position).get("message"));

        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;

    }

}
