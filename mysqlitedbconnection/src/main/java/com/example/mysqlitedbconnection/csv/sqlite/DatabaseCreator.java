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

//    public void update(byte[] pic1, byte[] pic2, byte[] pic3, byte[] pic4, byte[] pic5, int id) {
//        String u_statement = "UPDATE ALBUM SET pic1 = ?,pic2 = ?,pic3 = ?,pic4 = ?,pic5 = ?,WHERE id =?";
//        SQLiteStatement statement = db.compileStatement(u_statement);
//        statement.bindBlob(1, pic1);
//        statement.bindBlob(2, pic2);
//        statement.bindBlob(3, pic3);
//        statement.bindBlob(4, pic4);
//        statement.bindBlob(5, pic5);
//        statement.bindDouble(6, (double) id);
//        statement.execute();
//        db.close();
//    }
//
//    public void Delete(int id, String d_statement) {
//        SQLiteDatabase db = getWritableDatabase();
//        SQLiteStatement statement = db.compileStatement(d_statement);
//        statement.clearBindings();
//        statement.bindDouble(1, (double) id);
//        statement.execute();
//        db.close();
//
//
//    }
//
//    public void DeleteAll(String d_statement) {
//        SQLiteDatabase db = getWritableDatabase();
//        SQLiteStatement statement = db.compileStatement(d_statement);
//        statement.execute();
//        db.close();
//
//
//    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
