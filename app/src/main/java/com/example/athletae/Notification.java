package com.example.athletae;

import java.util.Date;

public class Notification {
    public String user_id, username;
    public Date timestamp;

    public Notification(){}

    public Notification(String user_id, String username, Date timestamp) {
        this.user_id = user_id;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUser_name(String user_name) {
        this.username = user_name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
