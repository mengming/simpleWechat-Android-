package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends ActionBarActivity {

    private String r_account,r_password,r_password_sure;
    private EditText r_et_password,r_et_password_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText r_et_account = (EditText) findViewById(R.id.r_et_account);
        r_account = r_et_account.getText().toString();

        r_et_password = (EditText) findViewById(R.id.r_et_password);
        r_et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        r_et_password_sure = (EditText) findViewById(R.id.r_et_password_sure);
        r_et_password_sure.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button b_sure = (Button) findViewById(R.id.b_sure);
        b_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r_password = r_et_password.getText().toString();
                r_password_sure = r_et_password_sure.getText().toString();
                if (!r_password.equals(r_password_sure))
                    Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_LONG).show();
                else {
                    //send account and password to server
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this,Login.class));
                }
            }
        });

        Button b_return = (Button) findViewById(R.id.b_return);
        b_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
    }
}
