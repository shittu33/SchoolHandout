package com.example.abumuhsin.udusmini_library.firebaseStuff.model;

import java.io.Serializable;

public class RegisteredStudent implements Serializable {
    private String adm_no;
    private String student_imge_path;
    private String email;
    private String surname;
    private String full_name;
    private String student_uid;
    private String student_department;
    private String student_faculty;
    private String student_level;
    private String phone_no;

    public RegisteredStudent(){

    }
    public RegisteredStudent(String adm_no, String email, String uid) {
        this.adm_no = adm_no;
        this.email = email;
        this.student_uid = uid;
    }

    public RegisteredStudent(String adm_no, String email,String surname, String full_name,
                             String student_uid, String student_department, String student_faculty,
                             String student_level, String phone_no) {
        this.adm_no = adm_no;
        this.email = email;
        this.surname = surname;
        this.full_name = full_name;
        this.student_uid = student_uid;
        this.student_department = student_department;
        this.student_faculty = student_faculty;
        this.student_level = student_level;
        this.phone_no = phone_no;
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

    public String getStudent_uid() {
        return student_uid;
    }

    public void setStudent_uid(String student_uid) {
        this.student_uid = student_uid;
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

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
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

    public String getStudent_imge_path() {
        return student_imge_path;
    }

    public void setStudent_imge_path(String student_imge_path) {
        this.student_imge_path = student_imge_path;
    }
}
