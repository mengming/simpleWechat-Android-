package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samsung.Adapter.ChatViewAdapter;
import com.example.samsung.Data.MessageBean;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ChatView extends Activity {

    private PullToRefreshListView chatListView;
    private ArrayList<MessageBean> messageBeans;
    private ChatViewAdapter chatViewAdapter;
    private EditText etMessage;
    private TextView nameText;
    private Button btnBack,btnSend;
    private String message,account,friendAccount,sendMessageUrlString,getMessageUrlString;
    private String baseUrl = "http://8.sundoge.applinzi.com/index.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        getExtra();
        setNameText();
        setEtMessage();
        setBtnBack();
        setBtnSend();
        initChatView();
    }

    private void setNameText(){
        nameText = (TextView) findViewById(R.id.chat_name);
        nameText.setText(friendAccount);
    }

    private void setEtMessage(){
        etMessage = (EditText) findViewById(R.id.et_sendmessage);
    }

    private void getExtra(){
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        friendAccount = intent.getStringExtra("friendAccount");
    }

    private void setBtnBack(){
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatView.this, FriendList.class));
            }
        });
    }

    private void setBtnSend(){
        if (TextUtils.isEmpty(etMessage.getText()))
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server
                message = etMessage.getText().toString();
                if (message=="") finish();
                sendMessageUrlString = baseUrl + "table=messageList&method=save&data=" + sendMessageData();
                AsyncHttpClient sendMessageHttpClient = new AsyncHttpClient();
                sendMessageHttpClient.get(sendMessageUrlString, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        etMessage.setText("");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        etMessage.setText("");
                    }
                });
            }
        });
    }

    private String sendMessageData(){
        String result = new String();
        result = "{%22sender%22:%22" + account + "%22,%22receiver%22:%22" + friendAccount +
                "%22,%22message%22:%22" + message + "%22}";
        return result;
    }

    private void initChatView(){
        chatListView = (PullToRefreshListView) findViewById(R.id.chat_list);
        chatListView.getRefreshableView().setDivider(null);
        chatListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        chatListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        chatListView.setAdapter(chatViewAdapter);
        chatListView.setDividerPadding(5);
        chatListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMessageHistory();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        getMessageHistory();
    }

    private void getMessageHistory(){
        messageBeans = new ArrayList<>();
        AsyncHttpClient getMessageClient = new AsyncHttpClient();
        getMessageUrlString = baseUrl + "table=messageList&method=get&data="+ messageSenderAndReceiver();
        getMessageClient.get(getMessageUrlString,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JSONObject messageJsonObject = response.getJSONObject(i);
                        Gson gson = new Gson();
                        MessageBean messageBean = gson.fromJson(messageJsonObject.toString(),MessageBean.class);
                        messageBeans.add(messageBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                chatViewAdapter = new ChatViewAdapter(ChatView.this,messageBeans,account,friendAccount);
                chatListView.setAdapter(chatViewAdapter);
                chatListView.onRefreshComplete();
            }
        });
    }

    private String messageSenderAndReceiver(){
        String result = new String();
        result = "{%22sender%22:%22" + account + "%22,%22receiver%22:%22" + friendAccount + "%22}";
        return result;
    }
}
