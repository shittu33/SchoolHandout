package com.example.abumuhsin.udusmini_library.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;


public final class DevicePdfAsyncTask extends AsyncTask<String,String,String> {
    private Context context;
    public OnLoadingPdfs onLoadingPdfs;



    public DevicePdfAsyncTask(Context context, OnLoadingPdfs onLoadingPdfs) {
        this.context = context;
        this.onLoadingPdfs = onLoadingPdfs;
    }
    @Override
    protected String doInBackground(String... strings) {
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        boolean boolean_folder = false;
        String absolutePathOfPdfs;
//        String path_of_folder;

        uri = MediaStore.Files.getContentUri("external");
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
//        if (context != null && context.isAdded()) {
        cursor = context.getContentResolver().query(uri, projection, selectionMimeType, selectionArgsPdf, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//                pdf_paths.clear();
            while (cursor.moveToNext()) {
                absolutePathOfPdfs = cursor.getString(column_index_data);
                onLoadingPdfs.onPdfAdded(absolutePathOfPdfs);
//                    pdf_paths.add(absolutePathOfPdfs);
//                Log.i(GALLERY_TAG, "original path is " + absolutePathOfPdfs);
//                    path_of_folder = cursor.getString(column_index_folder_name);
            }
//                if (context != null && context.isAdded()) {
//                    context.getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (context != null && context.isAdded()) {
//                                context.pdf_paths.clear();
//                                for (String pdf : pdf_paths) {
//                                    context.updateList(pdf);
//                                                            Log.i(GALLERY_TAG, "duplicate path is " + pdf);
//                                }
//                            }
//                        }
//                    });
//                }
//            Log.i(GALLERY_TAG, "before cursor is close");
            cursor.close();
//            Log.i(GALLERY_TAG, "cursor is closed");

//            } else {
//            Log.i(GALLERY_TAG, "cursor is null");
//            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onLoadingPdfs.onLoadFinished();

    }

    public interface OnLoadingPdfs {
        void onPdfAdded(String pdf_name);
        void onLoadFinished();
//        boolean onPdfAdded(String pdf_name);
    }
}
