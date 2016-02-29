package com.example.samsung.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.samsung.Data.FriendBean;
import com.example.samsung.Data.FriendListEvent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class FriendListService extends Service {

    private final IBinder mBinder = new RemoteBinder();
    private boolean threadContinue = true;
    private String account,friendListUnsignedUrl,friendListSignedUrl;
    static String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
    private ArrayList<FriendBean> friendBeans;
    Thread friendListThread;

    public FriendListService (String account) {
        this.account = account;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        friendListUnsignedUrl = baseUrl + "table=friendList&method=get&identification=" + account;
        friendListSignedUrl = baseUrl + "method=getLatestMessages&identification=" + account;
        friendListThread = new Thread(){
            @Override
            public void run() {
//                AsyncHttpClient friendListUnsignedClient = new AsyncHttpClient();
//                AsyncHttpClient friendListSignedClient = new AsyncHttpClient();
//                friendBeans = new ArrayList<>();
                while (threadContinue) {
                    EventBus.getDefault().post("hello");
//                    friendListUnsignedClient.get(friendListUnsignedUrl, unsignedFriendHandler);
//                    friendListSignedClient.get(friendListSignedUrl, signedFriendHandler);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        friendListThread.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class RemoteBinder extends Binder {
        public FriendListService getService() {
            // 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性
            return FriendListService.this;
        }
    }

    private JsonHttpResponseHandler unsignedFriendHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject friendJsonObject = response.getJSONObject(i);
                    if (friendJsonObject.getInt("sign") == 0) {
                        Gson gson = new Gson();
                        FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                        friendBeans.add(friendBean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private JsonHttpResponseHandler signedFriendHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject friendJsonObject = response.getJSONObject(i);
                    Gson gson = new Gson();
                    FriendBean friendBean = gson.fromJson(friendJsonObject.toString(), FriendBean.class);
                    friendBean.setSign(1);
                    friendBeans.add(friendBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            EventBus.getDefault().post(new FriendListEvent(friendBeans));
        }
    };
}
