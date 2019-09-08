package com.example.mysqlitedbconnection.csv.sqlite.Models;

import java.util.ArrayList;

public class Pages_table_model extends Book_table_model {
    private int page_no;
    private int layout_type;
    private int picture_count;
    private String pic_path;
    private ArrayList<String> pictures;
    private String single_picutre;

    public Pages_table_model() {
    }

    public Pages_table_model(String book_name, int page_no, int layout_type, int picture_count) {
        super(book_name);
        this.page_no = page_no;
        this.layout_type = layout_type;
        this.picture_count = picture_count;
    }

    public Pages_table_model(String book_name, int page_no, int layout_type, int picture_count, String single_picutre) {
        super(book_name);
        this.page_no = page_no;
        this.layout_type = layout_type;
        this.picture_count = picture_count;
        this.single_picutre = single_picutre;
    }

    public Pages_table_model(String book_name, int page_no, int layout_type, int picture_count, ArrayList<String> pictures) {
        super(book_name);
        this.page_no = page_no;
        this.layout_type = layout_type;
        this.picture_count = picture_count;
        this.pictures = pictures;
    }

    public Pages_table_model(int page_no, int layout_type) {
        this.page_no = page_no;
        this.layout_type = layout_type;
    }

    public Pages_table_model(int page_no, String book_title) {
        super(book_title);
        this.page_no = page_no;
    }

    public void setSingle_picutre(String single_picutre) {
        this.single_picutre = single_picutre;
    }

    public String getSingle_picutre() {
        return single_picutre;
    }

    public int getPicture_count() {
        return picture_count;
    }

    public void setPicture_count(int picture_count) {
        this.picture_count = picture_count;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public int getLayout_type() {
        return layout_type;
    }

    public void setLayout_type(int layout_type) {
        this.layout_type = layout_type;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }
}
