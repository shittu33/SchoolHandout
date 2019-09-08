package com.example.mysqlitedbconnection.csv.sqlite.Models;

import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 21/08/2018.
 */

public class Scrap_Data_model {
    private int layout;
    private ArrayList<String> pictures;
    private ArrayList<String> texts;


    public Scrap_Data_model(int layout, ArrayList<String> pictures, ArrayList<String> texts) {
        this.layout = layout;
        this.pictures = pictures;
        this.texts = texts;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }
}
