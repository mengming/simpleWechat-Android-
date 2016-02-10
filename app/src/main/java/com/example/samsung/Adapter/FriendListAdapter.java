package com.example.samsung.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.myapplication.R;

public class FriendListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private FriendListAdapter(Context context)
    {
        //根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 0;
//        data.size();
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
//        holder.imageView.setBackgroundResource((Integer)data.get(position).get("image"));
//        holder.nameText.setText((String)data.get(position).get("name"));
//        holder.messageText.setText((String)data.get(position).get("message"));

        return convertView;
    }

    static class ViewHolder{
        public TextView nameText;
        public TextView messageText;
        public TextView timeText;
        public ImageView imageView;
    }
}
