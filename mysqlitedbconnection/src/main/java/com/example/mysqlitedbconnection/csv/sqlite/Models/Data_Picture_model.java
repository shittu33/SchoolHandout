package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 23/08/2018.
 */

public class Data_Picture_model implements Scalable {
    private String path;
    private int rotate;
    private int x;
    private int y;
    private int z;

    public Data_Picture_model(String path, int rotate) {
        this.path = path;
        this.rotate = rotate;
    }

    public String getPath() {
        return path;
    }

    public int getRotate() {
        return rotate;
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
