package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class Posting extends ActionBarActivity{

    private Button btnSend,btnBack;
    private String sendUrl,baseUrl = "http://119.29.186.49/wechatInterface/index.php?",account,name,avatarUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posting_edit);

        SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        account = sharedPreferences.getString("account","");
        name = sharedPreferences.getString("name","");
        avatarUrl = sharedPreferences.getString("avatarUrl","");
        btnSend = (Button) findViewById(R.id.btn_send_sure);
        final EditText editText = (EditText) findViewById(R.id.posting_edittext);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostings(editText.getText().toString());
            }
        });
        btnBack = (Button) findViewById(R.id.posting_edit_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendPostings(String message){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        sendUrl = baseUrl + "table=community&method=save&data={%22poster%22:%22" + account
                + "%22,%22posterName%22:%22" + name + "%22,%22message%22:%22" + message + "%22}";
        asyncHttpClient.get(sendUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"发帖成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
