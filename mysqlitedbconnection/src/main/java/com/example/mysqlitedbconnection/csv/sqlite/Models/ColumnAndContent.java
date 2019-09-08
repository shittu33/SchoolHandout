package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class ColumnAndContent {
    private String Column;
    private Object [] contents;

    public ColumnAndContent(String column, Object[] contents) {
        Column = column;
        this.contents = contents;
    }

    public String getColumn() {
        return Column;
    }

    public Object[] getContents() {
        return contents;
    }
}
