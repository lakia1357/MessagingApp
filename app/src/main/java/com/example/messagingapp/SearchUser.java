package com.example.messagingapp;

import android.widget.BaseAdapter;

public class SearchUser {
    String userID, userName;

    public SearchUser(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public String getSearchedID() { return userID; }

    public String getSearchedName() {
        return userName;
    }


}
