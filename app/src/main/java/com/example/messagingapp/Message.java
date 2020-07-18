package com.example.messagingapp;

import java.io.Serializable;

public class Message implements Serializable {

    String userID, receiveID, mailTitle, mailContents, date;

    public Message(String userID, String receiveID, String mailTitle, String mailContents, String date) {
        this.userID = userID;
        this.receiveID = receiveID;
        this.mailTitle = mailTitle;
        this.mailContents = mailContents;
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public String getReceiveID() {
        return receiveID;
    }

    public String getMailTitle() {
        return mailTitle;
    }

    public String getMailContents() {
        return mailContents;
    }

    public String getDate() {
        return date;
    }


}
