package com.example.abumuhsin.udusmini_library.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Abu Muhsin on 09/11/2018.
 */

public class DatabaseUtils {
    private static final String TAG = "active";

    public static void ExportDatabase(Context context, String dest_path, String package_name, String database_name) {
        /**dest_path is something like "/sd_data/pics.db"*/
        String src_path = "/data/" + package_name + "/databases/" + database_name;
        try {
            File src_file = new File(Environment.getDataDirectory(), src_path);
            File dst_file = new File(Environment.getExternalStorageDirectory(), dest_path);
            if (dst_file.canWrite()) {
                FileChannel src = new FileInputStream(src_file).getChannel();
                FileChannel dst = new FileOutputStream(dst_file).getChannel();
                dst.transferFrom(src,0,src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Database was successfully exported to "+ dest_path, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "can't write to this sd card", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, "smtn went wrong", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"smtn went wrong");
        }
    }

    public static void ImportDatabase(Context context, String src_path, String package_name, String dst_database_name) {
        String dest_path = "/data/" + package_name + "/databases/" + dst_database_name;
        try {
            File src_file = new File(Environment.getExternalStorageDirectory(), src_path);
            File dst_file = new File(Environment.getDataDirectory(), dest_path);
            if (src_file.canRead()){
                FileChannel src = new FileInputStream(src_file).getChannel();
                FileChannel dst = new FileOutputStream(dst_file).getChannel();
                dst.transferFrom(src,0,src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Database was successfully imported to "+ dst_database_name, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "can't write to this sd card", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, "smtn went wrong", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"smtn went wrong");
        }
    }
}
