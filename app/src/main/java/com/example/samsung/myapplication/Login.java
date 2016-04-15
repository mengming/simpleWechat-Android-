package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;


public class Login extends ActionBarActivity {

    private String account,password,rightPassword,name,sex,getUserClient;
    static String baseUrl = "http://119.29.186.49/wechatInterface/index.php?";
    private EditText etAccount,etPassword;
    private Button btnLogin,btnRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        readAccount();
        setEtAccount();
        setEtPassword();
        setBtnRegister();
        setBtnLogin();
    }

    private void login(){
        Intent friendListIntent = new Intent();
        friendListIntent.setClass(Login.this, Main.class);
        friendListIntent.putExtra("account", account);
        friendListIntent.putExtra("name", name);
        friendListIntent.putExtra("sex",sex);
        startActivity(friendListIntent);
        finish();
    }

    private void setEtAccount(){
        etAccount = (EditText) findViewById(R.id.et_account);
    }

    private void setEtPassword(){
        etPassword = (EditText) findViewById(R.id.et_password);
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void setBtnRegister(){
        btnRegister = (Button) findViewById(R.id.b_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void setBtnLogin(){
        btnLogin = (Button) findViewById(R.id.b_login);
        rightPassword = new String();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = etAccount.getText().toString();
                if (account.length() == 0)
                    Toast.makeText(getApplicationContext(), "账号不能为空", Toast.LENGTH_SHORT).show();
                else {
                    password = etPassword.getText().toString();
                    if (password.length() == 0)
                        Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    else {
                        AsyncHttpClient client = new AsyncHttpClient();
                        getUserClient = baseUrl + getUserParam();
                        client.get(getUserClient, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    int length = response.length();
                                    for (int i = 0; i < length; i++) {
                                        rightPassword = response.getJSONObject(i).getString("password");
                                        name = response.getJSONObject(i).getString("name");
                                        sex = response.getJSONObject(i).getString("sex");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (rightPassword.equals(password)) {
                                    rightPassword = null;
                                    saveAccount();
                                    login();
                                } else
                                    Toast.makeText(getApplicationContext(), "密码不正确", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Toast.makeText(getApplicationContext(), "账号不存在", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private String getUserParam(){
        return "table=users&method=get&identification=" + account;
    }

    private void saveAccount(){
        sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password",password);
        editor.putString("name",name);
        editor.putString("sex",sex);
        editor.commit();
    }

    private void readAccount(){
        sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        account = sharedPreferences.getString("account","");
        password = sharedPreferences.getString("password","");
        name = sharedPreferences.getString("name","");
        sex = sharedPreferences.getString("sex","");
        if (account.length()!=0 && password.length()!=0) login();
    }
}
