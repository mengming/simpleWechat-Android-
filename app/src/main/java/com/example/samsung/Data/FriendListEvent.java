package com.example.samsung.Data;

import org.json.JSONArray;

public class FriendListEvent {

    public JSONArray unsignedArray,latestMessagesArray;

    public FriendListEvent(JSONArray unsignedArray,JSONArray latestMessagesArray) {
        // TODO Auto-generated constructor stub
        this.unsignedArray = unsignedArray;
        this.latestMessagesArray = latestMessagesArray;
    }
}
