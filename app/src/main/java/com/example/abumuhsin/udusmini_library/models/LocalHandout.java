package com.example.abumuhsin.udusmini_library.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Comparator;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

public class LocalHandout implements Comparator,Comparable<LocalHandout> {
    private String title;
    private String cover;
    private int page_no;
    private String course_code;
    private boolean checked;
    private String level;
    private String cover_type;

    public LocalHandout() {

    }

    public LocalHandout(String title, String course_code, String cover, int page_no) {
        this.title = title;
        this.course_code = course_code;
        this.cover = cover;
        this.page_no = page_no;
    }

    public String getCover_type() {
        return cover_type;
    }

    public void setCover_type(String cover_type) {
        this.cover_type = cover_type;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int compare(Object o, Object t1) {
        return 0;
    }

    @Override
    public boolean equals(@Nullable Object image_title_model) {
        LocalHandout localHandout1 = (LocalHandout) image_title_model;
        if (image_title_model != null) {
            return this.title.equals(localHandout1.title);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull LocalHandout localHandout) {
        return this.getTitle().compareTo(localHandout.getTitle());
    }
}
