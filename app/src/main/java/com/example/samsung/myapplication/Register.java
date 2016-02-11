package com.example.samsung.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class Register extends ActionBarActivity {

    private String rAccount,rPassword,rPasswordSure,rName,rSignature,rAge,rSex;
    private EditText rEtAccount,rEtPassword,rEtPasswordSure,rEtName,rEtSignature,rEtAge;
    private RadioButton male,female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        rEtAccount = (EditText) findViewById(R.id.r_et_account);
        rEtPassword = (EditText) findViewById(R.id.r_et_password);
        rEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        rEtPasswordSure = (EditText) findViewById(R.id.r_et_password_sure);
        rEtPasswordSure.setTransformationMethod(PasswordTransformationMethod.getInstance());
        rEtName = (EditText) findViewById(R.id.r_et_name);
        rEtSignature = (EditText) findViewById(R.id.r_et_signature);
        rEtAge = (EditText) findViewById(R.id.r_et_age);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
        sexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == male.getId()) rSex = "男";
                if (checkedId == female.getId()) rSex = "女";
            }
        });

        Button btnSure = (Button) findViewById(R.id.b_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccountInformation();
                if (!rPassword.equals(rPasswordSure))
                    Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_SHORT).show();
                else {
                    //send account and password to server
                    saveInformationToServer();
                    startActivity(new Intent(Register.this,Login.class));
                }
            }
        });

        Button btnReturn = (Button) findViewById(R.id.b_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });

    }

    private void saveInformationToServer(){
        String baseUrl = "http://8.sundoge.applinzi.com/index.php?";
        String saveUrlString = baseUrl + "table=users&method=save&data="+accoutInformation();
        AsyncHttpClient saveHttpClient = new AsyncHttpClient();
        saveHttpClient.get(saveUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAccountInformation(){
        rAccount = rEtAccount.getText().toString();
        rName = rEtName.getText().toString();
        rSignature = rEtSignature.getText().toString();
        rAge = rEtAge.getText().toString();
        rPassword = rEtPassword.getText().toString();
        rPasswordSure = rEtPasswordSure.getText().toString();
    }

    private String accoutInformation(){
        String result = new String();
        result = "{%22Identification%22:%22"+rAccount+"%22,%22Password%22:%22"+rPassword
            +"%22,%22Name%22:%22"+rName+"%22,%22Signature%22:%22"+rSignature+
                "%22,%22Age%22:%22"+rAge+"%22,%22Sex%22:%22"+rSex+"%22}";
        return result;
    }
}
