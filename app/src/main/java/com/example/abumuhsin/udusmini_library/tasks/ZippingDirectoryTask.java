package com.example.abumuhsin.udusmini_library.tasks;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.abumuhsin.udusmini_library.CoverSuplier;
import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.activities.MainActivity;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.LocalHandout;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZippingDirectoryTask extends AsyncTask<Void, File, File> {
    private static final String ZIPTAG = "ZipTAG";
    private MyBook_fragment myBook_fragment = null;
    private File src_file;
    private File dest_file;
    private boolean is_for_share;
    private ProgressDialog dialog;
    private int no_of_fileToZip = 0;
    private int book_position;

    public ZippingDirectoryTask(MyBook_fragment myBook_fragment, File src_file
            , File dest_file, int position, boolean is_for_share) {
        this.myBook_fragment = myBook_fragment;
        this.src_file = src_file;
        this.dest_file = dest_file;
        this.book_position = position;
        this.is_for_share = is_for_share;
        FirebaseLoginOperation.get(myBook_fragment.requireActivity());
    }

    @Override
    protected void onPreExecute() {
//            dialog.setProgressStyle(R.style.Dial);
//                Log.i(ZIPTAG,src_file.listFiles().length + " files available to zip ");
        dialog = new ProgressDialog(myBook_fragment.requireActivity());
        //get no_of_pages
        File[] files = src_file.listFiles();
        if (files != null) {
            no_of_fileToZip = files.length;
            if (files[0] != null) {
                File[] sub_file = files[0].listFiles();
                int length = 0;
                if (sub_file != null) {
                    length = sub_file.length;
                }
                no_of_fileToZip = length;
            }
        }
        dialog.setMax(no_of_fileToZip);
        if (is_for_share) {
            dialog.setTitle("Zipping your handout to share...");
        } else {
            dialog.setTitle("Zipping the handout to upload...");
        }
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(Void... voids) {
        MyBook_fragment.book_table.WriteBookToCSV(myBook_fragment.getBook_clicked(), src_file);
        MyBook_fragment.page_table.WritePagesToCSV(myBook_fragment.getBook_clicked(), src_file);
        MyBook_fragment.pic_table.SavePicturesTOCSV(myBook_fragment.getBook_clicked(), src_file);
        MyBook_fragment.picture_bucketTable.SavePicturesTOCSV(myBook_fragment.getBook_clicked(), src_file);
        FileOutputStream fos = null;
        Log.i(ZIPTAG, "destination file is " + dest_file.getPath());
        try {
            fos = new FileOutputStream(dest_file);
            Log.i(ZIPTAG, "destination file is " + dest_file.getPath());
        } catch (FileNotFoundException e) {
            Log.i(ZIPTAG, "fos is null");
            e.printStackTrace();
        }
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(src_file.getPath());
        try {
            FileUtils.zipDirectory(fileToZip, fileToZip.getName(), zipOut, new FileUtils.OnDirectoryZipListener() {
                @Override
                public void onDirectoryZipProgress(int i) {
                    Log.i(ZIPTAG, "file " + i);
                    dialog.setProgress(i);
                }
            }, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zipOut.close();
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest_file;
    }

    @Override
    protected void onPostExecute(final File file) {
        super.onPostExecute(file);
        if (is_for_share) {
            FileUtils.share_Zip(myBook_fragment.requireActivity(), file);
            dialog.dismiss();
        } else {
            dialog.dismiss();
            dialog = new ProgressDialog(myBook_fragment.requireActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("Uploading your handout to the database...");
            dialog.show();
            FirebaseLoginOperation.RequestCurrentStudentInfo(new FirebaseLoginOperation.OnLogInOperation() {
                @Override
                public void onStudentInfoRetrieved(Student student) {
                    LocalHandout local_handout = myBook_fragment.getPics_title_list().get(book_position);
                    int total_number_of_pages = local_handout.getPage_no();
                    String book_title = local_handout.getTitle();
                    String course_abrv = CoverSuplier.getAbrvFromCourseCode(local_handout.getCourse_code());
                    String code = CoverSuplier.getCourseCodeNumber(local_handout.getCourse_code());
                    Handout handout = new Handout(
                            local_handout.getCourse_code()
                            , book_title
                            , student.getAdm_no(), student.getStudent_faculty()
                            , course_abrv
                            , CoverSuplier.getLevelFromCode(code)
                            , local_handout.getCover()
                            , local_handout.getCover_type()
                            , total_number_of_pages
                    );
                    handout.setIs_gst(CoverSuplier.isHandoutGst(course_abrv));
                    myBook_fragment.getHandoutOperation().UploadHandout(handout, Uri.fromFile(file), new FirebaseHandoutOperation.OnHandoutUpload() {
                        @Override
                        public void onHandoutUploaded(Handout handout) {
                            if (file.delete()) {
                                Log.i(ZIPTAG, "the shared zip file was deleted");
                                dialog.dismiss();
                            }
                            GotoOnlineFragment(myBook_fragment);
                        }

                        @Override
                        public void onHandoutUploadFailed(Handout handout, Exception exception) {
                            dialog.dismiss();
                            Toast.makeText(myBook_fragment.requireContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onStudentInfoRetrivalFailed(DatabaseError databaseError) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void GotoOnlineFragment(MyBook_fragment myBook_fragment) {
        MainActivity mainActivity = (MainActivity) myBook_fragment.requireActivity();
        mainActivity.getPager().setCurrentItem(MainActivity.ONLINE_BOOK_INDEX);
    }
}
