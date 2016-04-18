package com.example.samsung.Data;

public class FriendBean {

    private int ID,sign;
    private String friendRequest,friendResponse,message,time,sender,receiver,friendPic,friendName,friendPhone;
    private boolean judgeNew;

    public boolean getJudgeNew(){
        return judgeNew;
    }

    public void setJudgeNew(boolean judgeNew){
        this.judgeNew = judgeNew;
    }

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

    public String getFriendName(){
        return friendName;
    }

    public void setFriendName(String friendName){
        this.friendName = friendName;
    }

    public String getSender(){
        return sender;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public void setReceiver(String receiver){
        this.receiver = receiver;
    }

    public String getFriendPic(){
        return friendPic;
    }

    public void setFriendPic(String friendPic){
        this.friendPic = friendPic;
    }



    @Override
    public String toString() {
        return "friendBean{" + ID + "/"
                + sign + "/"
                + friendRequest + "/"
                + friendResponse+ "/"
                + message+ "/"
                + time + "/"
                + sender + "/"
                + receiver + "}";
    }
}
