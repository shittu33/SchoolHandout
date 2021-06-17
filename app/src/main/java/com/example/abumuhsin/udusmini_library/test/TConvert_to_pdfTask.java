package com.example.abumuhsin.udusmini_library.test;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class TConvert_to_pdfTask extends AsyncTask<File, File, File> {
    private boolean is_first_last_page_ignore;
    //    private MyBook_fragment myBook_fragment = null;
//    private ;
    private ArrayList<String> book_images;
    private ConvertPdfCallbacks convertPdfCallbacks;

    public TConvert_to_pdfTask(boolean ignore_first_last_page, ArrayList<String> book_images, ConvertPdfCallbacks convertPdfCallbacks) {
        this.is_first_last_page_ignore = ignore_first_last_page;
        this.book_images = book_images;
        this.convertPdfCallbacks = convertPdfCallbacks;
    }

    @Override
    protected void onPreExecute() {
            convertPdfCallbacks.onPdfConversionStarted(book_images.size());
        super.onPreExecute();
    }


    @Override
    protected File doInBackground(File... dest_file) {
        ArrayList<String> images = book_images;
        if (( !is_first_last_page_ignore && images.size() > 0) || (is_first_last_page_ignore && images.size() > 2)) {
            convertPdfCallbacks.onReadyToConvertInBackground(dest_file[0].getPath(), images);

        } else {
            String error_msg = "can't create pdf with an empty page";
            Log.i("pdf_bitmap", error_msg);
            return null;
        }
        return dest_file[0];
    }

    @Override
    protected void onPostExecute(final File file) {
        super.onPostExecute(file);
        if (file != null) {
            Log.i("pdf_bitmap", "pdf creation Succeed");
            convertPdfCallbacks.onPdfConversionSucceeded(file, "pdf is successfuly created!!");
        } else {
            String error_msg = "can't create pdf with an empty page, add new pages then come back to convert";
            Log.i("pdf_bitmap", error_msg);
            convertPdfCallbacks.onConversionError(error_msg);

        }
    }

    public interface ConvertPdfCallbacks {
        void onPdfConversionStarted(int book_size);

        void onReadyToConvertInBackground(String path, ArrayList<String> images);

        void onConversionError(String error_msg);

        void onPdfConversionSucceeded(File file, String s);
    }
}

