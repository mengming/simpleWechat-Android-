package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by SAMSUNG on 2016/1/29.
 */
public class ChatView extends ActionBarActivity {

    private Button b_back,b_send;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        b_back = (Button) findViewById(R.id.btn_back);
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatView.this,FriendList.class));
            }
        });

        EditText message = (EditText) findViewById(R.id.et_sendmessage);

        b_send = (Button) findViewById(R.id.btn_send);
        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
