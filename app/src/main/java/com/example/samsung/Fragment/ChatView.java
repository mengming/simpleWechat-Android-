package com.example.samsung.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.samsung.Adapter.ChatViewAdapter;
import com.example.samsung.Data.MessageBean;
import com.example.samsung.myapplication.R;
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

public class ChatView extends Fragment {

    private PullToRefreshListView chatListView;
    private ArrayList<MessageBean> messageBeans,newMessageBeans;
    private ChatViewAdapter chatViewAdapter;
    private EditText etMessage;
    private Button btnSend;
    private String message,account,friendAccount,sendMessageUrlString,getMessageUrlString;
    private String baseUrl = "http://115.159.156.241/wechatinterface/index.php?";
    private int count=10,positionStart,positionEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_view, container, false);
        initView(view);
        initChatView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        etMessage =  (EditText) view.findViewById(R.id.et_sendmessage);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server
                message = etMessage.getText().toString();
                System.out.println(message);
                sendMessageUrlString = baseUrl + "table=messageList&method=save&data=" + sendMessageData();
                AsyncHttpClient sendMessageHttpClient = new AsyncHttpClient();
                sendMessageHttpClient.get(sendMessageUrlString, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        etMessage.setText("");
                        getMessageHistory();
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

    private void initChatView(View view){
        Bundle bundle = getArguments();
        account = bundle.getString("account");
        friendAccount = bundle.getString("friendAccount");
        messageBeans = new ArrayList<>();
        chatViewAdapter = new ChatViewAdapter(getActivity(),messageBeans,account,friendAccount);
        chatListView = (PullToRefreshListView) view.findViewById(R.id.chat_list);
        chatListView.getRefreshableView().setDivider(null);
        chatListView.setMode(PullToRefreshBase.Mode.BOTH);
        chatListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        chatListView.setAdapter(chatViewAdapter);
        chatListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                count += 10;
                getMessageHistory();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMessageHistory();
            }
        });
    }

    private void getMessageHistory(){
        newMessageBeans = new ArrayList<>();
        AsyncHttpClient getMessageClient = new AsyncHttpClient();
        getMessageUrlString = baseUrl + "table=messageList&method=get&data="+ messageSenderAndReceiver();
        getMessageClient.get(getMessageUrlString,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                positionEnd = response.length();
                positionStart = positionEnd-count;
                if (positionStart<0) positionStart=0;
                for (int i=positionStart;i<positionEnd;i++) {
                    try {
                        JSONObject messageJsonObject = response.getJSONObject(i);
                        Gson gson = new Gson();
                        MessageBean messageBean = gson.fromJson(messageJsonObject.toString(),MessageBean.class);
                        newMessageBeans.add(messageBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                messageBeans.clear();
                messageBeans.addAll(newMessageBeans);
                chatViewAdapter.notifyDataSetChanged();
                chatListView.getRefreshableView().setSelection(messageBeans.size() - 1);
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
