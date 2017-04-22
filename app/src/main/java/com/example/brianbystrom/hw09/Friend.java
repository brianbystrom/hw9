package com.example.brianbystrom.hw09;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class Friend {

    String user;
    String friend;


    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }




    public Friend(String user, String friend) {
        this.user = user;
        this.friend = friend;
    }

    public Friend() {
        
    }


}
