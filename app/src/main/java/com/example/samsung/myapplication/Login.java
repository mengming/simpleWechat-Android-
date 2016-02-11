package com.example.samsung.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class Login extends ActionBarActivity {

    private String account,password,rightPassword,name;
    private EditText etAccount,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //get account and password
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //enter the register interface
        Button btnRegister = (Button) findViewById(R.id.b_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        //enter the friendlist interface
        Button btnLogin = (Button) findViewById(R.id.b_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = etAccount.getText().toString();
                password = etPassword.getText().toString();
                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://8.sundoge.applinzi.com/index.php?table=users&method=get&data={%22Identification%22:%22"
                        +account+"%22}",new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            rightPassword = response.getString("Password");
                            name = response.getString("Name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (rightPassword.equals(password)) {
                            Intent FriendList = new Intent();
                            FriendList.setClass(Login.this,FriendList.class);
                            FriendList.putExtra("account", account);
                            FriendList.putExtra("name",name);
                            startActivity(FriendList);
                        }
                        else Toast.makeText(getApplicationContext(),"密码不正确",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
}
