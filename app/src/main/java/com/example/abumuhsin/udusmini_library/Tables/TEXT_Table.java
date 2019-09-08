package com.example.abumuhsin.udusmini_library.Tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Table;

import java.io.Serializable;

import static com.example.mysqlitedbconnection.csv.Constants.CONTENT;
import static com.example.mysqlitedbconnection.csv.Constants.ID;
import static com.example.mysqlitedbconnection.csv.Constants.TEXT;
import static com.example.mysqlitedbconnection.csv.Constants.TITLE;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class TEXT_Table extends Table implements Serializable {
    public TEXT_Table(DatabaseCreator database) {
        super(database,TEXT, getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(ID)
                , getString_ColumnStatement(TITLE), getString_ColumnStatement(CONTENT));
    }

    public void insert_to_All(String title, String content){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(CONTENT,content);
        insert_values_to_columns(contentValues);
    }
    public Cursor fetch_Title(){
        return fetch_Column(TITLE);
    }
    public  Cursor fetch_Content(){
        return fetch_Column(CONTENT);
    }





}
