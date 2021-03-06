package com.example.samsung.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.samsung.Adapter.ChatViewAdapter;
import com.example.samsung.Data.ChatViewEvent;
import com.example.samsung.Data.MessageBean;
import com.example.samsung.myapplication.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class ChatView extends Fragment {

    private PullToRefreshListView chatListView;
    private ArrayList<MessageBean> messageBeans,newMessageBeans;
    private ChatViewAdapter chatViewAdapter;
    private EditText etMessage;
    private Button btnSend;
    private String message,account,friendAccount,name,friendName,friendPhone,avatar,friendAvatar,
            sendMessageUrlString,getMessageUrlString;
    private String baseUrl = "http://119.29.186.49/wechatInterface/index.php?";
    private int count=10,positionStart,positionEnd,condition;
    static int CREATE=0;
    public Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_view, container, false);
        initView(view);
        initChatView(view);
        System.out.println("logonCreateView");
        return view;
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            messageBeans.clear();
            messageBeans.addAll(newMessageBeans);
            chatViewAdapter.notifyDataSetChanged();
            chatListView.getRefreshableView().setSelection(messageBeans.size() - 1);
            chatListView.onRefreshComplete();
            super.handleMessage(msg);
        }
    };

    public void onEventMainThread(ChatViewEvent event){
        newMessageBeans = event.messageBeans;
        handler.sendMessage(new Message());
    }

    private void timerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getMessageHistory();
                    }
                });
                thread.start();
                System.out.println("timer");
            }
        }, 0, 5000);
    }

    @Override
    public void onResume() {
        if (condition != CREATE) {
            timer = new Timer();
            timerTask();
        }
        super.onResume();
    }


    @Override
    public void onStop() {
        if (condition != CREATE) {
            timer.cancel();
            timer.purge();
        }
        super.onStop();
    }

    private void initView(View view) {
        etMessage =  (EditText) view.findViewById(R.id.et_sendmessage);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        System.out.println("loginitView");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message to server
                message = etMessage.getText().toString();
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
        System.out.println("loginitChatView");
        condition = bundle.getInt("condition");
        if (condition != CREATE) {
            account = bundle.getString("account");
            friendAccount = bundle.getString("friendAccount");
            avatar = bundle.getString("selfAvatar");
            friendAvatar = bundle.getString("friendAvatar");
            name = bundle.getString("name");
            friendName = bundle.getString("friendName");
            friendPhone = bundle.getString("friendPhone");
        }
        System.out.println("log"+account+"\n"+friendAccount);
        messageBeans = new ArrayList<>();
        chatViewAdapter = new ChatViewAdapter(getActivity(),messageBeans,account,friendAccount,avatar,friendAvatar);
        chatListView = (PullToRefreshListView) view.findViewById(R.id.chat_list);
        chatListView.getRefreshableView().setDivider(null);
        chatListView.setMode(PullToRefreshBase.Mode.BOTH);
        chatListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉加载更多记录");
        chatListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉加载");
        chatListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        chatListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以加载");
        chatListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉刷新最新消息");
        chatListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新");
        chatListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在刷新...");
        chatListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
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

    public void getMessageHistory(){
        newMessageBeans = new ArrayList<>();
        SyncHttpClient getMessageClient = new SyncHttpClient();
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
                EventBus.getDefault().post(new ChatViewEvent(newMessageBeans));
                handler.sendMessage(new Message());
            }
        });
    }

    private String messageSenderAndReceiver(){
        String result = new String();
        result = "{%22sender%22:%22" + account + "%22,%22receiver%22:%22" + friendAccount + "%22}";
        return result;
    }
}
