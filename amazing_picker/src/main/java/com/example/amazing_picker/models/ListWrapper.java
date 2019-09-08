package com.example.amazing_picker.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ListWrapper implements Serializable {

    private ArrayList<Model_images> model_images;

    public ListWrapper(ArrayList<Model_images> data) {
        this.model_images = data;
    }

    public ArrayList<Model_images> getModel_images() {
        return this.model_images;
    }
}
