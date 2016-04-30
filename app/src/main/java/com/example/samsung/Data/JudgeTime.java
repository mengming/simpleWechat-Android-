package com.example.samsung.Data;

import java.util.Calendar;

public class JudgeTime {

    public int[] times = new int[6],messageTimes,lastMessageTimes;
    public int condition,FRIEND = 0,CHAT = 1;
    public String time[],day[],second[],lastTime,messageTime;

    public JudgeTime(String messageTime,int condition){
        this.condition = condition;
        this.messageTime = messageTime;
        messageTimes = new int[6];
        Calendar calendar = Calendar.getInstance();
        times[0] = calendar.get(Calendar.YEAR);
        times[1] = calendar.get(Calendar.MONTH);
        times[2] = calendar.get(Calendar.DAY_OF_MONTH);
        times[3] = calendar.get(Calendar.HOUR);
        times[4] = calendar.get(Calendar.MINUTE);
        times[5] = calendar.get(Calendar.SECOND);
        time = messageTime.split(" ");
        day = time[0].split("-");
        second = time[1].split(":");
        for (int i=0;i<3;i++) {
            messageTimes[i] = Integer.valueOf(day[i]);
            messageTimes[3+i] = Integer.valueOf(second[i]);
        }
    }

    public String returnTime(){
        if (condition == FRIEND) {
            if (times[0] == messageTimes[0]) {
                if (times[2] == messageTimes[2]) return time[1];
                else if ((times[2] - messageTimes[2]) == 1) return "昨天";
                else if ((times[2] - messageTimes[2]) < 7) return times[2] - messageTimes[2] + "天前";
                else return addZero(messageTimes[1]) + "-" + addZero(messageTimes[2]);
            } else return addZero(messageTimes[0]) + "-" + addZero(messageTimes[1]) + "-" + addZero(messageTimes[2]);
        }
        else if (condition == CHAT) {
            lastMessageTimes = new int[6];
            time = lastTime.split(" ");
            day = time[0].split("-");
            second = time[1].split(":");
            for (int i=0;i<3;i++) {
                lastMessageTimes[i] = Integer.valueOf(day[i]);
                lastMessageTimes[3+i] = Integer.valueOf(second[i]);
            }
            int i = 0;
            while (i < 5 && lastMessageTimes[i] == messageTimes[i]) i++;
            if (i == 0) return messageTime;
            else if (i < 3) return addZero(messageTimes[1]) + "-" + addZero(messageTimes[2]);
            else if (i < 4) return addZero(messageTimes[3]) + ":" + addZero(messageTimes[4]);
            else if (i == 4) {
                if ((messageTimes[4]-lastMessageTimes[4]) < 3) return null;
                else return addZero(messageTimes[3]) + ":" + addZero(messageTimes[4]);
            }
            else return null;
        }
        else return "ERROR";
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    private String addZero(int a){
        if (a < 0) return "ERROR";
        else if (a < 10) return "0"+a;
        else return Integer.toString(a);
    }

}
