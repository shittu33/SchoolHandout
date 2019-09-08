package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 27/08/2018.
 */

public class Row {

    private Object unique_row;
    private Object new_value;

    public Row(Object unique_row, Object new_value) {
        this.unique_row = unique_row;
        this.new_value = new_value;
    }

    public Object getUnique_row() {
        return unique_row;
    }

    public void setUnique_row(String unique_row) {
        this.unique_row = unique_row;
    }

    public Object getNew_value() {
        return new_value;
    }

    public void setNew_value(Object new_value) {
        this.new_value = new_value;
    }


}
