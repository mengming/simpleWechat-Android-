package com.example.samsung.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private String account,friendAccount,getUserClient,message,saveUrlString;
    private Button btnAdd;
    static String baseUrl = "http://8.sundoge.applinzi.com/index.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information);

        init();
        getExtra();
        getAccountInformation();
    }

    private void init() {
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvSignature = (TextView) findViewById(tv_signature);
        setBtnAdd();
    }

    private void getExtra() {
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        friendAccount = intent.getStringExtra("friendAccount");
    }

    private void getAccountInformation() {
        AsyncHttpClient getInformationClient = new AsyncHttpClient();
        getUserClient = baseUrl + getUserParam();
        getInformationClient.get(getUserClient, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    tvAccount.setText("账号:" + jsonObject.getString("identification"));
                    tvName.setText("昵称:" + jsonObject.getString("name"));
                    tvAge.setText("年龄:" + jsonObject.getInt("age"));
                    tvSex.setText("性别:" + jsonObject.getString("sex"));
                    tvSignature.setText("个性签名:" + jsonObject.getString("signature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void setBtnAdd(){
        btnAdd = (Button) findViewById(R.id.btn_sure_add);
        btnAdd.setText("确定添加");
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog();
            }
        });
    }

    private String getUserParam(){
        return "table=users&method=get&identification=" + friendAccount;
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
                +"%22,%22sign%22:%220%22,%22message%22:%22"+message+"%22}";
    }
}
