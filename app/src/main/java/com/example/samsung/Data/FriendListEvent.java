package com.example.samsung.Data;

import com.example.samsung.myapplication.FriendList;

import java.util.ArrayList;

public class FriendListEvent {
//    private ArrayList<FriendBean> friendBeans;
//
//    public FriendListEvent(ArrayList<FriendBean> friendBeans){
//        this.friendBeans = friendBeans;
//    }
//
//    public ArrayList<FriendBean> getFriendBeans(){
//        return friendBeans;
//    }

    private String mMsg;
    public FriendListEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }
}
