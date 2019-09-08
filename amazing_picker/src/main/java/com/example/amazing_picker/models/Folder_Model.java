package com.example.amazing_picker.models;

public class Folder_Model {

    private String name, id,first_image;

    public Folder_Model(String name, String id, String first_image) {
        this.name = name;
        this.id = id;
        this.first_image= first_image;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getFirst_image() {
        return first_image;
    }

}
