package com.example.mysqlitedbconnection.csv.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abu Muhsin on 15/02/2018.
 */

public class DatabaseCreator extends SQLiteOpenHelper  {
    public static final String TAG = "debug";
    SQLiteDatabase db = getWritableDatabase();


    public DatabaseCreator(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
