package com.devonngames.myjournal.model;

import com.google.firebase.Timestamp;

public class Journal {
    private  String title;
    private  String thoughts;
    private  String imageUrl;

    private  String userId;
    private Timestamp timeAdded;
    private  String userName;

    public Journal() {
    }

    public Journal(String title, String thoughts, String imageUrl, String userId, Timestamp timeAdded, String userName) {
        this.title = title;
        this.thoughts = thoughts;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
    }
    //Getters

    public String getTitle() {
        return title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
