package com.example.abumuhsin.udusmini_library.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;


public final class DeviceGalleryAsyncTask extends AsyncTask<String,String,String> {
    private Context context;
    public OnLoadingImages onLoadingImages;

//    private static DeviceGalleryAsyncTask _object;
//    private Thread pdf_thread;
//    private GalleryBook_fragment galleryBook_fragment;
//    public boolean do_stop_thread = false;

    public DeviceGalleryAsyncTask(Context context, OnLoadingImages onLoadingImages) {
        this.context = context;
        this.onLoadingImages = onLoadingImages;
    }

    @Override
    protected String doInBackground(String... strings) {
        final ArrayList<Model_images> al_images_and_folder = new ArrayList<>();
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
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                path_of_folder = cursor.getString(column_index_folder_name);
//                Log.e(PIC_TAG, path_of_folder);
                for (int i = 0; i < al_images_and_folder.size(); i++) {
                    if (al_images_and_folder.get(i).getStr_folder().equals(path_of_folder)) {
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
                    ArrayList<String> al_path = new ArrayList<>(al_images_and_folder.get(int_position).getAl_imagepath());
//                    Log.e(PIC_TAG, "adding it images....");
                    al_path.add(absolutePathOfImage);
//                    Log.e(PIC_TAG, absolutePathOfImage);
                    al_images_and_folder.get(int_position).setAl_imagepath(al_path);
                    onLoadingImages.onImageFolderAdded(al_images_and_folder.get(int_position).getStr_folder(),
                            al_images_and_folder.get(int_position).getAl_imagepath(),boolean_folder,int_position);
                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    final Model_images obj_model = new Model_images();
                    obj_model.setStr_folder(path_of_folder);
//                    Log.e(PIC_TAG, "folder named " + path_of_folder + " was added");
                    obj_model.setAl_imagepath(al_path);
                    al_images_and_folder.add(obj_model);
                    onLoadingImages.onImageFolderAdded(path_of_folder,al_path,boolean_folder,int_position);
                }
            }
//            Log.i(PIC_TAG, "before cursor is close");
            cursor.close();
//            Log.i(PIC_TAG, "cursor is closed");
        } else {
//            Log.i(PIC_TAG, "cursor is null");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onLoadingImages.onLoadFinished();

    }

    public interface OnLoadingImages {
        void onImageFolderAdded(String folder, ArrayList<String> images, boolean is_folder, int int_position);
        void onLoadFinished();
//        boolean onPdfAdded(String pdf_name);
    }

}
