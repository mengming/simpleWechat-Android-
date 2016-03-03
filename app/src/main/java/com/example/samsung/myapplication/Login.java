package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class Login extends ActionBarActivity {

    private String account,password,rightPassword,name,getUserClient;
    static String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
    private EditText etAccount,etPassword;
    private Button btnLogin,btnRegister;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void login(){
        Intent friendListIntent = new Intent();
        friendListIntent.setClass(Login.this, FriendList.class);
        friendListIntent.putExtra("account", account);
        friendListIntent.putExtra("name",name);
        startActivity(friendListIntent);
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
                password = etPassword.getText().toString();
//                account="ceshi";password="123456";
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
        });
    }

    private String getUserParam(){
        return "table=users&method=get&identification=" + account;
    }

    private void saveAccount(){
        SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password",password);
        editor.putString("name",name);
        editor.commit();
    }

    private void readAccount(){
        SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        account = sharedPreferences.getString("account","");
        password = sharedPreferences.getString("password","");
        name = sharedPreferences.getString("name","");
        if (account.length()!=0 && password.length()!=0) login();
    }
}
