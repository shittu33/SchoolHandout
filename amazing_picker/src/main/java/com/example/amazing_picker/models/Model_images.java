package com.example.amazing_picker.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Model_images implements Serializable {

    String str_folder;
    ArrayList<String> al_imagepath;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null) {
            return ((Model_images) obj).str_folder.equals(this.str_folder);
        }
        return false;
    }

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }
}
