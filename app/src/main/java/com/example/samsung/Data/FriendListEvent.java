package com.example.samsung.Data;

import com.example.samsung.myapplication.FriendList;

import org.json.JSONArray;

import java.util.ArrayList;

public class FriendListEvent {

    public JSONArray unsignedArray,latestMessagesArray;

    public FriendListEvent(JSONArray unsignedArray,JSONArray latestMessagesArray) {
        // TODO Auto-generated constructor stub
        this.unsignedArray = unsignedArray;
        this.latestMessagesArray = latestMessagesArray;
    }
}
