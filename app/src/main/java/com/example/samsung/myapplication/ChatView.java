package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChatView extends Activity {

    private Button b_back,b_send;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        //back to friendlist button
        b_back = (Button) findViewById(R.id.btn_back);
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatView.this,FriendList.class));
            }
        });

        EditText et_message = (EditText) findViewById(R.id.et_sendmessage);
        message = et_message.getText().toString();

        b_send = (Button) findViewById(R.id.btn_send);
        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server
            }
        });
    }
}
