package com.example.abumuhsin.udusmini_library.firebaseStuff.model;

public class HandoutFilter {
    private String department;
    private String level;
    private String course_code;
    private String handout_id;

    public HandoutFilter(String department, String level, String course_code, String handout_id) {
        this.department = department;
        this.level = level;
        this.course_code = course_code;
        this.handout_id = handout_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getHandout_id() {
        return handout_id;
    }

    public void setHandout_id(String handout_id) {
        this.handout_id = handout_id;
    }
}
