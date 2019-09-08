package com.example.abumuhsin.udusmini_library.FirebaseStuff.model;

import java.io.Serializable;

public class Student implements Serializable {

    private String adm_no;
    private String student_image_path;
    private String email;
    private String surname;
    private String full_name;
    private String student_uid;
    private String student_department;
    private String student_faculty;
    private String student_level;
    private String phone_no;

    public Student() {

    }
    public Student(String adm_no, String email, String full_name,
                   String student_uid, String student_department,
                   String student_faculty, String student_level,String student_image_path) {
        this.adm_no = adm_no;
        this.email = email;
        this.full_name = full_name;
        this.student_uid = student_uid;
        this.student_department = student_department;
        this.student_faculty = student_faculty;
        this.student_level = student_level;
        this.student_image_path = student_image_path;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getStudent_department() {
        return student_department;
    }

    public void setStudent_department(String student_department) {
        this.student_department = student_department;
    }

    public String getStudent_faculty() {
        return student_faculty;
    }

    public void setStudent_faculty(String student_faculty) {
        this.student_faculty = student_faculty;
    }

    public String getStudent_level() {
        return student_level;
    }

    public void setStudent_level(String student_level) {
        this.student_level = student_level;
    }

    public void setStudent_uid(String student_uid) {
        this.student_uid = student_uid;
    }

    public String getStudent_uid() {
        return student_uid;
    }

    public String getAdm_no() {
        return adm_no;
    }

    public void setAdm_no(String adm_no) {
        this.adm_no = adm_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getStudent_image_path() {
        return student_image_path;
    }

    public void setStudent_image_path(String student_image_path) {
        this.student_image_path = student_image_path;
    }
}
