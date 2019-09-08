package com.example.amazing_picker.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.amazing_picker.utilities.FileUtils.PDFS;


public class PdfUtils {
    private static final String TAG = "pdf_debug";

    public static int getPdf_page_no(String pdf_page_path) {
        String[] root = pdf_page_path.split("_");
        String num = root[root.length - 1].replace("." + PDFS, "");
        String short_num = "";
        if (num.startsWith("0000")) {
            short_num = Character.toString(num.charAt(4));
        } else if (num.startsWith("000")) {
            short_num = num.substring(3, 5);
        } else if (num.startsWith("00")) {
            short_num = num.substring(2, 5);
        } else if (num.startsWith("0")) {
            short_num = num.substring(1, 5);
        }
        return Integer.valueOf(short_num);
    }

    public static int getPdfPage_count(Context context, String pdf_path) {
        int count = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        PdfDocument pdfDocument = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor
                    .open(new File(pdf_path), ParcelFileDescriptor.MODE_READ_ONLY);
            pdfDocument = pdfiumCore.newDocument(parcelFileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pdfDocument != null) {
            count = pdfiumCore.getPageCount(pdfDocument);
        }
        return count;
    }


    public synchronized static File SaveZoomablePdfPage_to(Context context, String src_pdf_path, String dest_path, int i) {
        File dest_file = new File(dest_path);
        if (dest_file.exists()) {
            return dest_file;
        }
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor
                    .open(new File(src_pdf_path), ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fd != null) {
                PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                pdfiumCore.openPage(pdfDocument, i);
                int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
                int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
                Bitmap bitmap1 = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
//            int width = pdfiumCore.getPageWidth(pdfDocument, i);
//            int height = pdfiumCore.getPageHeight(pdfDocument, i);
//            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);
//            float factor = screenWidth / (float) bitmap1.getWidth();
//            Bitmap bitmap = Bitmap.createScaledBitmap(bitmap1, screenWidth, (int) (bitmap1.getHeight() * factor), true);
                int dest_width = View_Utils.getScreenResolution(context).width * 2;
                int dest_height = View_Utils.getScreenResolution(context).height * 2;
                Bitmap bitmap = resize(bitmap1, dest_width, dest_height);
                pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight());
                Log.e(TAG, "before saving page... ");
                saveImage(bitmap, dest_file);
                Log.e(TAG, "bitmap page " + i + " was saved");

                pdfiumCore.closeDocument(pdfDocument); // important!
            } else {
                Log.i(TAG, "descriptor is null for path " + src_pdf_path);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (dest_file.exists()) {
            return dest_file;
        }
        return null;
    }

    public static final String PDF_PATH_DEBUG = "pdf_path_debug";

    public static String getActivePdfPagePath(String pdf_path, int page_no) {

        String root = Environment.getExternalStorageDirectory()
                + File.separator + "Temp_pdf_pages";

        File book_dir = new File(root);
        if (!book_dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            Log.i(PDF_PATH_DEBUG, "root dir doesnt exist and it will be created now");
            if (book_dir.mkdirs())
                Log.i(PDF_PATH_DEBUG, "book_dir of path" + book_dir.getName() + "is created" );
        }
        String num = String.valueOf(page_no);
        String long_num = "_";
        if (num.length() == 1) {
            long_num += "0000" + num;
        } else if (num.length() == 2) {
            long_num += "000" + num;
        } else if (num.length() == 3) {
            long_num += "00" + num;
        } else if (num.length() == 4) {
            long_num += "0" + num;
        }
        Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  pdf_path is " + pdf_path);
        File file = new File(root, "PDF_" + new File(pdf_path).getName() + "_page " + long_num + "." + PDFS);
        if (file.exists()) {
            Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  final pdf__page_path is " + file.getPath());
        } else {
            Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  final pdf__page_path " + file.getPath() + "doesn't exist yet");
        }
        return file.getPath();
    }

    public static File saveImage(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (saveFile.exists()) {
            return saveFile;
        }
        return null;
    }


    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Bitmap getPdfPageBitmapWithLow_quality(Context context, String src_pdf_path, int i) {
        Bitmap bitmap = null;
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor
                    .open(new File(src_pdf_path), ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, i);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
//            int dest_width = View_Utils.getScreenResolution(context).width ;
//            int dest_height = View_Utils.getScreenResolution(context).height ;
            bitmap = Bitmap.createBitmap(/*width*/width, height/*height*/,
                    Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
                    /*getPdfScaledwidth(context,width)*/width, height/*getPdfScaledheight(context,height)*/);
            Log.e(TAG, "before saving page... ");
            Log.e(TAG, "bitmap page " + i + " was saved");

            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

}
