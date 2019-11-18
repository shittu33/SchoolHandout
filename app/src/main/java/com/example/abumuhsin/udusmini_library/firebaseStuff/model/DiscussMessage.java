package com.example.abumuhsin.udusmini_library.firebaseStuff.model;

public class DiscussMessage {
    String sender_name;
    String message;
    long time_stamp;
    String sender_url;
    boolean is_url;
    String sender_id;

    public DiscussMessage() {
    }

    public DiscussMessage(String sender_id,String sender_name, String message, long time_stamp, boolean is_url) {
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.message = message;
        this.time_stamp = time_stamp;
        this.is_url = is_url;

    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getSender_url() {
        return sender_url;
    }

    public void setSender_url(String sender_url) {
        this.sender_url = sender_url;
    }

    public boolean isIs_url() {
        return is_url;
    }

    public void setIs_url(boolean is_url) {
        this.is_url = is_url;
    }
}
