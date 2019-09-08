package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class Column_Row {
    private String Column;
    private String row;

    public Column_Row(String column, String row) {
        Column = column;
        this.row = row;
    }

    public String getColumn() {
        return Column;
    }

    public String getRow() {
        return row;
    }
}
