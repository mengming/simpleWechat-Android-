package com.example.samsung.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

    private String r_identification,r_password,r_password_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText r_et_identification = (EditText) findViewById(R.id.r_et_identification);
        r_identification = r_et_identification.getText().toString();
        EditText r_et_password = (EditText) findViewById(R.id.r_et_password);
        r_et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        r_password = r_et_password.getText().toString();
        EditText r_et_password_sure = (EditText) findViewById(R.id.r_et_password_sure);
        r_et_password_sure.setTransformationMethod(PasswordTransformationMethod.getInstance());
        r_password_sure = r_et_password_sure.getText().toString();

        Button b_sure = (Button) findViewById(R.id.b_sure);
        b_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!r_password.equals(r_password_sure))
                    Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_LONG).show();
                else {
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
