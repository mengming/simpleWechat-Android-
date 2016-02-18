package com.example.samsung.Data;

public class FriendBean {

    private int ID,sign;
    private String friendRequest,friendResponse,friendRequestName,friendResponseName;
    private String message;
    private String time;

    public int getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public String getFriendRequest(){
        return friendRequest;
    }

    public void setFriendRequest(String friendRequest){
        this.friendRequest = friendRequest;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getFriendResponse(){
        return friendResponse;
    }

    public void setFriendResponse(String friendResponse){
        this.friendResponse = friendResponse;
    }

    public int getSign(){
        return sign;
    }

    public void setSign(int sign){
        this.sign = sign;
    }

    public String getFriendRequestName(){
        return friendRequestName;
    }

    public void setFriendRequestName(String friendRequestName){
        this.friendRequestName = friendRequestName;
    }

    public String getFriendResponseName(){
        return friendResponseName;
    }

    public void setFriendResponseName(String friendResponseName){
        this.friendResponseName = friendResponseName;
    }

    @Override
    public String toString() {
        return "FriendBean{" + ID + '\''+
                friendRequest + '\'' +
                friendResponse + '\'' +
                sign + '\'' +
                message + '\'' +
                time + '\'' +
                friendRequestName + '\'' +
                friendResponseName + '\'' +
                '}';
    }
}
