package com.example.abumuhsin.udusmini_library.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.flip_model;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Convert_to_pdfTask extends AsyncTask<Void, File, File> {
    FlipBooKActivity flipBooKActivity = null;
    private MyBook_fragment myBook_fragment = null;
    private File dest_file;
    private ProgressDialog dialog;

    public Convert_to_pdfTask(@Nullable FlipBooKActivity flipBooKActivity, @Nullable MyBook_fragment myBook_fragment
            , File dest_file) {
        this.flipBooKActivity = flipBooKActivity;
        this.myBook_fragment = myBook_fragment;
        this.dest_file = dest_file;
    }

    @Override
    protected void onPreExecute() {
//            dialog.setProgressStyle(R.style.Dial);
        if (flipBooKActivity != null) {
            dialog = new ProgressDialog(flipBooKActivity);
            dialog.setMax(flipBooKActivity.getFlip_list().size());
        } else if (myBook_fragment != null) {
            dialog = new ProgressDialog(myBook_fragment.requireActivity());
            //get no_of_pages
            dialog.setMax(myBook_fragment.getNo_of_pages());
        }
        dialog.setTitle("Converting to pdf...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(Void... voids) {
        ArrayList<String> images = new ArrayList<>();
        Context context = null;
        if (myBook_fragment != null) {
            context = myBook_fragment.requireContext();
            //get paths from database
            images = myBook_fragment.getPic_table().get_BookPaths(myBook_fragment.getBook_clicked());
            Collections.reverse(images);
        } else if (flipBooKActivity != null) {
            context = flipBooKActivity.getApplicationContext();
            LinkedList<flip_model> flip_list = flipBooKActivity.getFlip_list();
                for (int i = 1; i < flip_list.size()-1; i++) {
                    Log.i("pdf_bitmap", "pdf page  " + i + " is about to add");
                    images.add(flip_list.get(i).getImage_path());
                    Log.i("pdf_bitmap", "pdf page  " + i + " is added");
                }
        }
        try {
            if ((myBook_fragment!=null && images.size()>0)
                    ||flipBooKActivity!=null &&(images.size()>2)) {
                PdfUtils.add_image_to_pdfItext(context,dest_file.getPath(), images, new PdfUtils.OnPdfListener() {
                    @Override
                    public void onPdf_progress(int i) {
                        dialog.setProgress(i);
                    }
                });
            }else {
                return null;
            }
            Log.i("pdf_bitmap", "pdf creation Succeed");
        } catch (Exception e) {
            Log.i("pdf_bitmap", "pdf creation failed");
            e.printStackTrace();
        }

        return dest_file;
    }

    @Override
    protected void onPostExecute(final File file) {
        super.onPostExecute(file);
        if (file!=null) {
            if (flipBooKActivity != null) {
                PdfUtils.play_pdf(flipBooKActivity, file);
                Toast.makeText(flipBooKActivity, "Done!!!", Toast.LENGTH_SHORT).show();
            } else if (myBook_fragment != null) {
                if (myBook_fragment.is_opening_with()) {
                    PdfUtils.play_pdf(myBook_fragment.requireActivity(), file);
                } else if (myBook_fragment.is_sharing_withPdf()) {
                    PdfUtils.share_pdf(myBook_fragment.requireActivity(), file);
                } else if (myBook_fragment.is_sharing_withBook()) {
    //                PdfUtils.share_pdf(myBook_fragment.requireActivity(), file);
                } else {
                    new AlertDialog.Builder(myBook_fragment.requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                            .setMessage("Pdf conversion is finished, you can find the file at the location " + dest_file.getPath()
                                    + ".Do you want to open the pdf file now?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PdfUtils.play_pdf(myBook_fragment.requireActivity(), file);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        }else {
            Context context = flipBooKActivity!=null?flipBooKActivity.getApplicationContext():myBook_fragment.requireContext();
            Toast.makeText(context,
                    "This book is empty,you need to add one or more pages" +
                            "before you can convert the book to pdf"
                    , Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }
}
