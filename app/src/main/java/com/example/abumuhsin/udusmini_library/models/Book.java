package com.example.abumuhsin.udusmini_library.models;

public class Book {
    String book_title;
    String book_cover;
    boolean is_pdf;
    String pdf_path;

    public Book(String book_title, String book_cover) {
        this.book_title = book_title;
        this.book_cover = book_cover;
        this.is_pdf = false;
        this.pdf_path = "";
    }

    public Book(String book_title, boolean is_pdf, String pdf_path) {
        this.book_title = book_title;
        this.book_cover = book_cover;
        this.is_pdf = is_pdf;
        this.pdf_path = pdf_path;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public boolean isPdf() {
        return is_pdf;
    }

    public String getPdf_path() {
        return pdf_path;
    }
}
