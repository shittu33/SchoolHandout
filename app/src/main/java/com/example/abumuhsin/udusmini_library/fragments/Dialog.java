package com.example.abumuhsin.udusmini_library.fragments;

import com.example.abumuhsin.udusmini_library.adapters.Message;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.List;

public class Dialog implements IDialog {
    String handout_id;
    String handout_photo;
    String handout_title;
    String unread_count;
    private IMessage message;
    private List<? extends IUser> users;

    public Dialog() {
    }

    public Dialog(String handout_id, String handout_photo, String handout_title, String unread_count, Message message) {
        this.handout_id = handout_id;
        this.handout_photo = handout_photo;
        this.handout_title = handout_title;
        this.unread_count = unread_count;
        this.message = message;
    }
    public Dialog(String handout_id, String handout_photo, String handout_title) {
        this.handout_id = handout_id;
        this.handout_photo = handout_photo;
        this.handout_title = handout_title;
    }

    public void setHandout_id(String handout_id) {
        this.handout_id = handout_id;
    }

    public void setHandout_photo(String handout_photo) {
        this.handout_photo = handout_photo;
    }

    public void setHandout_title(String handout_title) {
        this.handout_title = handout_title;
    }

    public void setUnread_count(String unread_count) {
        this.unread_count = unread_count;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public void setUsers(List<? extends IUser> users) {
        this.users = users;
    }


    @Override
    public String getId() {
        return handout_id;
    }

    @Override
    public String getDialogPhoto() {
        return handout_photo;
    }

    @Override
    public String getDialogName() {
        return handout_title;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    @Override
    public IMessage getLastMessage() {
        return message;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.message = message;
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
