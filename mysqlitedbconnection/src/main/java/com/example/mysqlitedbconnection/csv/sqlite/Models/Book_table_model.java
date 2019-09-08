package com.example.mysqlitedbconnection.csv.sqlite.Models;

public class Book_table_model {
    String book_name;
    private String course_code;
    String book_type;
    String first_page;
    int no_of_pages;
    long time_stamp;

    public Book_table_model(){
    }

    public Book_table_model(String book_name, String book_type, String first_page, int no_of_pages, long time_stamp) {
        this.book_name = book_name;
        this.book_type = book_type;
        this.first_page = first_page;
        this.no_of_pages = no_of_pages;
        this.time_stamp = time_stamp;
    }

    public Book_table_model(String book_name,String course_code, String book_type,int no_of_pages, String first_page) {
        this.book_name = book_name;
        this.course_code = course_code;
        this.book_type = book_type;
        this.first_page = first_page;
        this.no_of_pages = no_of_pages;
    }

    public Book_table_model(String book_name, String book_type) {
        this.book_name = book_name;
        this.book_type = book_type;
    }
    public Book_table_model(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_name() {
        return book_name;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_type() {
        return book_type;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    public String getFirst_page() {
        return first_page;
    }

    public void setFirst_page(String first_page) {
        this.first_page = first_page;
    }

    public int getNo_of_pages() {
        return no_of_pages;
    }

    public void setNo_of_pages(int no_of_pages) {
        this.no_of_pages = no_of_pages;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
