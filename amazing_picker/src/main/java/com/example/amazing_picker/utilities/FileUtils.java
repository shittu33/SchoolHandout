package com.example.amazing_picker.utilities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Abu Muhsin on 09/11/2018.
 */

public class FileUtils {
    public static int calculateSampleSize(BitmapFactory.Options options, int destWidth, int destHeight) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }


    public static Bitmap getDownSizedBitmapFromPath(String path, int width, int height) {
        Bitmap selectedBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options, width, height);
        selectedBitmap = BitmapFactory.decodeFile(path, options);
        return selectedBitmap;
    }

    public static final int CAMERA = 0;
    public static final int GALLERY = 1;
    public static final int PDFS = 2;

    public static String getRealDateAndTime(int type) {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == CAMERA) {
            return "CAMERA_" + timeStamp + "_";
        } else if (type == GALLERY) {
            return "GALLERY_" + timeStamp + "_";
        }
        return "PDF_" + timeStamp + "_";
    }


    public static File saveImage(Bitmap bitmap, File saveFile, int quality) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (saveFile.exists()) {
            return saveFile;
        }
        return null;
    }

    public static File saveScaledImage(File dest, File src, int require_width, int require_height) {
        Bitmap bitmap = getDownSizedBitmapFromPath(src.getPath(), require_width, require_height);
        return saveImage(bitmap, dest, 100);
    }

    public static File saveScaledImageSpecific(File dest, File src) {
        Bitmap bitmap = getDownSizedBitmapFromPath(src.getPath(), 800, 800);
        return saveImage(bitmap, dest, 100);
    }
}
