package com.example.amazing_picker.models;

import java.io.Serializable;

public class Selectable_image implements Serializable {
    private boolean selected;
    private String selectable_path;

    public Selectable_image(String path, boolean selected) {
        this.selected = selected;
        selectable_path = path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void set_selected(boolean is_selected) {
        this.selected = is_selected;
    }

    public String getSelectable_path() {
        return selectable_path;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        Selectable_image selectable_image_comp = (Selectable_image) obj;
        return selectable_image_comp.getSelectable_path().equals(this.getSelectable_path());

    }
}
