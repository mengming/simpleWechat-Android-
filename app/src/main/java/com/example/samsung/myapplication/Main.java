package com.example.samsung.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.Adapter.ViewPagerAdapter;
import com.example.samsung.Fragment.ChatView;
import com.example.samsung.Fragment.Community;
import com.example.samsung.Fragment.FriendList;
import com.example.samsung.Service.FriendListService;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class Main extends ActionBarActivity {

    final private int FRIEND_LIST = 0, COMMUNITY = 2, CHAT_VIEW = 1;
    private Button btnFriend,btnChat,btnMenu,btnCommunity;
    private String account,friendAccount,name,selfAvatar;
    private String items[] = {"添加好友","个人资料","退出登录"};
    private Intent intent;
    private ArrayList<Fragment> fragments = null;
    private ViewPager viewPager = null;
    private ViewPagerAdapter viewPagerAdapter = null;
    private TextView tvFriendName,phone,appName;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private Fragment friendFragment,chatFragment,communityFragment;
    private ListPopupWindow listPopupWindow;
    private int chatViewCondition = 0,nowFragment = 0;
    private boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.main);

        getExtra();
        initView();
        initMenu();
        initService();
    }

    private void initService() {
        intent = new Intent(this,FriendListService.class);
        intent.putExtra("account", account);
        startService(intent);
        isBound = bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        appName = (TextView) findViewById(R.id.tv_app);
        btnMenu = (Button) findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(listener);
        tvFriendName = (TextView) findViewById(R.id.friend_name);
        phone = (TextView) findViewById(R.id.tv_phone);
        tvFriendName.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        fragments = new ArrayList<>();
        friendFragment = new FriendList();
        EventBus.getDefault().register(friendFragment);
        Bundle accountData = new Bundle();
        accountData.putString("account", account);
        accountData.putString("name", name);
        friendFragment.setArguments(accountData);
        fragments.add(friendFragment);
        chatFragment = new ChatView();
        Bundle chatBundle = new Bundle();
        chatBundle.putInt("condition", 1);
        chatFragment.setArguments(chatBundle);
        fragments.add(chatFragment);
        communityFragment = new Community();
        fragments.add(communityFragment);
        viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(pageListener);
        btnFriend = (Button) findViewById(R.id.btn_friend);
        btnFriend.setOnClickListener(listener);
        btnFriend.setEnabled(false);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(listener);
        btnCommunity = (Button) findViewById(R.id.btn_community);
        btnCommunity.setOnClickListener(listener);
    }

    private void getExtra(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        selfAvatar = intent.getStringExtra("avatarUrl");
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        stopService(intent);
        EventBus.getDefault().unregister(friendFragment);
    }

    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            btnChange(position);
            switch (position) {
                case FRIEND_LIST :
                    if (chatViewCondition == 1) {
                        EventBus.getDefault().unregister(chatFragment);
                        chatFragment.onStop();
                    }
                    break;
                case COMMUNITY : break;
                case CHAT_VIEW :
                    if (chatViewCondition == 1 && nowFragment != 2) {
                        EventBus.getDefault().register(chatFragment);
                    }
                    break;
            }
            nowFragment = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void btnChange(int index){
        btnChat.setEnabled(true);
        btnChat.setBackgroundResource(R.drawable.chatview_unselect);
        btnFriend.setEnabled(true);
        btnFriend.setBackgroundResource(R.drawable.friendlist_unselect);
        btnCommunity.setEnabled(true);
        btnCommunity.setBackgroundResource(R.drawable.community_unselect);
        switch (index) {
            case FRIEND_LIST : btnFriend.setEnabled(false);
                btnFriend.setBackgroundResource(R.drawable.friendlist);
                tvFriendName.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.INVISIBLE);
                break;
            case CHAT_VIEW : btnChat.setEnabled(false);
                btnChat.setBackgroundResource(R.drawable.chatview);
                tvFriendName.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                break;
            case COMMUNITY : btnCommunity.setEnabled(false);
                btnCommunity.setBackgroundResource(R.drawable.community);
                tvFriendName.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_chat:
                    if (fragments.size()==1) Toast.makeText(getApplicationContext(),"请先选择一个好友喔",Toast.LENGTH_SHORT).show();
                    else viewPager.setCurrentItem(CHAT_VIEW); break;
                case R.id.btn_friend: viewPager.setCurrentItem(FRIEND_LIST); break;
                case R.id.btn_menu:
                    listPopupWindow.setAnchorView(v);
                    listPopupWindow.show(); break;
                case R.id.btn_community: viewPager.setCurrentItem(COMMUNITY); break;
            }
        }
    };

    private void initMenu() {
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this, R.layout.popup_item, items));
        listPopupWindow.setWidth(280);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(0xFF2980b9));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: checkFriendDialog(); break;
                    case 1: checkSelfInformation(); break;
                    case 2: logout(); break;
                }
            }
        });
    }

    public void openChat(String friendAccount,String friendAvatar,String friendName,String friendPhone){
        chatFragment = new ChatView();
        chatViewCondition = 1;
        Bundle chatBundle = new Bundle();
        chatBundle.putInt("condition",1);
        chatBundle.putString("friendAccount", friendAccount);
        chatBundle.putString("account", account);
        chatBundle.putString("name", name);
        chatBundle.putString("friendName", friendName);
        chatBundle.putString("selfAvatar", selfAvatar);
        chatBundle.putString("friendAvatar",friendAvatar);
        chatBundle.putString("friendPhone",friendPhone);
        tvFriendName.setText(friendName);
        phone.setText(friendPhone);
        chatFragment.setArguments(chatBundle);
        fragments.set(1,chatFragment);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setCurrentItem(CHAT_VIEW);
        viewPager.setOffscreenPageLimit(2);
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
                friendInformationIntent.putExtra("name",name);
                startActivity(friendInformationIntent);
                dialog.dismiss();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }

    private void logout() {
        sharedPreferences = getSharedPreferences("login",Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        startActivity(new Intent(this,Login.class));
        finish();
    }

    private void initNotification(String friendAccount,String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.default_avatar;
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