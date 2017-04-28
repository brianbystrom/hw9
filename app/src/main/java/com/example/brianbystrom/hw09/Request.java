package com.example.brianbystrom.hw09;

import java.util.ArrayList;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class Request {

    String uID, fID, key;

    public Request(String uID, String fID, String key) {
        this.uID = uID;
        this.fID = fID;
        this.key = key;

    }

    public Request() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getfID() {
        return fID;
    }

    public void setfID(String fID) {
        this.fID = fID;
    }
}
