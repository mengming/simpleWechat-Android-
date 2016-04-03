package com.example.samsung.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.samsung.Data.FriendListEvent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class FriendListService extends Service {

    static String baseUrl = "http://115.159.156.241/wechatinterface/index.php?";
    private String account;
    private String getLatestMessagesUrlString,getUnsignedUrlString;
    private JSONArray unsignedArray,latestMessagesArray;

    @Override
    public IBinder onBind(Intent intent) {
        account = intent.getStringExtra("account");
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        new MyServerThread().start();
        return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
    }

    class MyServerThread extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    getFriendListUnsigned();
                    getLatestMessages();
                    System.out.println("service");
                    MyServerThread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
}

    private void getLatestMessages(){
        AsyncHttpClient getLatestMessagesClient = new AsyncHttpClient();
        getLatestMessagesUrlString = baseUrl + getLatestMessagesParam();
        getLatestMessagesClient.get(getLatestMessagesUrlString,getLatestMessagesHandler);
    }

    private String getLatestMessagesParam(){
        return "method=getLatestMessages&identification=" + account;
    }

    JsonHttpResponseHandler getLatestMessagesHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            latestMessagesArray = response;
            EventBus.getDefault().post(new FriendListEvent(unsignedArray,latestMessagesArray));
        }
    };

    private void getFriendListUnsigned(){
        AsyncHttpClient getFriendListUnsignedClient = new AsyncHttpClient();
        getUnsignedUrlString = baseUrl + getFriendListParam();
        getFriendListUnsignedClient.get(getUnsignedUrlString,unsignedHandler);
    }

    JsonHttpResponseHandler unsignedHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            unsignedArray = response;
        }
    };

    private String getFriendListParam(){
        return "table=friendList&method=get&identification=" + account;
    }
}
