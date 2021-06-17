package com.example.abumuhsin.udusmini_library.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.abumuhsin.udusmini_library.activities.FlipPdfActivity;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;

import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_File_for_ActivePdf;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getActivePdf_ImagePath;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfScaledheight;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfScaledwidth;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdf_page_no;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;

public final class FlipPdfBackLoaderTask implements Runnable {
    private static FlipPdfBackLoaderTask _object;
    private Thread pdf_thread;
    private FlipPdfActivity flipPdfActivity;
    private String pdf_path;
    private int pdf_page_count;
    public boolean do_stop_thread = false;
    private String page_path;


    public static FlipPdfBackLoaderTask get(Activity context) {
        if (_object == null) {
            _object = new FlipPdfBackLoaderTask(context);
        }
        return _object;
    }

    public FlipPdfBackLoaderTask(Activity context) {
        if (context instanceof FlipPdfActivity) {
            this.flipPdfActivity = (FlipPdfActivity) context;
        }
//        pdf_path = flipPdfActivity.pdf_path;
    }

    //String tmp_book;
    int requested_index;

    public void LoadPdfToStorage(String pdf_path, String page_path) {
        Log.i("pdf_bitmap", "path " + pdf_path + " is coopied");
        this.pdf_path = pdf_path;
//        this.tmp_book = page_path;
//        this.page_path = page_path;
        requested_index = getPdf_page_no(page_path);
        do_stop_thread = false;
        pdf_thread = new Thread(this);
        Log.i("pdf_bitmap", "before starting the Loadthread");
        pdf_thread.start();
        Log.i("pdf_bitmap", "after starting the Loadthread");
    }

    public int getPdf_page_count() {
        return pdf_page_count;
    }

    @Override
    public void run() {
        LoadBackward();
    }


    private void LoadBackward(/*PdfDocument pdfDocument, PdfiumCore pdfiumCore*/) {
        pdf_page_count = PdfUtils.getPdfPage_count(flipPdfActivity, pdf_path);
        Log.i("pdf_bitmap", "backward ");
        if (requested_index >= 0) {
            Log.i("pdf_bitmap", " requested_index is > 0");
            int start_index = requested_index - 1 > 0 ? requested_index - 1 : requested_index; //ex 26-1 = 25
            for (int i = start_index; i >= 0; i--) {
                if (do_stop_thread) {
                    Log.i("pdf_bitmap", "Thread Stopped");
                    break;
                }
                String pdf_name = new File(pdf_path).getName();
                File page_file = null;
//                if (flipPdfActivity.other_app_intent!=null) {
//                    page_file = create_new_File_for_ActivePdf(pdf_name+"_dir", i);
//                }else {
                    page_file = create_new_File_for_ActivePdf(pdf_name, i);
//                }
                if (!page_file.exists()) {
                    final File page_saved =PdfUtils.SavePdfPage_to(flipPdfActivity, pdf_path, page_file.getPath(), i);
                    if (page_saved != null) {
                        final int page_no = i;
                        Runnable runnable = new Runnable() {
                            public void run() {
                                onPdfPageDownloadListener.onPdfPageDownloaded(page_no,page_saved.getPath());
                            }
                        };
                        flipPdfActivity.runOnUiThread(runnable);
                    }
//                    saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, i), page_file);
                    Log.i("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
                }

            }
        }
    }
    OnPdfPageDownloadListener onPdfPageDownloadListener;

    public void setOnPdfPageDownloadListener(OnPdfPageDownloadListener onPdfPageDownloadListener) {
        this.onPdfPageDownloadListener = onPdfPageDownloadListener;
    }

    public interface OnPdfPageDownloadListener{
        void onPdfPageDownloaded(int page_no, String page_path);
    }

    Bitmap getBitmapFromPdf(PdfDocument pdfDocument, PdfiumCore pdfiumCore, int i) {
        pdfiumCore.openPage(pdfDocument, i);
        int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
        int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);

        Bitmap bitmap = Bitmap.createBitmap(getPdfScaledwidth(flipPdfActivity, width), getPdfScaledheight(flipPdfActivity, height),
                Bitmap.Config.ARGB_8888);
        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
                getPdfScaledwidth(flipPdfActivity, width), getPdfScaledheight(flipPdfActivity, height), true);
        return bitmap;
    }

    public void stop_pdfThread_and_delete_tmp_pages() {
        if (is_pdf_thread_objectRunning()) {
            synchronized (this) {
                do_stop_thread = true;
                notify();
                deletePdfsWhenExit(pdf_path);
            }

            // wait for thread stopping
            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
                Log.d(PIC_TAG, "Waiting thread to stop ...");
                try {
                    Thread.sleep(500);
                    deletePdfsWhenExit(pdf_path);
                } catch (InterruptedException e) {
                }

            }
        }
        if (pdf_thread.isAlive()) {
            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
            try {
                Thread.sleep(300);
                deletePdfsWhenExit(pdf_path);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //delete even when the Thread is dead.
        deletePdfsWhenExit(pdf_path);
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

    private void deletePdfsWhenExit(final String pdf_path) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < pdf_page_count; i++) {

                    String pdf_name = new File(pdf_path).getName();
                    File del_file = new File(getActivePdf_ImagePath(pdf_name, i));
                    if (del_file.exists()) {
                        if (del_file.delete()) {
                            Log.d("pdf_bitmap", String.format("deleted %s of index %d", pdf_name, i));
                        }
                    }
                }
            }
        }).start();
    }

}
