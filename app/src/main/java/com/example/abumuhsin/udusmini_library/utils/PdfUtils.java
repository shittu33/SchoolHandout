package com.example.abumuhsin.udusmini_library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.example.abumuhsin.udusmini_library.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity.PDF_PATH_DEBUG;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.PDFS;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getBookFilesPath;

//import com.github.barteksc.pdfviewer.PDFView;
//import com.google.gson.Gson;
//import com.tom_roush.pdfbox.pdmodel.PDDocument;
//import com.tom_roush.pdfbox.pdmodel.PDPage;
//import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
//import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
//import com.tom_roush.pdfbox.rendering.PDFRenderer;
//import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

public class PdfUtils {
    private static final String TAG = "pdf_debug";


    public static ArrayList<Bitmap> pdfToBitmap(Context c, File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            PdfRenderer renderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                for (int i = 0; i < 2; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);

                    int width = /*c.getResources().getDisplayMetrics().densityDpi / 72 **/ page.getWidth();
                    int height = /*c.getResources().getDisplayMetrics().densityDpi / 72 **/ page.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    bitmaps.add(bitmap);

                    // close the page
                    page.close();

                }

                // close the renderer
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;
    }

    public static Bitmap CustomPdf_toBitmap(Context context, ParcelFileDescriptor fd, int page_no) {
        Bitmap bitmap = null;
        Bitmap tmp_bitmap = null;
        int count = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            count = pdfiumCore.getPageCount(pdfDocument);
            pdfiumCore.openPage(pdfDocument, page_no);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, page_no);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, page_no);

            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, page_no, 0, 0,
                    width, height);
            Log.e(TAG, "page " + page_no + " size = " + count);
            tmp_bitmap = bitmap;
            pdfiumCore.closeDocument(pdfDocument); // important!
            pdfDocument = null;
            pdfiumCore = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return tmp_bitmap;
    }


    public static File getActivePdf_ImageFolder(String pdf_path) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfImages/"
                + new File(pdf_path).getName());
    }

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

    public static String getActivePdf_ImagePath(String book_name, int no) {
        book_name =book_name.contains("_dir")?book_name:book_name +"_dir";
        String root = getBookFilesPath(book_name);
        String num = String.valueOf(no);
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
        Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  First pdf_path is " + book_name );
        File file = new File(root, "PDF_" /*+ book_name*/+ "_page " + long_num + "." + PDFS);
        if (file.exists()) {
            Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  final pdf_path is " + file.getPath() );
        }else {
            Log.i(PDF_PATH_DEBUG, " getActivePdf_ImagePath->  this file is not in existent" );
        }
        return file.getPath();
    }

    public static File create_new_File_for_ActivePdf(String book_name, int no) {
        book_name = book_name.contains("_dir") ? book_name : book_name + "_dir";
        String root = getBookFilesPath(book_name);
        String num = String.valueOf(no);
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
        return new File(root, "PDF_" /*+ book_name +*/+ "_page " + long_num + "." + PDFS);
    }

    public synchronized static File create_new_ZoomableFile(String pdf_name, int page_no) {
//        String root = getImagePagesFilePath(/*new File(pdf_name).getName()*/pdf_name);
        String root = getBookFilesPath(pdf_name);
        return new File(root, /*pdf_name +*/ "page_" + page_no + ".zoomable");
    }

//    public void pdfBox_pdf_toBitmap(Context context, File pdf_file, String book_name) {
//
//        Bitmap pageImage;
//        PDFBoxResourceLoader.init(context.getApplicationContext());
//        try {
//            PDDocument document = PDDocument.load(pdf_file);
//            PDFRenderer renderer = new PDFRenderer(document);
//            for (int i = 0; i < document.getNumberOfPages(); i++) {
//                pageImage = renderer.renderImage(i, 1, Bitmap.Config.RGB_565);
//
//                saveImage(pageImage, create_new_File_for_ActivePdf("s", book_name, 1));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void add_image_to_pdfItext(Context context, String new_pdfPath, ArrayList<String> image_paths, OnPdfListener onPdfListener) throws Exception {
        Document document = new Document();
//        String directoryPath = Environment.getExternalStorageDirectory().toString();
//        PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/Itext_sample.pdf")); //  Change pdf's name.
        PdfWriter.getInstance(document, new FileOutputStream(new_pdfPath)); //  Change pdf's name.
        document.open();
        for (int i = 0; i < image_paths.size(); i++) {
            onPdfListener.onPdf_progress(i);
            String image_path = image_paths.get(i);
            Image image = null;  // Change image's name and extension.
            if (!image_path.equals("")) {
                image = Image.getInstance(image_path);
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_page);
                image = Image.getInstance(Bitmap_to_byteArray(bitmap));
            }
            image.setCompressionLevel(9);

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(image);
            Log.i(TAG, "page " + i + "was added");
        }
        document.close();
    }

    public static byte[] Bitmap_to_byteArray(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        return output.toByteArray();

    }

    public interface OnPdfListener {
        void onPdf_progress(int i);
    }

    public static void play_pdf(Activity context, File pdf_file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(pdf_file), "application/pdf");//application/zip
        context.startActivity(intent);
    }

    public static void share_pdf(Activity context, File pdf_file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdf_file));
        intent.setType("application/pdf");
//        intent.setDataAndType(Uri.fromFile(pdf_file),"application/pdf");
        Intent.createChooser(intent, "share " + pdf_file.getName() + "with...");
        context.startActivity(intent);
    }

//    public static void createPdf(String dest_path, String image_path) {
//        PDDocument document = new PDDocument();
//        PDPage page = new PDPage();
//        document.addPage(page);
//        // Create a new font object selecting one of the PDF base fonts
//        PDFont font = PDType1Font.HELVETICA;
//// Or a custom font
////	try {
////	PDType0Font font = PDType0Font.load(document, assetManager.open("MyFontFile.TTF"));
////	} catch(IOException e) {
////	e.printStackTrace();
////	}
//        PDPageContentStream contentStream;
//        try {
//// Define a content stream for adding to the PDF
//            contentStream = new PDPageContentStream(document, page);
//// Write Hello World in blue text
//            contentStream.beginText();
//            contentStream.setNonStrokingColor(15, 38, 192);
//            contentStream.setFont(font, 12);
//            contentStream.newLineAtOffset(100, 700);
//            contentStream.showText("Hello World");
//            contentStream.endText();
//// Draw a green rectangle
//            contentStream.addRect(5, 500, 100, 100);
//            contentStream.setNonStrokingColor(0, 255, 125);
//            contentStream.fill();
//// Draw the falcon base image
//            PDImageXObject ximage = PDImageXObject.createFromFile(image_path, document);
////            PDImageXObject ximage = JPEGFactory.createFromStream(document, in);
//            contentStream.drawImage(ximage, 20, 20);
////// Draw the red overlay image
////            Bitmap alphaImage = BitmapFactory.decodeStream(alpha);
////            PDImageXObject alphaXimage = LosslessFactory.createFromImage(document, alphaImage);
////            contentStream.drawImage(alphaXimage, 20, 20 );
//// Make sure that the content stream is closed:
//            contentStream.close();
//// Save the final pdf document to a file
//            document.save(dest_path);
//            document.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void add_image_to_ExistingPdf(String dest_path, String image_path, int page_no) throws Exception {
//        //Loading an existing document
////        PDFBoxResourceLoader.init(get);
//        File file = new File(dest_path);
////        PDDocument doc = new PDDocument();
//        Log.i("pdf_bitmap", "pdf_path is " + dest_path);
//        PDDocument doc = null;
//        PDPage page = null;
//
////            if (file.exists()) {
////                doc = PDDocument.load(file);
////                page = doc.getPage(page_no<doc.getNumberOfPages()?page_no:doc.getNumberOfPages()-1);
////            }else {
////            }
//        doc = new PDDocument();
//        page = new PDPage();
//        Log.i("pdf_bitmap", "doc is loaded");
//        doc.addPage(page);
//
//        Log.i("pdf_bitmap", "PdfBox: new page added");
//
//        try { //Creating PDImageXObject object
//            PDImageXObject pdImage = PDImageXObject.createFromFile(image_path, doc);
//            Log.i("pdf_bitmap", "PdfBox: pdfbox object created");
//
//            //creating the PDPageContentStream object
//            PDPageContentStream contents = new PDPageContentStream(doc, page);
//            Log.i("pdf_bitmap", "PdfBox: after PDPageContentStream");
//
//            //Drawing the image in the PDF document
//            contents.drawImage(pdImage, 70, 250);
//            Log.i("pdf_bitmap", "PdfBox: Image drawn");
//            //Closing the PDPageContentStream object
//            contents.close();
//
//            //Saving the document
//            doc.save(dest_path);
//            Log.i("pdf_bitmap", "PdfBox: Image saved");
//
//            //Closing the document
//            doc.close();
//
//        } catch (IOException e) {
//            Log.i("pdf_bitmap", "doc failed");
//            e.printStackTrace();
//        }
//
//        //Retrieving the page
////        doc.removePage(page_no);
////        PDPage page = new PDPage();
//
//    }


    public static final int SCALE_FACTOR = 2;

    public static int getPdfScaledwidth(Context context, int width) {
//        int scale = View_Utils.getScreenResolution(context).width / width;
        return width * SCALE_FACTOR;
    }

    public static int getPdfScaledheight(Context context, int height) {
//        int scale = View_Utils.getScreenResolution(context).height / height;
        return height * SCALE_FACTOR;
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

    public static File SavePdfPage_to(Context context, String src_pdf_path, String dest_path, int i) {
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
                        Bitmap.Config.RGB_565);
//            int width = pdfiumCore.getPageWidth(pdfDocument, i);
//            int height = pdfiumCore.getPageHeight(pdfDocument, i);
//            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);
//            float factor = screenWidth / (float) bitmap1.getWidth();
//            Bitmap bitmap = Bitmap.createScaledBitmap(bitmap1, screenWidth, (int) (bitmap1.getHeight() * factor), true);
                int dest_width = View_Utils.getScreenResolution(context).width;
                int dest_height = View_Utils.getScreenResolution(context).height;
                Bitmap bitmap = resize(bitmap1, dest_width, dest_height); // get *2 of this when zoomed
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

    public static Bitmap scalePreserveRatio(Bitmap imageToScale, int destinationWidth,
                                            int destinationHeight) {
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();

            //Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;

            //Use the ratio that will fit the image into the desired sizes
            int finalWidth = (int) Math.floor(width * widthRatio);
            int finalHeight = (int) Math.floor(height * widthRatio);
            if (finalWidth > destinationWidth || finalHeight > destinationHeight) {
                finalWidth = (int) Math.floor(width * heightRatio);
                finalHeight = (int) Math.floor(height * heightRatio);
            }

            //Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);

            //Created a bitmap with desired sizes
            Bitmap scaledImage = Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledImage);

            //Draw background color
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            //Calculate the ratios and decide which part will have empty areas (width or height)
            float ratioBitmap = (float) finalWidth / (float) finalHeight;
            float destinationRatio = (float) destinationWidth / (float) destinationHeight;
            float left = ratioBitmap >= destinationRatio ? 0 : (float) (destinationWidth - finalWidth) / 2;
            float top = ratioBitmap < destinationRatio ? 0 : (float) (destinationHeight - finalHeight) / 2;
            canvas.drawBitmap(imageToScale, left, top, null);

            return scaledImage;
        } else {
            return imageToScale;
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
            bitmap = Bitmap.createBitmap(/*getPdfScaledwidth(context,width)*/width, height/*getPdfScaledheight(context,height)*/,
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

    public static File getPdfFileWith_Stream(Context context, InputStream pdf_stream,File dest_file) {
        try {
//            for reading the pdf Stream...
            FileOutputStream fileOutputStream = new FileOutputStream(dest_file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = pdf_stream.read(bytes)) >= 0) {
                fileOutputStream.write(bytes, 0, length);
            }
            pdf_stream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest_file;
    }
    public static ArrayList<Bitmap> getBitmapsFromPdf(Context context, ParcelFileDescriptor fd, int amount, String book_name) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
//        int pageNum = 0;
        int count = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            count = pdfiumCore.getPageCount(pdfDocument);
            for (int i = 0; i < (count > amount ? amount : count); i++) {
                pdfiumCore.openPage(pdfDocument, i);

                int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
                int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);

                Bitmap bitmap = Bitmap.createBitmap(getPdfScaledwidth(context, width), getPdfScaledheight(context, height),
                        Bitmap.Config.ARGB_8888);
                pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
                        getPdfScaledwidth(context, width), getPdfScaledheight(context, height));
                bitmaps.add(bitmap);
                saveImage(bitmap, create_new_File_for_ActivePdf(book_name, i));
                Log.e(TAG, "page " + i + " size = " + count);

            }
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bitmaps;
    }

    private static void printInfo(PdfiumCore core, PdfDocument doc) {
        PdfDocument.Meta meta = core.getDocumentMeta(doc);
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(core.getTableOfContents(doc), "-");

    }

    private static void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

//    public static void LoadPdf(String pdf_path, PDFView pdfView) {
//        pdfView.fromFile(new File(pdf_path))
//                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                .enableSwipe(true) // allows to block changing pages using swipe
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .defaultPage(0)
////            // allows to draw something on the current page, usually visible in the middle of the screen
////            .onDraw(onDrawListener)
////            // allows to draw something on all pages, separately for every page. Called only for visible pages
////            .onDrawAll(onDrawListener)
////            .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
////            .onPageChange(onPageChangeListener)
////            .onPageScroll(onPageScrollListener)
////            .onError(onErrorListener)
////            .onPageError(onPageErrorListener)
////            .onRender(onRenderListener) // called after document is rendered for the first time
////            // called on single tap, return true if handled, false to toggle scroll handle visibility
////            .onTap(onTapListener)
////            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                .password(null)
//                .scrollHandle(null)
//                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//                // spacing between pages in dp. To define spacing color, set view background
//                .spacing(0)
////            .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
//                .load();
//    }

    public static class DeviceDimensionsHelper {
        // DeviceDimensionsHelper.getDisplayWidth(context) => (display width in pixels)
        public static int getDisplayWidth(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        }

        // DeviceDimensionsHelper.getDisplayHeight(context) => (display height in pixels)
        public static int getDisplayHeight(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        }

        // DeviceDimensionsHelper.convertDpToPixel(25f, context) => (25dp converted to pixels)
        public static float convertDpToPixel(float dp, Context context) {
            Resources r = context.getResources();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        }

        // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
        public static float convertPixelsToDp(float px, Context context) {
            Resources r = context.getResources();
            DisplayMetrics metrics = r.getDisplayMetrics();
            float dp = px / (metrics.densityDpi / 160f);
            return dp;
        }
    }
}
