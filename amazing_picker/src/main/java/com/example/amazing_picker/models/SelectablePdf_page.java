package com.example.amazing_picker.models;

public class SelectablePdf_page {
    private String pdf_page_path;
    private boolean is_page_selected;

    public SelectablePdf_page(String pdf_page_path, boolean is_page_selected) {
        this.pdf_page_path = pdf_page_path;
        this.is_page_selected = is_page_selected;
    }

    public String getPdf_page_path() {
        return pdf_page_path;
    }

    public void setPdf_page_path(String pdf_page_path) {
        this.pdf_page_path = pdf_page_path;
    }

    public boolean is_page_selected() {
        return is_page_selected;
    }

    public void set_page_selected(boolean is_page_selected) {
        this.is_page_selected = is_page_selected;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        SelectablePdf_page selectablePdf_page = (SelectablePdf_page) obj;
        return selectablePdf_page.getPdf_page_path().equals(this.getPdf_page_path());

    }
}
