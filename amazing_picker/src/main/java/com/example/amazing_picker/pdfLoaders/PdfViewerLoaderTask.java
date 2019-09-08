package com.example.amazing_picker.pdfLoaders;

import android.app.Activity;
import android.util.Log;

import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.utilities.PdfUtils;

import java.io.File;

import static com.example.amazing_picker.utilities.PdfUtils.getActivePdfPagePath;
import static com.example.amazing_picker.utilities.Pdf_Cursor_loaderUtils.PIC_TAG;

public final class PdfViewerLoaderTask implements Runnable {
    private static PdfViewerLoaderTask _object;
    private Thread pdf_thread;
    private Picker_Activity picker_context;
    private String pdf_path;
    private int pdf_page_count;
    public boolean do_stop_thread = false;


    public static PdfViewerLoaderTask get(Activity context) {
        if (_object == null) {
            _object = new PdfViewerLoaderTask(context);
        }
        return _object;
    }

    private PdfViewerLoaderTask(Activity context) {
        if (context instanceof Picker_Activity) {
            this.picker_context = (Picker_Activity) context;
        }
        pdf_path = picker_context.getPdf_path();
    }


    public void LoadPdfToStorage(String pdf_path) {
        this.pdf_path = pdf_path;
        do_stop_thread = false;
        pdf_thread = new Thread(this);
        pdf_thread.start();
    }

    public int getPdf_page_count() {
        return pdf_page_count;
    }

    @Override
    public void run() {
        pdf_page_count = PdfUtils.getPdfPage_count(picker_context,pdf_path);
        Log.e("pdf_bitmap", "after pdf_page_count" + pdf_page_count);
        for (int i = 0; i < pdf_page_count; i++) {
            if (do_stop_thread) {
                Log.e("pdf_bitmap", "Thread Stopped");
                break;
            }
            File pdf_page_file = PdfUtils.SaveZoomablePdfPage_to(picker_context, pdf_path, getActivePdfPagePath(pdf_path, i), i);
            if (pdf_page_file==null){
                Log.e("pdf_bitmap", "page_path is null ");
            }else {
                Log.e("pdf_bitmap", "page_path is " + pdf_page_file.getName() );
            }
            Log.e("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
        }
//
    }


    public void stop_pdfThread_and_delete_tmp_pages() {
        if (is_pdf_thread_objectRunning()) {
            synchronized (this) {
                do_stop_thread = true;
                notify();
                deletePdfsWhenExit();
            }

            // wait for thread stopping
            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
                Log.d(PIC_TAG, "Waiting thread to stop ...");
                try {
                    Thread.sleep(500);
                    deletePdfsWhenExit();
                } catch (InterruptedException e) {
                }

            }
        }
        if (pdf_thread.isAlive()) {
            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
            try {
                Thread.sleep(300);
                deletePdfsWhenExit();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //delete even when the Thread is dead.
        deletePdfsWhenExit();
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

    private void deletePdfsWhenExit() {
//        if (pdfiumCore!=null && pdfDocument!=null) {
//            pdfiumCore.closeDocument(pdfDocument);
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < pdf_page_count; i++) {

                    File del_file = new File(getActivePdfPagePath(pdf_path, i));
                    if (del_file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        del_file.delete();
                    }
                }
            }
        }).start();
    }

}
