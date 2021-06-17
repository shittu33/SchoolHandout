package com.example.abumuhsin.udusmini_library.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;

import static com.example.amazing_picker.utilities.Pdf_Cursor_loaderUtils.PIC_TAG;


public final class DeviceGalleryAsyncTask extends AsyncTask<String, String, String> {
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
//        boolean boolean_folder = false;
        String absolutePathOfImage = null;
        String path_of_folder = null;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            onLoadingImages.onPreImageLoad();
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                path_of_folder = cursor.getString(column_index_folder_name);
                Log.i("do_in_background","the path_of_folder is " + path_of_folder);
                onLoadingImages.onImageANDFolderLoading(path_of_folder!=null?path_of_folder:"others", absolutePathOfImage);
            }
            cursor.close();
        } else {
            Log.i(PIC_TAG, "cursor is null");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onLoadingImages.onLoadFinished();

    }

    public interface OnLoadingImages {

        void onLoadFinished();

        void onImageANDFolderLoading(String path_of_folder, String absolutePathOfImage);

        void onPreImageLoad();

        //        boolean onPdfAdded(String pdf_name);
    }

}
