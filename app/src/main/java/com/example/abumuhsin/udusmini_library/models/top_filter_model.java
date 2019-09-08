package com.example.abumuhsin.udusmini_library.models;

public class top_filter_model {
        private String filter;
        private String filter_column;

    public top_filter_model(String filter, String filter_column) {
        this.filter = filter;
        this.filter_column = filter_column;
    }

    @Override
    public boolean equals(Object obj) {
        return ((top_filter_model) obj).filter.equals(this.filter);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter_column() {
        return filter_column;
    }

    public void setFilter_column(String filter_column) {
        this.filter_column = filter_column;
    }
}
