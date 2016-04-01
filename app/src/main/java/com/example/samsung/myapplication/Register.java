package com.example.samsung.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class Register extends ActionBarActivity {

    private String rAccount,rPassword,rPasswordSure,rName,rSignature,rAge,rSex,avatarUrl;
    static String baseUrl = "http://115.159.156.241/wechatinterface/index.php?";
    private EditText rEtAccount,rEtPassword,rEtPasswordSure,rEtName,rEtSignature,rEtAge;
    private ImageButton male,female;
    private Button btnSure,btnReturn,btnAvator;
    private int condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.register);

        initView();
    }

    private void initView() {
        rEtAccount = (EditText) findViewById(R.id.r_et_account);
        rEtPassword = (EditText) findViewById(R.id.r_et_password);
        rEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        rEtPasswordSure = (EditText) findViewById(R.id.r_et_password_sure);
        rEtPasswordSure.setTransformationMethod(PasswordTransformationMethod.getInstance());
        rEtAge = (EditText) findViewById(R.id.r_et_age);
        rEtName = (EditText) findViewById(R.id.r_et_name);
        rEtSignature = (EditText) findViewById(R.id.r_et_signature);

        btnSure = (Button) findViewById(R.id.b_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccountInformation();
                condition = 0;
                if (rAccount.length()>20 || rAccount.length()<6) condition = 1;
                else if (!rPassword.equals(rPasswordSure)) condition = 2;
                else if (rPassword.length()>20 || rPassword.length()<6) condition = 3;
                else if (rSex == null) condition = 4;
                if (rName.length()==0) rName = rAccount;
                if (rSignature.length()==0) rSignature = "无";
                if (rAge.length()==0) rAge = "0";
                switch (condition) {
                    case 1:Toast.makeText(getApplicationContext(), "账号长度应在6~20之间", Toast.LENGTH_SHORT).show();break;
                    case 2:Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_SHORT).show();break;
                    case 3:Toast.makeText(getApplicationContext(),"密码长度应在6~20之间",Toast.LENGTH_SHORT).show();break;
                    case 4:Toast.makeText(getApplicationContext(), "请选择性别", Toast.LENGTH_SHORT).show();break;
                    case 0:{//send account and password to server
                        saveInformationToServer();
                        startActivity(new Intent(Register.this, Login.class));
                        break;
                    }
                }
            }
        });

        btnReturn = (Button) findViewById(R.id.b_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        male = (ImageButton) findViewById(R.id.img_btn_boy);
        male.setOnClickListener(imageButtonListener);

        female = (ImageButton) findViewById(R.id.img_btn_girl);
        female.setOnClickListener(imageButtonListener);
    }

    private View.OnClickListener imageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_btn_boy : rSex = "男";
                    male.setBackgroundResource(R.drawable.avator_boy);
                    female.setBackgroundResource(R.drawable.avator_girl_unselect);
                    break;
                case R.id.img_btn_girl : rSex = "女";
                    male.setBackgroundResource(R.drawable.avator_boy_unselect);
                    female.setBackgroundResource(R.drawable.avator_girl);
                    break;
            }
        }
    };

//    private void setBtnAvator() {
//        btnAvator = (Button) findViewById(R.id.btn_avatar);
//        btnAvator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setAvatorSelect();
//            }
//        });
//    }

    private void saveInformationToServer(){
        String saveUrlString = baseUrl + "table=users&method=save&data="+accountInformation();
        AsyncHttpClient saveHttpClient = new AsyncHttpClient();
        saveHttpClient.get(saveUrlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
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

    private String accountInformation(){
        String result = new String();
        result = "{%22Identification%22:%22"+rAccount+"%22,%22Password%22:%22"+rPassword
            +"%22,%22Name%22:%22"+rName+"%22,%22Signature%22:%22"+rSignature+
                "%22,%22Age%22:%22"+rAge+"%22,%22Sex%22:%22"+rSex+
                "%22,%22picUrl%22:%22"+avatarUrl+"%22}";
        return result;
    }

//    private void setAvatorSelect(){
//        Intent intent = new Intent();
//        /* 开启Pictures画面Type设定为image */
//        intent.setType("image/*");
//        /* 使用Intent.ACTION_GET_CONTENT这个Action */
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        /* 取得相片后返回本画面 */
//        startActivityForResult(intent, 1);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            avatarUrl = data.getDataString();
//            Uri uri = Uri.parse(avatarUrl);
//            SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.user_avatar_select);
//            draweeView.setImageURI(uri);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
