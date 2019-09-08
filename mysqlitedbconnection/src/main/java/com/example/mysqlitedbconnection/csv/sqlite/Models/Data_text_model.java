package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 23/08/2018.
 */

public class Data_text_model implements Scalable {
    private String text;
    private int rotate;
    private int title;
    public Data_text_model(String text, int rotate, int title) {
        this.text = text;
        this.rotate = rotate;
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
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
