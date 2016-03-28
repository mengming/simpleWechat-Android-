package com.example.samsung.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.samsung.Fragment.ChatView;
import com.example.samsung.Fragment.FriendList;
import com.example.samsung.Service.FriendListService;
import com.facebook.drawee.backends.pipeline.Fresco;

public class Main extends ActionBarActivity {

    static int FRIEND_LIST = 0;
    static int CHAT_VIEW = 1;
    private int nowFragment = 0;
    private Button btnFriend,btnChat;
    private String account,friendAccount,name;
    private boolean isCreate;
    private Intent intent;
    private Fragment[] fragments = new Fragment[2];
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.main);
        getExtra();
        initMenu();

        System.out.println("onCreateF");

        isCreate = true;
        intent = new Intent(this,FriendListService.class);
        intent.putExtra("account", account);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void getExtra(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
    }

    private void initMenu() {
        fragmentManager = getFragmentManager();
        btnFriend = (Button) findViewById(R.id.btn_friend);
        btnFriend.setOnClickListener(new menuListener());
        btnFriend.setEnabled(false);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new menuListener());
        fragmentTransaction = fragmentManager.beginTransaction();
        fragments[1] = new ChatView();
        Bundle bundle1 = new Bundle();
        account = "abc";
        bundle1.putString("account", account);
        friendAccount = "321";
        bundle1.putString("friendAccount",friendAccount);
        fragments[1].setArguments(bundle1);
        fragmentTransaction.replace(R.id.fragment_container, fragments[1]);
        fragments[0] = new FriendList();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("name", name);
        fragments[0].setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fragments[0]);
        fragmentTransaction.commit();
    }

    class menuListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (v.getId()) {
                case R.id.btn_friend:
                    btnChat.setEnabled(true);
                    btnFriend.setEnabled(false);
                    fragmentTransaction.replace(R.id.fragment_container, fragments[0]).commit();
                    nowFragment = FRIEND_LIST;
                    break;
                case R.id.btn_chat:
                    if (fragments[1]==null)
                        Toast.makeText(getApplicationContext(),"请选择聊天对象哦",Toast.LENGTH_SHORT).show();
                    else {
                        btnChat.setEnabled(false);
                        btnFriend.setEnabled(true);
                        fragments[1] = new ChatView();
                        fragmentTransaction.replace(R.id.fragment_container, fragments[1]);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        nowFragment = CHAT_VIEW;
                    }
                    break;
                default:break;
            }
        }
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
        unbindService(connection);
        System.out.println("onDestroyF");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionbar_add_friend: 
                checkFriendDialog();
                break;
            case R.id.actionbar_logout:
                logout();
                break;
            case R.id.actionbar_self_information:
                checkSelfInformation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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