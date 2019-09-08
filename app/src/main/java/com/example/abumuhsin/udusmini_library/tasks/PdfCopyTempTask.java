package com.example.abumuhsin.udusmini_library.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;

import java.io.File;
import java.io.InputStream;

import androidx.annotation.Nullable;

public class PdfCopyTempTask extends AsyncTask<Void, File, File> {
    public static final String ZIPTAG = "ZipTAG";
    private FlipBooKActivity flipBooKActivity = null;
    private File dest_directory;
    //    private String file_to_unzip;
    private InputStream pdf_inputStream;
    private ProgressDialog dialog;

    public PdfCopyTempTask(@Nullable FlipBooKActivity flipBooKActivity, File dest_directory, InputStream pdf_stream) {
        this.flipBooKActivity = flipBooKActivity;
        this.dest_directory = dest_directory;
        this.pdf_inputStream = pdf_stream;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(flipBooKActivity);
        dialog.setTitle("UnZipping your files...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(Void... voids) {
return null;
    }

    @Override
    protected void onPostExecute(final File file) {
        super.onPostExecute(file);

        dialog.dismiss();
    }
}
