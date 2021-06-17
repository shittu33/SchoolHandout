package com.example.amazing_picker.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;

public class Pdf_Cursor_loaderUtils {
    public static final String PIC_TAG = "Loader_sample";

    public static ArrayList<Model_images> getPdfs_and_Folders(Context c) {
        ArrayList<Model_images> all_pdfs = new ArrayList<>();
        int int_position = 0;

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        boolean boolean_folder = false;
        String absolutePathOfPdfs;
        String path_of_folder;

        uri =MediaStore.Files.getContentUri("external");
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{ mimeType };

        cursor = c.getApplicationContext().getContentResolver().query(uri, projection, selectionMimeType, selectionArgsPdf, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfPdfs = cursor.getString(column_index_data);
                String tmp_folder = cursor.getString(column_index_folder_name);
                path_of_folder = tmp_folder!=null?tmp_folder:"others";
                Log.e(PIC_TAG, path_of_folder);
                for (int i = 0; i < all_pdfs.size(); i++) {
                    if (all_pdfs.get(i).getStr_folder().equals(path_of_folder)) {
//                        Log.e(PIC_TAG, "inside loop" + "index i is " + i);
                        boolean_folder = true;
                        int_position = i;
                        break;
                    } else {
                        boolean_folder = false;
                    }
                }


                if (boolean_folder) {
//                    Log.e(PIC_TAG, "folder named " + path_of_folder + " already exist");
                    ArrayList<String> al_path = new ArrayList<>(all_pdfs.get(int_position).getAl_imagepath());
//                    Log.e(PIC_TAG, "adding it images....");
                    al_path.add(absolutePathOfPdfs);
                    Log.e(PIC_TAG, absolutePathOfPdfs);
                    all_pdfs.get(int_position).setAl_imagepath(al_path);
                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfPdfs);
                    Model_images obj_model = new Model_images();
                    obj_model.setStr_folder(path_of_folder);
                   // Log.e(PIC_TAG, "folder named " + path_of_folder + " was added");
                    obj_model.setAl_imagepath(al_path);
                    all_pdfs.add(obj_model);

                }
            }
//            Log.i(PIC_TAG, "before cursor is close");
            cursor.close();
//            Log.i(PIC_TAG, "cursor is closed");
        } else {
//            Log.i(PIC_TAG, "cursor is null");
        }
        return all_pdfs;
    }

    public static ArrayList<String> getPdfsFrom(ArrayList<Model_images> folder_pdf_collections, String folder) {
        ArrayList<String> empty_list = new ArrayList<>();
        for (Model_images model_pdf : folder_pdf_collections) {
            if (model_pdf.getStr_folder().equals(folder)) {
                return model_pdf.getAl_imagepath();
            }
        }
        return empty_list;
    }

}
