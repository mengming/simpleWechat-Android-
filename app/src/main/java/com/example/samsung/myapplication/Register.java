package com.example.samsung.myapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class Register extends ActionBarActivity {

    private String rAccount,rPassword,rPasswordSure,rName,rSignature,rAge,rSex,rPhone,avatarUrl;
    static String baseUrl = "http://119.29.186.49/wechatInterface/index.php?",
        uploadPicUrl = "http://119.29.186.49/wechatInterface/lib/upload.func.php?identification=";
    private EditText rEtAccount,rEtPassword,rEtPasswordSure,rEtName,rEtSignature,rEtAge;
    private Button male,female;
    private Button btnSure,btnReturn,btnAvator,btnPullMore;
    private ScrollView scrollView;
    private int condition;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.register);

        initView();
        setBtnAvator();
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccounts();
        for (Account account:accounts) {
            if (account.type.equals("com.osp.app.signin")) {
                rAccount = account.name;
                rEtAccount.setVisibility(View.INVISIBLE);
                TextView defaultAccount = (TextView) findViewById(R.id.tv_account_default);
                defaultAccount.setText(rAccount);
            }
        }
        if (rAccount==null) Toast.makeText(getApplicationContext(),"请先申请三星账号",Toast.LENGTH_SHORT).show();
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
        btnPullMore = (Button) findViewById(R.id.btn_pull_more);
        btnPullMore.setOnClickListener(listener);
        male = (Button) findViewById(R.id.btn_male);
        female = (Button) findViewById(R.id.btn_female);
        male.setOnClickListener(listener);
        female.setOnClickListener(listener);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int scrollY = v.getScrollY();
                        int height = v.getHeight();
                        int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
                        if ((scrollY + height) == scrollViewMeasuredHeight)
                            btnPullMore.setText("已到达底部");
                        else btnPullMore.setText("下拉更多");
                        break;
                }
                return false;
            }
        });
        TextView phoneText = (TextView) findViewById(R.id.r_t_phone);
        phoneText.append(Build.MODEL);
        btnSure = (Button) findViewById(R.id.b_sure);
        btnSure.setOnClickListener(listener);
        btnReturn = (Button) findViewById(R.id.b_return);
        btnReturn.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_return:
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                    break;
                case R.id.btn_pull_more:
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            btnPullMore.setText("已到达底部");
                        }
                    });
                    break;
                case R.id.btn_male: rSex = "男";
                    male.setBackgroundResource(R.drawable.avator_boy);
                    female.setBackgroundResource(R.drawable.avator_girl_unselect);
                    break;
                case R.id.btn_female: rSex = "女";
                    male.setBackgroundResource(R.drawable.avator_boy_unselect);
                    female.setBackgroundResource(R.drawable.avator_girl);
                    break;
                case R.id.b_sure:
                    getAccountInformation();
                    condition = 0;
                    if (rAccount.length()>20 || rAccount.length()<6) condition = 1;
                    else if (!rPassword.equals(rPasswordSure)) condition = 2;
                    else if (rPassword.length()>20 || rPassword.length()<6) condition = 3;
                    else if (rSex == null) condition = 4;
                    else if (avatarUrl == null) condition = 5;
                    if (rName.length()==0) rName = rAccount;
                    if (rSignature.length()==0) rSignature = "无";
                    if (rAge.length()==0) rAge = "0";
                    switch (condition) {
                        case 1:Toast.makeText(getApplicationContext(), "账号长度应在6~20之间", Toast.LENGTH_SHORT).show();break;
                        case 2:Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_SHORT).show();break;
                        case 3:Toast.makeText(getApplicationContext(),"密码长度应在6~20之间",Toast.LENGTH_SHORT).show();break;
                        case 4:Toast.makeText(getApplicationContext(), "请选择性别", Toast.LENGTH_SHORT).show();break;
                        case 5:Toast.makeText(getApplicationContext(), "请选择头像", Toast.LENGTH_SHORT).show();break;
                        case 0:{//send account and password to server
                            saveInformationToServer();
                            startActivity(new Intent(Register.this, Login.class));
                            finish();
                            break;
                        }
                    }
                    break;
            }
        }
    };

    private void setBtnAvator() {
        btnAvator = (Button) findViewById(R.id.btn_avatar);
        btnAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatorSelect();
            }
        });
    }

    private void saveInformationToServer(){
        String saveUrlString = baseUrl + "table=users&method=save&data="+accountInformation();
        AsyncHttpClient saveHttpClient = new AsyncHttpClient();
        System.out.println(saveUrlString);
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
        sendPic();
    }

    private void getAccountInformation(){
        rAccount = rEtAccount.getText().toString();
        rName = rEtName.getText().toString();
        rSignature = rEtSignature.getText().toString();
        rAge = rEtAge.getText().toString();
        rPassword = rEtPassword.getText().toString();
        rPasswordSure = rEtPasswordSure.getText().toString();
        rPhone = Build.MODEL;
    }

    private String accountInformation(){
        String result = new String();
        result = "{%22identification%22:%22"+rAccount+"%22,%22password%22:%22"+rPassword
            +"%22,%22name%22:%22"+rName+"%22,%22signature%22:%22"+rSignature+
                "%22,%22age%22:%22"+rAge+"%22,%22sex%22:%22"+rSex+"%22,%22phone%22:%22"+rPhone+"%22}";
        return result;
    }

    //选择相片界面
    private void setAvatorSelect(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    //返回相片地址
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            avatarUrl = data.getDataString();
            System.out.println(avatarUrl);
            btnAvator.setText("");
            SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.user_avatar_select);
            Uri uri = Uri.parse(avatarUrl);
            file = new File(getRealFilePath(this,uri));
            if (file.length()>2097152)
                Toast.makeText(getApplicationContext(),"图片大小应不大于2M",Toast.LENGTH_SHORT).show();
            else draweeView.setImageURI(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //照片相对路径转换为绝对路径
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void sendPic(){
        AsyncHttpClient upload = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("file",file);
            System.out.println("图片存在");
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
        }
        upload.post(uploadPicUrl+rAccount, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("failure");
            }
        });
    }
}
