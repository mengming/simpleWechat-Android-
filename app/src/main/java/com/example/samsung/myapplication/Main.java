package com.example.samsung.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.samsung.Adapter.PagerAdapter;
import com.example.samsung.Fragment.ChatView;
import com.example.samsung.Fragment.FriendList;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

public class Main extends ActionBarActivity {

    final static int FRIEND_LIST = 0;
    final static int CHAT_VIEW = 1;
    private Button btnFriend,btnChat,btnAdd,btnLogout,btnSelf;
    private String account,friendAccount,name,sex;
    private boolean isCreate;
    private Intent intent;
    private ArrayList<Fragment> fragments = null;
    private ViewPager viewPager = null;
    private PagerAdapter pagerAdapter = null;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.main);
        getExtra();
        initView();
        System.out.println("onCreateF");

        isCreate = true;
//        intent = new Intent(this,FriendListService.class);
//        intent.putExtra("account", account);
//        startService(intent);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        btnAdd = (Button) findViewById(R.id.bar_add);
        btnAdd.setOnClickListener(new buttonListener());
        btnSelf = (Button) findViewById(R.id.bar_self);
        btnSelf.setOnClickListener(new buttonListener());
        btnLogout = (Button) findViewById(R.id.bar_logout);
        btnLogout.setOnClickListener(new buttonListener());
        fragments = new ArrayList<>();
        Fragment friendFragment = new FriendList();
        Bundle accountData = new Bundle();
        accountData.putString("account", account);
        friendFragment.setArguments(accountData);
        fragments.add(friendFragment);
        viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(pageListener);
        btnFriend = (Button) findViewById(R.id.btn_friend);
        btnFriend.setOnClickListener(new buttonListener());
        btnFriend.setEnabled(false);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new buttonListener());
    }

    private void getExtra(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        sex = intent.getStringExtra("sex");
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("onServiceDisconnected");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStartF");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResumeF");
//        if (!isCreate) getFriendList();
        isCreate = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStopF");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
        System.out.println("onDestroyF");
    }

    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case FRIEND_LIST : btnFriend.setEnabled(false);
                    btnChat.setEnabled(true);
                    break;
                case CHAT_VIEW : btnChat.setEnabled(false);
                    btnFriend.setEnabled(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bar_logout : logout(); break;
                case R.id.bar_self : checkSelfInformation(); break;
                case R.id.bar_add : checkFriendDialog(); break;
                case R.id.btn_chat :
                    if (fragments.size()==1) Toast.makeText(getApplicationContext(),"请先选择一个好友喔",Toast.LENGTH_SHORT).show();
                    else viewPager.setCurrentItem(CHAT_VIEW); break;
                case R.id.btn_friend : viewPager.setCurrentItem(FRIEND_LIST); break;
            }
        }
    }

    public void openChat(String friendAccount){
        Fragment chatFragment = new ChatView();
        Bundle chatBundle = new Bundle();
        chatBundle.putString("friendAccount",friendAccount);
        chatBundle.putString("account",account);
        chatFragment.setArguments(chatBundle);
        fragments.add(chatFragment);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setCurrentItem(CHAT_VIEW);
    }

    private void checkSelfInformation() {
        Intent selfInformationIntent = new Intent();
        selfInformationIntent.setClass(Main.this,AccountInformation.class);
        selfInformationIntent.putExtra("statusCode",1);
        selfInformationIntent.putExtra("account", account);
        startActivity(selfInformationIntent);
    }


    private void checkFriendDialog() {
        final EditText accountInput = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setMessage("请输入对方账号").setView(accountInput);
        builder.setPositiveButton("查找", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent friendInformationIntent = new Intent();
                friendAccount = accountInput.getText().toString();
                friendInformationIntent.setClass(Main.this, AccountInformation.class);
                friendInformationIntent.putExtra("statusCode", 0);
                friendInformationIntent.putExtra("friendAccount", friendAccount);
                friendInformationIntent.putExtra("account", account);
                startActivity(friendInformationIntent);
                dialog.dismiss();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }


    private void logout() {
        sharedPreferences = getSharedPreferences("login",Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        finish();
    }

    private void initNotification(String friendAccount,String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.chat_pic;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon)
                .setContentTitle(friendAccount)
                .setTicker(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setDefaults(Notification.FLAG_AUTO_CANCEL)
                .setContentIntent(getDefalutIntent(friendAccount))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    public PendingIntent getDefalutIntent(String friendAccount){
        Intent notificationIntent = new Intent(this,ChatView.class);
        notificationIntent.putExtra("account",account);
        notificationIntent.putExtra("friendAccount", friendAccount);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}