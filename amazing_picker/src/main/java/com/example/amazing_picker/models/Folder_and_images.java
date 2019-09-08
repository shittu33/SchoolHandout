package com.example.amazing_picker.models;

import java.util.ArrayList;

public class Folder_and_images {

    private String name;
    private ArrayList<String> images;

    public Folder_and_images(String name, ArrayList<String> images) {
        this.name = name;
        this.images= images;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getImages() {
        return images;
    }
}
