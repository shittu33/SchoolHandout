package com.example.abumuhsin.udusmini_library.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.LocalHandout;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UnZippingToBookTask extends AsyncTask<Void, File, File> {
    public static final String ZIPTAG = "ZipTAG";
    private MyBook_fragment myBook_fragment = null;
    private File dest_directory;
//    private String file_to_unzip;
    private InputStream zip_inputStream;
    private boolean is_for_share;
    private ProgressDialog dialog;
    private int no_of_fileToZip = 0;
    File unzip_file_parent_dir ;
    private LocalHandout mNew_book_model;
    public UnZippingToBookTask(@Nullable MyBook_fragment myBook_fragment, File dest_directory
            , InputStream zip_stream, boolean is_for_share) {
        this.myBook_fragment = myBook_fragment;
        this.dest_directory = dest_directory;
//        this.file_to_unzip = file_to_unzip;
        this.zip_inputStream = zip_stream;
        this.is_for_share = is_for_share;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(myBook_fragment.requireActivity());
        dialog.setTitle("UnZipping your files...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(Void... voids) {
        try {
//            FileUtils.unzipStream(file_to_unzip, dest_directory);
            unzip_file_parent_dir = FileUtils.unzipStream(zip_inputStream, dest_directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(ZIPTAG, "the book is " + unzip_file_parent_dir.getName());
        File book_csvFile = new File(unzip_file_parent_dir, "books.csv");
        File pages_csvFile = new File(unzip_file_parent_dir, "pages.csv");
        File pic_csvFile = new File(unzip_file_parent_dir, "pictures.csv");
        File pic_bucket_csvFile = new File(unzip_file_parent_dir, "picturesBucket.csv");
        if (book_csvFile.exists()) {
            MyBook_fragment.book_table.ReadCsvToBookTable(book_csvFile);
            Log.i(ZIPTAG, "the book is read");
        }
        if (pages_csvFile.exists()) {
            MyBook_fragment.page_table.ReadCsvToPagesTable(pages_csvFile);
        }
        if (pic_csvFile.exists()) {
            MyBook_fragment.pic_table.ReadCsvToPictureTable(pic_csvFile);
        }
        if (pic_bucket_csvFile.exists()) {
            MyBook_fragment.picture_bucketTable.ReadCsvToBucketTable(pic_bucket_csvFile);
        }
        mNew_book_model = MyBook_fragment.book_table.getLastBookInfo();
        ArrayList<LocalHandout> local_handouts = myBook_fragment.getPics_title_list();
        if (!local_handouts.contains(mNew_book_model)) {
            local_handouts.add(mNew_book_model);
        }
        return unzip_file_parent_dir;
    }

    @Override
    protected void onPostExecute(final File file) {
        super.onPostExecute(file);
//        mNew_book_model = MyBook_fragment.book_table.getLastBookInfo();
//        if (!myBook_fragment.getPics_title_list().contains(mNew_book_model)) {
            if (file!=null && file.exists()) {
                myBook_fragment.getGrid_adapter().notifyDataSetChanged();
                Toast.makeText(myBook_fragment.requireContext(), "File of path " + file.getPath() + "was extracted", Toast.LENGTH_SHORT).show();
            }
//        }else {
//            Toast.makeText(myBook_fragment.requireContext(),"This book is already in existence" , Toast.LENGTH_SHORT).show();
//        }
        dialog.dismiss();
    }
}
