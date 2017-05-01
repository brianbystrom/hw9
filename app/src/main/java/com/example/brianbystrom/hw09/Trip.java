package com.example.brianbystrom.hw09;

import java.util.ArrayList;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class Trip {

    String uID, title, image, tID;
    Location location;
    ArrayList<Message> m;
    ArrayList<Location> l;

    public Trip(String uID, String title, Location location, String image, String tID, ArrayList<Message> m, ArrayList<Location> l) {
        this.uID = uID;
        this.title = title;
        this.location = location;
        this.image = image;
        this.tID = tID;
        this.m = m;
        this.l = l;
    }

    public ArrayList<Message> getM() {
        return m;
    }

    public void setM(ArrayList<Message> m) {
        this.m = m;
    }

    public Trip() {
        
    }

    public ArrayList<Location> getL() {
        return l;
    }

    public void setL(ArrayList<Location> l) {
        this.l = l;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
