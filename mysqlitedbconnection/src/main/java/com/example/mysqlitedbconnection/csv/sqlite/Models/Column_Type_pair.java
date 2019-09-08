package com.example.mysqlitedbconnection.csv.sqlite.Models;

/**
 * Created by Abu Muhsin on 26/08/2018.
 */

public class Column_Type_pair {
    private String Colum;
    private String Type;

    public Column_Type_pair(String colum, String type) {
        Colum = colum;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public String getColum() {
        return Colum;
    }
}
