package com.example.abumuhsin.udusmini_library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Abu Muhsin on 15/11/2018.
 */

public class App_Preferences {
    private SharedPreferences mPreferences;
    private static String PREF_NAME = "App_Info";
    private SharedPreferences.Editor editor;
    public final static int VERY_FAST = 0;
    public final static int FAST = 1;
    public final static int NORMAL = 2;
    public final static int SLOW = 3;

    @SuppressLint("CommitPrefEdits")
    public App_Preferences(Context mContext) {
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public void savePageNo(String which_book, int page_no) {
        editor.putInt(which_book, page_no);
        editor.apply();
    }

    public void clearBookSettings(String book_name) {
        editor.remove(book_name).commit();
    }

    public void saveFlipSpeed(String which_book, int speed) {
        editor.putInt(which_book + "_speed", speed);
        editor.apply();
    }

    public int getFlipSpeed(String which_book) {
        if (which_book == null) {
            return NORMAL;
        }
        return mPreferences.getInt(which_book + "_speed", NORMAL);
    }

    public int getPageNo(String which_book) {
        if (which_book == null) {
            return 0;
        }
        return mPreferences.getInt(which_book, 0);
    }

//    public void saveObject(String key, View view) {
//        Gson gson = new Gson();
//        String gson_of_object = gson.toJson(view);
//        editor.putString(key, gson_of_object);
//        editor.commit();
//    }
//
//    public Object getObject(String key) {
//        Gson gson = new Gson();
//        return gson.fromJson(mPreferences.getString(key, ""), View.class);
//    }
//
//    public void saveListOfObject(String key, List<View> object_list) {
//        Gson gson = new Gson();
//        String gson_of_object = gson.toJson(object_list);
//        editor.putString(key, gson_of_object);
//        editor.commit();
//    }
//
//    public List<View> getListOfObject(String key) {
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<View>>() {
//        }.getType();
//        return gson.fromJson(mPreferences.getString(key, ""), type);
//    }

}
