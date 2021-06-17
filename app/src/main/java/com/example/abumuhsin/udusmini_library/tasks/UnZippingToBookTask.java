package com.example.abumuhsin.udusmini_library.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.abumuhsin.udusmini_library.activities.MainActivity;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.LocalHandout;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class UnZippingToBookTask extends AsyncTask<Void, File, File> {
    public static final String ZIPTAG = "ZipTAG";
    public static final String SHARE_ID = "_share" ;
    private MyBook_fragment myBook_fragment = null;
    private String handout_title;
    private File dest_directory;
    //    private String file_to_unzip;
    private InputStream zip_inputStream;
    private File cover_file;
    private boolean is_for_share;
    private ProgressDialog dialog;
    private int no_of_fileToZip = 0;
    File unzip_file_parent_dir;
    private LocalHandout mNew_book_model;

    public UnZippingToBookTask(@Nullable MyBook_fragment myBook_fragment, String handout_title
            , File dest_directory
            , InputStream zip_stream, File cover_file, boolean is_for_share) {
        this.myBook_fragment = myBook_fragment;
        this.handout_title = handout_title;
        this.dest_directory = dest_directory;
//        this.file_to_unzip = file_to_unzip;
        this.zip_inputStream = zip_stream;
        this.cover_file = cover_file;
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

    private static final String TAG = "UnZippingToBookTask";

    public static String SHARED_TIME;

    @Override
    protected File doInBackground(Void... voids) {
        try {
            if (handout_title != null) {
                File old_book = new File(FileUtils.getBookFilesPath(handout_title));
                Log.e(TAG, " unzip_file_parent_dir : original title is " + handout_title);
                boolean is_book_exist = MyBook_fragment.book_table.is_book_exist(handout_title);
                File[] old_book_sub_dir = old_book.listFiles();
                boolean is_book_file_exist = old_book_sub_dir != null && old_book_sub_dir.length > 0;
                if (is_book_exist && is_book_file_exist) {
                    File tmp_dir = new File(old_book.getParentFile(), handout_title + "_tmp");
                    SHARED_TIME = String.valueOf(System.currentTimeMillis());
                    File shared_dir = new File(old_book.getParentFile(), handout_title + SHARE_ID + SHARED_TIME);
//                    if (shared_dir.exists() || MyBook_fragment.book_table.is_book_exist(handout_title + SHARE_ID)){
//                        return null;
//                    }
                    if (old_book.renameTo(tmp_dir)) {
                        unzip_file_parent_dir = FileUtils.unzipStream(zip_inputStream, dest_directory, shared_dir.getName());
                        Log.e(TAG, " unzip_file_parent_dir : renamed from " + old_book.getPath() + " to " + tmp_dir.getPath());
                    }
                    if (unzip_file_parent_dir.renameTo(shared_dir)) {
                        Log.e(TAG, " unzip_file_parent_dir : renamed from " + unzip_file_parent_dir.getPath() + " to " + shared_dir.getPath());
                        if (tmp_dir.renameTo(old_book)) {
                            Log.e(TAG, " tmp_dir : renamed to " + old_book.getPath());
                        }
                    }

                } else {
                    unzip_file_parent_dir = FileUtils.unzipStream(zip_inputStream, dest_directory, handout_title);
                    Log.e(TAG, " book doesn't exist so the unzip_file_parent_dir is still" + unzip_file_parent_dir.getName());
                }
            } else {
                unzip_file_parent_dir = FileUtils.unzipStream(zip_inputStream, dest_directory, handout_title);
                Log.e(TAG, "handout title is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(ZIPTAG, "the book is " + unzip_file_parent_dir.getName());
        File book_csvFile = new File(unzip_file_parent_dir, "books.csv");
        File pages_csvFile = new File(unzip_file_parent_dir, "pages.csv");
        File pic_csvFile = new File(unzip_file_parent_dir, "pictures.csv");
        File pic_bucket_csvFile = new File(unzip_file_parent_dir, "picturesBucket.csv");
        if (book_csvFile.exists()) {
            MyBook_fragment.book_table.ReadCsvToBookTable(book_csvFile, cover_file);
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
        if (file != null && file.exists()) {
            GotoLocalFragment(myBook_fragment);
            myBook_fragment.getGrid_adapter().notifyDataSetChanged();
            Toast.makeText(myBook_fragment.requireContext(), "File of path " + file.getPath() + "was extracted", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(myBook_fragment.requireContext(), "book already exist", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void GotoLocalFragment(MyBook_fragment myBook_fragment) {
        MainActivity mainActivity = (MainActivity) myBook_fragment.requireActivity();
        mainActivity.getPager().setCurrentItem(MainActivity.MY_BOOK_INDEX);
    }
}
