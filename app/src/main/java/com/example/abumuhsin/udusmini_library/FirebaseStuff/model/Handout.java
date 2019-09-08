package com.example.abumuhsin.udusmini_library.FirebaseStuff.model;

import java.io.Serializable;

public class Handout implements Serializable {
    private String course_code;
    private String handout_title;
    private String poster;
    private String faculty;
    private boolean is_gst;
    private String department;
    private String student_level;
    private int no_of_pages;
    private String handout_url;
    private String handout_id;
    private String cover_url;
    private String cover_type;
    private boolean checked;
    private long no_of_likers;

    public Handout() {
    }

    public Handout(String course_code, String handout_title, String poster, String handout_id) {
        this.course_code = course_code;
        this.handout_title = handout_title;
        this.poster = poster;
        this.handout_id = handout_id;
    }

    public Handout(String course_code, String handout_title
            , String poster, String faculty
            , String department, String level,String cover_url,String cover_type
            , int no_of_pages) {
        this.course_code = course_code;
        this.handout_title = handout_title;
        this.poster = poster;
        this.faculty = faculty;
        this.department = department;
        this.student_level = level;
        this.cover_url = cover_url;
        this.cover_type = cover_type;
        this.no_of_pages = no_of_pages;
    }

    public void setNo_of_likers(long no_of_likers) {
        this.no_of_likers = no_of_likers;
    }

    public long getNo_of_likers() {
        return no_of_likers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Handout handout = (Handout) obj;
        return handout.handout_id.equals(this.handout_id);
    }

    public String getStudent_level() {
        return student_level;
    }

    public void setStudent_level(String student_level) {
        this.student_level = student_level;
    }

    public void setHandout_id(String handout_id) {
        this.handout_id = handout_id;
    }

    public String getHandout_id() {
        return handout_id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getHandout_title() {
        return handout_title;
    }

    public int getNo_of_pages() {
        return no_of_pages;
    }

    public void setNo_of_pages(int no_of_pages) {
        this.no_of_pages = no_of_pages;
    }

    public void setHandout_title(String handout_title) {
        this.handout_title = handout_title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public boolean isIs_gst() {
        return is_gst;
    }

    public void setIs_gst(boolean is_gst) {
        this.is_gst = is_gst;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHandout_url() {
        return handout_url;
    }

    public void setHandout_url(String handout_url) {
        this.handout_url = handout_url;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getCover_type() {
        return cover_type;
    }

    public void setCover_type(String cover_type) {
        this.cover_type = cover_type;
    }
}
