package com.example.samsung.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.samsung.myapplication.R.id.tv_signature;

public class AccountInformation extends ActionBarActivity{

    private TextView tvAccount,tvName,tvSex,tvAge,tvSignature;
    private String account,friendAccount,getSelfClient,getFriendClient,message,saveUrlString
            ,name,friendName,sex,signature;
    final static int self = 1,other = 0;
    private int CODE,age;
    private SimpleDraweeView draweeView;
    private Button btnAdd,btnBack;
    static String baseUrl = "http://119.29.186.49/wechatInterface/index.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information);

        init();
        getExtra();
    }

    private void init() {
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvSignature = (TextView) findViewById(tv_signature);
        draweeView = (SimpleDraweeView) findViewById(R.id.user_avatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.account_information_toolbar);
        setSupportActionBar(toolbar);
        btnAdd = (Button) findViewById(R.id.btn_sure_add);
        btnAdd.setOnClickListener(listener);
        btnBack = (Button) findViewById(R.id.bar_back);
        btnBack.setOnClickListener(listener);
    }

    private void getExtra() {
        Intent intent = getIntent();
        CODE = intent.getIntExtra("statusCode", 0);
        if (CODE==other) {
            name = intent.getStringExtra("name");
            btnAdd.setText("确认添加");
            account = intent.getStringExtra("account");
            friendAccount = intent.getStringExtra("friendAccount");
            getFriendAccountInformation();
        }
        if (CODE==self) {
            btnAdd.setText("修改个人资料");
            account = intent.getStringExtra("account");
            getSelfAccountInformation();
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_sure_add :
                    switch (CODE) {
                        case other : addFriendDialog(); break;
                        case self : break;
                    }
                    break;
                case R.id.bar_back : finish(); break;
            }
        }
    };

    private void getSelfAccountInformation() {
        AsyncHttpClient getInformationClient = new AsyncHttpClient();
        getSelfClient = baseUrl + getUserParam(account);
        getInformationClient.get(getSelfClient,handler);
    }

    private void getFriendAccountInformation() {
        AsyncHttpClient getInformationClient = new AsyncHttpClient();
        getFriendClient = baseUrl + getUserParam(friendAccount);
        getInformationClient.get(getFriendClient,handler);
    }

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            try {
                JSONObject jsonObject = response.getJSONObject(0);
                if (CODE==other) friendName = jsonObject.getString("name");
                age = jsonObject.getInt("age");
                sex = jsonObject.getString("sex");
                signature = jsonObject.getString("signature");
                tvAccount.append(jsonObject.getString("identification"));
                tvName.append(jsonObject.getString("name"));
                tvAge.append(age + "");
                tvSex.append(sex);
                tvSignature.append(signature);
                Uri uri = Uri.parse(jsonObject.getString("picUrl"));
                draweeView.setImageURI(uri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private String getUserParam(String account){
        return "table=users&method=get&identification=" + account;
    }

    private void addFriendDialog(){
        LayoutInflater dialogInflater = LayoutInflater.from(this);
        final View dialogView = dialogInflater.inflate(R.layout.alert_dialog_view,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInformation.this);
        builder.setMessage("请求添加"+friendAccount+"为好友");
        builder.setView(dialogView);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText dialogRequestMessage = (EditText) dialogView.findViewById(R.id.friend_request_message);
                message = dialogRequestMessage.getText().toString();
                saveRequestToServer();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void saveRequestToServer(){
        saveUrlString = baseUrl + "table=friendList&method=save&data=" + friendRequest();
        AsyncHttpClient saveHttpClient = new AsyncHttpClient();
        saveHttpClient.get(saveUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), "发送请求成功，等待对方确认", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "对方账号不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String friendRequest(){
        return "{%22friendRequest%22:%22"+account+"%22,%22friendResponse%22:%22"+friendAccount
                +"%22,%22sign%22:%220%22,%22message%22:%22"+message+"%22,%22friendRequestName%22:%22"+name+
                "%22,%22friendResponseName%22:%22" +friendName+"%22}";
    }
}
