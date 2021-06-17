//package com.example.abumuhsin.udusmini_library.utils;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.util.Log;
//
//import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
//import com.shockwave.pdfium.PdfDocument;
//import com.shockwave.pdfium.PdfiumCore;
//
//import java.io.File;
//import java.util.LinkedList;
//
//import static com.example.abumuhsin.udusmini_library.utils.FileUtils.saveImage;
//import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_File_for_ActivePdf;
//import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getActivePdf_ImagePath;
//import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfScaledheight;
//import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfScaledwidth;
//import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdf_page_no;
//import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;
//
//public final class PdfForwardLoader implements Runnable {
//    private static PdfForwardLoader _object;
//    private Thread pdf_thread;
//    private FlipBooKActivity activity;
//    private String pdf_path;
//    private String page_path;
//    private int requested_index;
//    private int buffer_index;
//    boolean is_first;
//    public boolean is_firstLoaded;
//    private static final int buffer_size = 20; // 10 front, 10 back
//    private LinkedList<String> buffer_page_pathForward = new LinkedList<>();
//    private LinkedList<String> buffer_page_pathBackward = new LinkedList<>();
//    private int pdf_page_count;
//    public boolean do_stop_thread = false;
//
//
//    public static PdfForwardLoader get(Activity context) {
//        if (_object == null) {
//            _object = new PdfForwardLoader(context);
//        }
//        return _object;
//    }
//
//    private PdfForwardLoader(Activity context) {
//        if (context instanceof FlipBooKActivity) {
//            this.activity = (FlipBooKActivity) context;
//        }
//    }
//
//    PdfBackLoaderTask.OnPdfPageDownloadListener onPdfPageDownloadListener;
//
//    public void setOnPdfPageDownloadListener(PdfBackLoaderTask.OnPdfPageDownloadListener onPdfPageDownloadListener) {
//        this.onPdfPageDownloadListener = onPdfPageDownloadListener;
//    }
//
//    public synchronized void LoadPdfToStorage(String pdf_path, String page_path, boolean is_first) {
//        Log.i("pdf_bitmap", "path " + pdf_path + " is copied");
//        this.pdf_path = pdf_path;
////        this.page_path = page_path;
//        requested_index = getPdf_page_no(page_path);
//        this.is_first = is_first;
//        do_stop_thread = false;
//        pdf_thread = new Thread(this);
//        pdf_thread.start();
//    }
//
//    public int getPdf_page_count() {
//        return pdf_page_count;
//    }
//
//
//    @Override
//    public void run() {
//        LoadForward();
//    }
//
//
//    private void LoadForward(/*PdfDocument pdfDocument, PdfiumCore pdfiumCore*/) {
//        pdf_page_count = PdfUtils.getPdfPage_count(activity, pdf_path);
//        String pdf_name = new File(pdf_path).getName();
//        final int prev_index = requested_index >= 0 ? requested_index : 0;// ex 26-3 = 23
//        int start_index = pdf_page_count - 1 >= prev_index ? prev_index : pdf_page_count;
//        final int reload_index = pdf_page_count - 1 >= start_index + 1 ? start_index + 1 : pdf_page_count;
//        Log.e("pdf_bitmap", "start index forward is " + start_index);
//        for (int i = start_index; i < pdf_page_count; i++) {
//            File page_file = /*create_new_File_for_ActivePdf(pdf_name, pdf_name, i)*/null;
//
////            if (activity.other_app_intent != null) {
////                page_file = create_new_File_for_ActivePdf(pdf_name + "_dir", i);
////            } else {
//                page_file = create_new_File_for_ActivePdf(pdf_name, i);
////            }
//            if (do_stop_thread) {
//                Log.e("pdf_bitmap", "Thread Stopped");
//                break;
//            }
//            if (!page_file.exists()) {
//                final File saved_page = PdfUtils.SavePdfPage_to(activity, pdf_path, page_file.getPath(), i);
//                if (saved_page != null) {
//                    final int page_no = i;
//                    Runnable runnable = new Runnable() {
//                        public void run() {
//                            onPdfPageDownloadListener.onPdfPageDownloaded(page_no, saved_page.getPath());
//                        }
//                    };
//                    activity.runOnUiThread(runnable);
//                }
//                Log.e("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
//            }
////            if (i == reload_index /*|| i==reload_index+1*/) {
////                activity.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        Runnable runnable = new Runnable() {
////                            public void run() {
////                                Log.i("pdf_bitmap", "requested index: about to request " + requested_index + "/" + pdf_page_count);
//////                                activity.mAdapterFlipView.setSelection(requested_index);
////                                activity.ManuallyRefresh_Page(50);
////                                Log.i("pdf_bitmap", "setUpAdapterFlipView: requested index is" + requested_index + "/" + pdf_page_count);
////                            }
////                        };
////                        new android.os.Handler().post(runnable);
////                        Log.e("pdf_bitmap", "First: 3 pages saved");
////                    }
////                });
////            }
//        }
//    }
//
//
//    Bitmap getBitmapFromPdf(PdfDocument pdfDocument, PdfiumCore pdfiumCore, int i) {
//        pdfiumCore.openPage(pdfDocument, i);
//        int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
//        int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
//
//        Bitmap bitmap = Bitmap.createBitmap(getPdfScaledwidth(activity, width), getPdfScaledheight(activity, height),
//                Bitmap.Config.ARGB_8888);
//        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
//                getPdfScaledwidth(activity, width), getPdfScaledheight(activity, height), true);
//        return bitmap;
//    }
//
//    private void LoadBackward(PdfDocument pdfDocument, PdfiumCore pdfiumCore) {
//        pdf_page_count = pdfiumCore.getPageCount(pdfDocument);
//        Log.i("pdf_bitmap", "backward ");
//        if (requested_index >= 0) {
//            Log.i("pdf_bitmap", " requested_index is > 0");
//            int start_index = requested_index - 1;
//            for (int i = start_index; i >= 0; i--) {
//                Log.i("pdf_bitmap", " pdf of index " + i);
//                String pdf_name = new File(pdf_path).getName();
//                File page_file = create_new_File_for_ActivePdf(pdf_name, i);
//                if (do_stop_thread) {
//                    Log.i("pdf_bitmap", "Thread Stopped");
//                    break;
//                }
//                if (!page_file.exists()) {
//                    saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, i), page_file, 100);
//                    Log.i("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
//                    Log.i("pdf_bitmap", "page file path is" + page_file.getPath());
//                }
//            }
//        }
//    }
//
//    public synchronized void UpdateBuffer(PdfDocument pdfDocument, PdfiumCore pdfiumCore) {
//        if (requested_index == buffer_index + 1) {
//            //forward
//            Log.e("pdf_bitmap", " you flip forward");
//            buffer_index = requested_index;
//            if (buffer_page_pathBackward.size() >= 10) {
//                //you can delete from back
//                File first_file = new File(buffer_page_pathBackward.getFirst());
//                if (first_file.exists()) {
//                    if ((first_file.delete())) {
//                        buffer_page_pathBackward.removeFirst();
//                        Log.e("pdf_bitmap", " first file deleted");
//                    }
//                }
//            }
//            //add a file forward
//            String pdf_name = new File(pdf_path).getName();
//            int page_no = buffer_page_pathForward.indexOf(buffer_page_pathForward.getLast()) + 1;
//            File dest_file = create_new_File_for_ActivePdf(pdf_name, page_no);
//            if (!dest_file.exists()) {
//                saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, page_no), dest_file, 100);
//                buffer_page_pathForward.add(dest_file.getPath());
//                Log.e("pdf_bitmap", " page of path \n " + dest_file.getPath() + " was added to the end");
//            }
//        } else if (requested_index == buffer_index - 1) {
//            //backward
//            Log.e("pdf_bitmap", " you flip backward");
//            buffer_index = requested_index;
//            if (buffer_page_pathForward.size() >= 10) {
//                //you can delete from front
//                File last_file = new File(buffer_page_pathForward.getLast());
//                if (last_file.exists()) {
//                    if ((last_file.delete())) {
//                        buffer_page_pathForward.removeLast();
//                    }
//                }
//            }
//            //add a file backward
//            String pdf_name = new File(pdf_path).getName();
//            int page_no = buffer_page_pathBackward.indexOf(buffer_page_pathBackward.getFirst()) - 1;
//            File dest_file = create_new_File_for_ActivePdf(pdf_name, page_no);
//            if (!dest_file.exists()) {
//                saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, page_no), dest_file, 100);
//                buffer_page_pathBackward.addFirst(dest_file.getPath());
//            }
//        } else if (requested_index <= 0) {
//            //do nothing;
//            buffer_index = requested_index;
//        }
//    }
//
//
//    public void FirstLoad(PdfDocument pdfDocument, PdfiumCore pdfiumCore) {
//        pdf_page_count = pdfiumCore.getPageCount(pdfDocument);
//        Log.e("pdf_bitmap", "after pdf_page_count" + pdf_page_count);
//
//        if (requested_index > 0) {
////                if (pdf_page_count>requested_index) {
//            int backward_start = requested_index - 10 >= 0 ? requested_index - 10 : 0;
//            int backward_count = pdf_page_count >= requested_index ? requested_index : pdf_page_count;
//            for (int i = backward_start; i < backward_count; i++) {
//                String pdf_name = new File(pdf_path).getName();
//                File page_file = create_new_File_for_ActivePdf(pdf_name, i);
//                if (do_stop_thread) {
//                    Log.e("pdf_bitmap", "Thread Stopped");
//                    break;
//                }
//                if (!page_file.exists()) {
//                    saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, i), page_file, 100);
//                    Log.e("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
//                    Log.e("pdf_bitmap", "page file path is" + page_file.getPath());
//                    buffer_page_pathBackward.add(page_file.getPath());
//                }
//
//            }
//            if (pdf_page_count - 1 >= backward_count) {
//                int forward_count = pdf_page_count >= (backward_count + 11) ? (backward_count + 11) : pdf_page_count;
//                for (int i = backward_count; i < forward_count; i++) {
//                    String pdf_name = new File(pdf_path).getName();
//                    File page_file = create_new_File_for_ActivePdf(pdf_name, i);
//                    if (do_stop_thread) {
//                        Log.e("pdf_bitmap", "Thread Stopped");
//                        break;
//                    }
//                    if (!page_file.exists()) {
//                        saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, i), page_file, 100);
//                        Log.e("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
//                        Log.e("pdf_bitmap", "page file path is" + page_file.getPath());
//                        buffer_page_pathForward.add(page_file.getPath());
//                    }
//
//                }
//            }
//        } else {
//            int forward_count = pdf_page_count >= (requested_index + 20) ? requested_index + 20 : pdf_page_count;
//            for (int i = requested_index; i < forward_count; i++) {
//                String pdf_name = new File(pdf_path).getName();
//                File page_file = create_new_File_for_ActivePdf(pdf_name, i);
//                if (do_stop_thread) {
//                    Log.e("pdf_bitmap", "Thread Stopped");
//                    break;
//                }
//                if (!page_file.exists()) {
//                    saveImage(getBitmapFromPdf(pdfDocument, pdfiumCore, i), page_file, 100);
//                    Log.e("pdf_bitmap", "page " + i + " size = " + pdf_page_count);
//                    Log.e("pdf_bitmap", "page file path is" + page_file.getPath());
//                    buffer_page_pathForward.add(page_file.getPath());
//                }
//
//            }
//        }
//        is_firstLoaded = true;
//    }
//
//    public void stop_pdfThread_and_delete_tmp_pages() {
//        if (is_pdf_thread_objectRunning()) {
//            synchronized (this) {
//                do_stop_thread = true;
//                notify();
//                deletePdfsWhenExit(pdf_path);
//            }
//
//            // wait for thread stopping
//            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
//                Log.d(PIC_TAG, "Waiting thread to stop ...");
//                try {
//                    Thread.sleep(500);
//                    deletePdfsWhenExit(pdf_path);
//                } catch (InterruptedException e) {
//                }
//
//            }
//        }
//        if (pdf_thread.isAlive()) {
//            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
//            try {
//                Thread.sleep(300);
//                deletePdfsWhenExit(pdf_path);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        //delete even when the Thread is dead.
//        deletePdfsWhenExit(pdf_path);
//    }
//
//    public void JustStop_pdfThread() {
//        if (is_pdf_thread_objectRunning()) {
//            synchronized (this) {
//                do_stop_thread = true;
//                notify();
//            }
//
//            // wait for thread stopping
//            for (int i = 0; i < 3 && pdf_thread.isAlive(); ++i) {
//                Log.d(PIC_TAG, "Waiting thread to stop ...");
//                try {
//                    Thread.sleep(500);
//                    do_stop_thread = true;
//                } catch (InterruptedException e) {
//                }
//
//            }
//        }
//        if (pdf_thread.isAlive()) {
//            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
//            try {
//                Thread.sleep(300);
//                do_stop_thread = true;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public boolean is_pdf_thread_objectRunning() {
//        return pdf_thread != null && pdf_thread.isAlive();
//    }
//
//    public boolean is_pdf_thread_notNull() {
//        return pdf_thread != null;
//    }
//
//    private void deletePdfsWhenExit(final String pdf_path) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                for (int i = 0; i < pdf_page_count; i++) {
//                    String pdf_name = new File(pdf_path).getName();
//                    File del_file = new File(getActivePdf_ImagePath(pdf_name, i));
//                    if (del_file.exists()) {
//                        if (del_file.delete()) {
//                            Log.d("pdf_bitmap", String.format("deleted %s of index %d", pdf_name, i));
//                        }
//                    }
//                }
//            }
//        }).start();
//    }
//
//}
