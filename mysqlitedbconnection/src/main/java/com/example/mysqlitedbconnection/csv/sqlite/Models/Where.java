package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 27/08/2018.
 */

public class Where {

    private String column;
    private Object value;

    public Where(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
