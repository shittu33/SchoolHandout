package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class Single_ColumnAndContent {
    private String column;
    private Object content;

    public Single_ColumnAndContent(String column, Object content) {
        this.column = column;
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public String getColumn() {
        return column;
    }
}
