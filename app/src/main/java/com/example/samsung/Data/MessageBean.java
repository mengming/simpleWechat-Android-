package com.example.samsung.Data;

public class MessageBean {

    private int ID;
    private String message,time,receiver,sender,receiverName,senderName;

    public void setID(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public void setReceiver(String receiver){
        this.receiver = receiver;
    }

    public String getReceiver(){
        return receiver;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

//    public void setReceiverName(String receiverName) {
//        this.receiverName = receiverName;
//    }
//
//    public String getReceiverName(){
//        return receiverName;
//    }
//
//    public void setSenderName(String senderName) {
//        this.senderName = senderName;
//    }
//
//    public String getSenderName(){
//        return senderName;
//    }

    @Override
    public String toString(){
        String result = "MessageBean{" + ID + "/"
                + message + "/"
                + time + "/"
                + receiver + "/"
                + sender + "}";
        return result;
    }
}
