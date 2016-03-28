package com.example.samsung;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.samsung.myapplication.R;

/**
 * Created by SAMSUNG on 2016/3/28.
 */
public class ActionbarLayout extends RelativeLayout {

    private TextView tvAppName,chatName;
    private ImageButton btnAdd,btnMore,btnSelf;

    public ActionbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.actionbar, this);
        tvAppName = (TextView) findViewById(R.id.tv_app_name);
        chatName = (TextView) findViewById(R.id.tv_name_actionbar);
        btnAdd = (ImageButton) findViewById(R.id.actionbar_add_friend);
        btnMore = (ImageButton) findViewById(R.id.actionbar_more);
        btnSelf = (ImageButton) findViewById(R.id.actionbar_self_information);
    }
}
