package com.example.abumuhsin.udusmini_library.models;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;

import java.io.Serializable;

public class OnlineHandout implements Serializable{
    private Handout handout;
    private boolean is_user_like;

    public OnlineHandout() {

    }

    public OnlineHandout(Handout handout, boolean is_user_like) {
        this.handout = handout;
        this.is_user_like = is_user_like;
    }

    public OnlineHandout(Handout handout) {
        this.handout = handout;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        OnlineHandout onlineHandout = (OnlineHandout) obj;
        return onlineHandout.handout.equals(this.handout);
    }

    public Handout getHandout() {
        return handout;
    }

    public void setHandout(Handout handout) {
        this.handout = handout;
    }

    public boolean isIs_user_like() {
        return is_user_like;
    }

    public void setIs_user_like(boolean is_user_like) {
        this.is_user_like = is_user_like;
    }

}
