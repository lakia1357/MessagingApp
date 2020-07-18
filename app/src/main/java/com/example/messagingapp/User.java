package com.example.messagingapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {

    String userID;
    String userPassword;
    String userName;
    String userDepartment;
    Bitmap userImage;

    public User(String userID, String userPassword, String userName, String userDepartment, Bitmap userImage) {
            this.userID = userID;
            this.userPassword = userPassword;
            this.userName = userName;
            this.userDepartment = userDepartment;
            this.userImage = userImage;
    }

    public String getUserID(){
        return userID;
    }
    public String getUserPassword(){
        return userPassword;
    }
    public String getUserName(){
        return userName;
    }
    public String getUserDepartment(){
        return userDepartment;
    }
    public Bitmap getUserImage(){
        return userImage;
    }

}
