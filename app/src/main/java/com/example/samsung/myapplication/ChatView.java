package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.samsung.Adapter.ChatViewAdapter;

import org.json.JSONObject;

public class ChatView extends Activity {

    private Button btnBack,btnSend;
    private String message;
    private JSONObject messageJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        //back to friendlist button
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatView.this, FriendList.class));
            }
        });

        EditText etMessage = (EditText) findViewById(R.id.et_sendmessage);
        message = etMessage.getText().toString();

        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server
            }
        });

        ListView chat_list = (ListView) findViewById(R.id.chat_list);
        ChatViewAdapter chat_list_adapter = new ChatViewAdapter();
        chat_list.setAdapter(chat_list_adapter);

    }

}
