package com.example.abumuhsin.udusmini_library.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment;

import java.util.ArrayList;

import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;


public final class DevicePdfLoaderTask implements Runnable {
    private static DevicePdfLoaderTask _object;
    private Thread pdf_thread;
    private PDFBook_fragment pdfBook_fragment;
    public boolean do_stop_thread = false;


    public static DevicePdfLoaderTask get(PDFBook_fragment context) {
        if (_object == null) {
            _object = new DevicePdfLoaderTask(context);
        }
        return _object;
    }

    private DevicePdfLoaderTask(PDFBook_fragment context) {
        this.pdfBook_fragment = context;
    }

    //String tmp_book;
    ArrayList<String> pdf_paths = new ArrayList<>();

    public void LoadPdfsFromStorage() {
        do_stop_thread = false;
        pdf_thread = new Thread(this);
        Log.i("pdf_bitmap", "before starting the Loadthread");
        pdf_thread.start();
        Log.i("pdf_bitmap", "after starting the Loadthread");
    }

    @Override
    public void run() {
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        boolean boolean_folder = false;
        String absolutePathOfPdfs;
        String path_of_folder;

        uri = MediaStore.Files.getContentUri("external");
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
        if (pdfBook_fragment!=null && pdfBook_fragment.isAdded()) {
            cursor = pdfBook_fragment.requireContext().getContentResolver().query(uri, projection, selectionMimeType, selectionArgsPdf, orderBy + " DESC");
            if (cursor != null) {
                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                pdf_paths.clear();
                while (cursor.moveToNext()) {
                    absolutePathOfPdfs = cursor.getString(column_index_data);
                    pdf_paths.add(absolutePathOfPdfs);
//                Log.i(GALLERY_TAG, "original path is " + absolutePathOfPdfs);
                    path_of_folder = cursor.getString(column_index_folder_name);
                }
                if (pdfBook_fragment!=null && pdfBook_fragment.isAdded()) {
                    pdfBook_fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pdfBook_fragment!=null && pdfBook_fragment.isAdded()) {
                                pdfBook_fragment.pdf_paths.clear();
                            for (String pdf : pdf_paths) {
                                pdfBook_fragment.updateList(pdf);
    //                        Log.i(GALLERY_TAG, "duplicate path is " + pdf);
                            }
                            }
                        }
                    });
                }
//            Log.i(GALLERY_TAG, "before cursor is close");
                cursor.close();
//            Log.i(GALLERY_TAG, "cursor is closed");

            } else {
//            Log.i(GALLERY_TAG, "cursor is null");
            }
        }
    }


    public void stop_pdfThread_and_delete_tmp_pages() {
        if (is_pdf_thread_objectRunning()) {
            synchronized (this) {
                do_stop_thread = true;
                notify();
            }

            // wait for thread stopping
            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
                Log.d(PIC_TAG, "Waiting thread to stop ...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

            }
        }
        if (pdf_thread.isAlive()) {
            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //delete even when the Thread is dead.
    }

    public void JustStop_pdfThread() {
        if (is_pdf_thread_objectRunning()) {
            synchronized (this) {
                do_stop_thread = true;
                notify();
            }

            // wait for thread stopping
            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
                Log.d(PIC_TAG, "Waiting thread to stop ...");
                try {
                    Thread.sleep(500);
                    do_stop_thread = true;
                } catch (InterruptedException e) {
                }

            }
        }
        if (pdf_thread.isAlive()) {
            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
            try {
                Thread.sleep(300);
                do_stop_thread = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean is_pdf_thread_objectRunning() {
        return pdf_thread != null && pdf_thread.isAlive();
    }

    public boolean is_pdf_thread_notNull() {
        return pdf_thread != null;
    }

}
