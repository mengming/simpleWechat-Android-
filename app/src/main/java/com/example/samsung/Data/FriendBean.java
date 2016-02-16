package com.example.samsung.Data;

public class FriendBean {

    private int ID,Sign;
    private String Friendrequest,Friendresponse;
    private String Message;
    private String Time;

    public int getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public String getFriendrequest(){
        return Friendrequest;
    }

    public void setFriendrequest(String Friendrequest){
        this.Friendrequest = Friendrequest;
    }

    public String getMessage(){
        return Message;
    }

    public void setMessage(String message){
        this.Message = message;
    }

    public String getTime(){
        return Time;
    }

    public void setTime(String Time){
        this.Time = Time;
    }

    public String getFriendresponse(){
        return Friendresponse;
    }

    public void setFriendresponse(String Friendresponse){
        this.Friendresponse = Friendresponse;
    }

    public int getSign(){
        return Sign;
    }

    public void setSign(int Sign){
        this.Sign = Sign;
    }

    @Override
    public String toString() {
        return "FriendBean{" + ID + '\''+
                Friendrequest + '\'' +
                Friendresponse + '\'' +
                Sign + '\'' +
                Message + '\'' +
                Time + '\'' +
                '}';
    }
}
