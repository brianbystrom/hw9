package com.example.brianbystrom.hw09;

/**
 * Created by brianbystrom on 4/18/17.
 */

public class User {

    String fName, lName, gender, profileURL;

    

    public User(String fName, String lName, String gender, String profileURL) {
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.profileURL = profileURL;
    }
    
    public User() {
        
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }
}
