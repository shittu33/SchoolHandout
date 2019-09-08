package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class Column_Values {
    private String Column;
    private Object[] values;

    public Column_Values(String column, Object... values) {
        Column = column;
        this.values = values;
    }

    public String getColumn() {
        return Column;
    }

    public Object[] getValues() {
        return values;
    }
}
