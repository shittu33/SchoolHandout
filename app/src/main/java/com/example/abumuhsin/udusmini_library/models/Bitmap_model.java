package com.example.abumuhsin.udusmini_library.models;

import android.graphics.Bitmap;
import android.util.SparseArray;

/**
 * Created by Abu Muhsin on 27/11/2018.
 */

public class Bitmap_model {
//    HashMap<Integer, Bitmap> bitmapHashMap = new HashMap<>();
    private static SparseArray<Bitmap> sparseArray = new SparseArray<>();

    public static void AddBitmap(int key,Bitmap bitmap) {
        sparseArray.put(key,bitmap);
//        bitmapHashMap.put(key,bitmap);
    }

    public static Bitmap get_A_Bitmap(int key) {
        return sparseArray.get(key);
//        return bitmapHashMap.get(key);
    }


}
