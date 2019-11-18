package com.example.abumuhsin.udusmini_library.firebaseStuff.model;

public class DiscussData {
    String handout_title;
    String last_message;
    long time_stamp;
    String handout_url;
    String last_sender_url;
    String last_sender_name;
    String last_message_id;

    public DiscussData() {
    }

    public String getHandout_title() {
        return handout_title;
    }

    public void setHandout_title(String handout_title) {
        this.handout_title = handout_title;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getHandout_url() {
        return handout_url;
    }

    public void setHandout_url(String handout_url) {
        this.handout_url = handout_url;
    }

    public String getLast_sender_url() {
        return last_sender_url;
    }

    public void setLast_sender_url(String last_sender_url) {
        this.last_sender_url = last_sender_url;
    }

    public DiscussData(String handout_title, long time_stamp, String handout_url) {
        this.handout_title = handout_title;
        this.time_stamp = time_stamp;
        this.handout_url = handout_url;
    }
}
