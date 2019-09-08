package com.example.amazing_picker.models;

public class Images_model {
    private String path;

    public Images_model(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        Images_model images_model_compare = (Images_model) obj;

        if(images_model_compare.getPath().equals(this.getPath()))
            return true;

        return false;
    }
}
