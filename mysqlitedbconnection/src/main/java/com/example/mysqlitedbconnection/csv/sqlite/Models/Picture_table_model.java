package com.example.mysqlitedbconnection.csv.sqlite.Models;

public class Picture_table_model extends Pages_table_model implements Scalable{
    String picture_path;

    public Picture_table_model(){

    }
    public Picture_table_model(int page_no, String book_title, String picture_path) {
        super(page_no,book_title);
        this.picture_path = picture_path;
    }

    public String getPicture_path() {
        return picture_path;
    }

    @Override
    public String getBook_name() {
        return super.getBook_name();
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    @Override
    public float getFocus_X() {
        return 0;
    }

    @Override
    public void setFocus_X(float x) {

    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public void setY(float y) {

    }

    @Override
    public float scale_factor() {
        return 0;
    }

    @Override
    public void scale_factor(float z) {

    }

    @Override
    public int getScale_type() {
        return 0;
    }

    @Override
    public int setScale_type() {
        return 0;
    }
}
