package com.example.brianbystrom.hw09;

import java.util.ArrayList;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class Message {

    String uID, tID, time, msg;
    //boolean you;


    public Message(String uID, String tID, String time, String msg) {
        this.uID = uID;
        this.tID = tID;
        this.time = time;
        this.msg = msg;
        //this.you = you;
    }


    public Message() {

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String gettID() {
        return tID;
    }

    public void settID(String tID) {
        this.tID = tID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
