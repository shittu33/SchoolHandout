package com.example.abumuhsin.udusmini_library.models;

public class flip_model {
    int type;
    int pic_count;
    String image_path;

    public flip_model(int type, String image_path,int pic_count) {
        this.type = type;
        this.image_path = image_path;
        this.pic_count = pic_count;
    }

    public int getType() {
        return type;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getPic_count() {
        return pic_count;
    }

    public void setPic_count(int pic_count) {
        this.pic_count = pic_count;
    }
}
