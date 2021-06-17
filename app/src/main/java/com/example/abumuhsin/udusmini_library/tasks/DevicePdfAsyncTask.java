package com.example.abumuhsin.udusmini_library.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;


public final class DevicePdfAsyncTask extends AsyncTask<String, String, String> {
    private Context context;
    public OnLoadingPdfs onLoadingPdfs;


    public DevicePdfAsyncTask(Context context, OnLoadingPdfs onLoadingPdfs) {
        this.context = context;
        this.onLoadingPdfs = onLoadingPdfs;
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        String absolutePathOfPdfs;
//        String path_of_folder;

        uri = MediaStore.Files.getContentUri("external");
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
        cursor = context.getContentResolver().query(uri, projection, selectionMimeType, selectionArgsPdf, orderBy + " DESC");
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfPdfs = cursor.getString(column_index_data);
                onLoadingPdfs.onPdfAdded(absolutePathOfPdfs);
            }
            cursor.close();
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
    }
}
