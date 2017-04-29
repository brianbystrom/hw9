package com.example.brianbystrom.hw09;

import java.util.ArrayList;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class Trip {

    String uID, title, location, image, tID;
    ArrayList<Message> m;

    public Trip(String uID, String title, String location, String image, String tID, ArrayList<Message> m) {
        this.uID = uID;
        this.title = title;
        this.location = location;
        this.image = image;
        this.tID = tID;
        this.m = m;
    }

    public ArrayList<Message> getM() {
        return m;
    }

    public void setM(ArrayList<Message> m) {
        this.m = m;
    }

    public Trip() {
        
    }

    public String gettID() {
        return tID;
    }

    public void settID(String tID) {
        this.tID = tID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
