package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 23/08/2018.
 */

public interface Scalable {

    float getFocus_X();

    void setFocus_X(float x);

    float getY();

    void setY(float y);

    float scale_factor();

    void scale_factor(float z);

    int getScale_type();

    int setScale_type();
}
