package com.example.samsung.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends ActionBarActivity {

    private String rAccount,rPassword,rPasswordSure;
    private EditText rEtPassword,rEtPasswordSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText r_et_account = (EditText) findViewById(R.id.r_et_account);
        rAccount = r_et_account.getText().toString();

        rEtPassword = (EditText) findViewById(R.id.r_et_password);
        rEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        rEtPasswordSure = (EditText) findViewById(R.id.r_et_password_sure);
        rEtPasswordSure.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button btnSure = (Button) findViewById(R.id.b_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rPassword = rEtPassword.getText().toString();
                rPasswordSure = rEtPasswordSure.getText().toString();
                if (!rPassword.equals(rPasswordSure))
                    Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_LONG).show();
                else {
                    //send account and password to server
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
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
}
