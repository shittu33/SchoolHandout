package com.example.amazing_picker.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.amazing_picker.models.Folder_Model;
import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;

public class ImageCursorLoaderUtils {
    public static final String PIC_TAG = "Loader_sample";
    //    private static boolean boolean_folder = false;
    public static final int PERMISSION_ALREADY_REQUESTED = 2;
    public static final int PERMISSION_ALREADY_GRANTED = 3;
    public static final int PERMISSION_REQUEST_NEEDED = 4;

    public static int isPermissionGranted(Activity context) {
        if ((ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
                Log.i(PIC_TAG, "permission request is made by default");
                return PERMISSION_ALREADY_REQUESTED;
            } else {
                Log.i(PIC_TAG, "Manually ask for permission");
                //Request_for_permission();
                return PERMISSION_REQUEST_NEEDED;
            }
        } else {
            Log.i(PIC_TAG, "No hassle at all, you can do anything");
            return PERMISSION_ALREADY_GRANTED;
        }
    }

    public static void Request_for_permission(Activity context, int permission_code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    permission_code);
        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permission_code);
        }
    }

    /**
     * This is a convinient method to generate all folders and images plus taking care of permissions, but you
     * will need to listen for the request permission from your Activity through onRequestPermissionsResult
     *
     * @param context      context of your Activity
     * @param request_code the code you wanna use to request
     * @return this will return all folders with each of their images
     */
    public static ArrayList<Model_images> getImages_and_FoldersWithPermission(Activity context, int request_code) {
        if (isPermissionGranted(context)==PERMISSION_ALREADY_GRANTED) {
            return getImages_and_Folders(context);
        } else if (isPermissionGranted(context)==PERMISSION_REQUEST_NEEDED){
            Request_for_permission(context, request_code);
            Log.i(PIC_TAG, "Should automatically request for permission, just call the method getImages_and_Folders() inside" +
                    "onRequestPermissionsResult()");
            return null;
        }
        return null;
    }

    public static ArrayList<Model_images> getImages_and_Folders(Context c) {
        ArrayList<Model_images> al_images = new ArrayList<>();
        int int_position = 0;

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        boolean boolean_folder = false;
        String absolutePathOfImage;
        String path_of_folder;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};

        cursor = c.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                String tmp_folder = cursor.getString(column_index_folder_name);
                path_of_folder = tmp_folder!=null?tmp_folder:"others";
                Log.e(PIC_TAG, path_of_folder);
                for (int i = 0; i < al_images.size(); i++) {
                    if (al_images.get(i).getStr_folder().equals(path_of_folder)) {
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
                    ArrayList<String> al_path = new ArrayList<>(al_images.get(int_position).getAl_imagepath());
//                    Log.e(PIC_TAG, "adding it images....");
                    al_path.add(absolutePathOfImage);
                    Log.e(PIC_TAG, absolutePathOfImage);
                    al_images.get(int_position).setAl_imagepath(al_path);
                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    Model_images obj_model = new Model_images();
                    obj_model.setStr_folder(path_of_folder);
//                    Log.e(PIC_TAG, "folder named " + path_of_folder + " was added");
                    obj_model.setAl_imagepath(al_path);
                    al_images.add(obj_model);

                }
            }
            Log.i(PIC_TAG, "before cursor is close");
            cursor.close();
            Log.i(PIC_TAG, "cursor is closed");
        } else {
            Log.i(PIC_TAG, "cursor is null");
        }
        return al_images;
    }


    public static ArrayList<Folder_Model> getListOfDirectories(Context c) {
        ArrayList<Folder_Model> folders = new ArrayList<>();
        ArrayList<String> folder_names = new ArrayList<>();
        ArrayList<String> folders_ids = new ArrayList<>();
        ArrayList<String> folders_first_image = new ArrayList<>();


        boolean does_folder_exist = false;

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_index_folder_id;

        String absolutePathOfFirstImage = null;
        String path_of_folder = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = c.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        if (cursor != null) {
//            cursor.getColumnCount(MediaStore.MediaColumns.DATA);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            column_index_folder_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                absolutePathOfFirstImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfFirstImage);
                path_of_folder = cursor.getString(column_index_folder_name);
                String id_of_folder = cursor.getString(column_index_folder_id);
                Log.e(PIC_TAG, cursor.getString(column_index_folder_name));
                if (!folder_names.contains(path_of_folder)) {
                    folder_names.add(path_of_folder);
                    folders_ids.add(id_of_folder);
                    folders_first_image.add(absolutePathOfFirstImage);
                }

            }
            cursor.close();
        }
        for (int i = 0; i < folder_names.size(); i++) {
            Folder_Model folder_model = new Folder_Model(folder_names.get(i), folders_ids.get(i), folders_first_image.get(i));
            folders.add(folder_model);
        }

        return folders;
    }


    public static ArrayList<String> getImagesFrom(ArrayList<Model_images> folder_image_collections, String folder) {
        ArrayList<String> empty_list = new ArrayList<>();
        for (Model_images model_image : folder_image_collections) {
            if (model_image.getStr_folder().equals(folder)) {
                return model_image.getAl_imagepath();
            }
        }
        return empty_list;
    }

    public static ArrayList<String> getAllImages(ArrayList<Model_images> folders_and_imagesCollections) {
        ArrayList<String> images = new ArrayList<>();
        for (Model_images model_image : folders_and_imagesCollections) {
            images.addAll(model_image.getAl_imagepath());
        }
        return images;
    }


    public static ArrayList<String> getPictureFromDirectories(Context c, String dir_id) {
//        al_images.clear();
        ArrayList<String> images = new ArrayList<>();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_id;

        String absolutePathOfImage = null;
        String folder_id = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = c.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                folder_id = cursor.getString(column_index_folder_id);
                if (dir_id.equals(folder_id)) {
                    images.add(absolutePathOfImage);
                }
            }
            cursor.close();
        }
        return images;
    }


}
