package com.example.abumuhsin.udusmini_library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask.ZIPTAG;

/**
 * Created by Abu Muhsin on 09/11/2018.
 */

public class FileUtils {

    public static void UnZipFile(File src_zip_file, String dest_path) throws IOException {
//        String fileZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File(dest_path);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(src_zip_file.getPath()));
        ZipEntry zipEntry = zis.getNextEntry();
        Log.i(ZIPTAG, "b4 while");
        while (zipEntry != null) {
            Log.i(ZIPTAG, "zip entry is not null");
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                Log.i(ZIPTAG, "file zip in progress");
                fos.write(buffer, 0, len);
            }
            fos.close();
            Log.i(ZIPTAG, "file zip is closed");
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    public static File unzipStream(InputStream inputStream, File targetDirectory) throws IOException {
        File zip_parent_dir = null;
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(inputStream));
        try {
            ZipEntry ze;
            int count;
            int index =0;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                if (index==0)zip_parent_dir=file;
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                }
                index++;
                if (ze.isDirectory()) {
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
        return zip_parent_dir;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void ZipFile(String src_path, File dest_file) throws IOException {
        FileOutputStream fos = new FileOutputStream(dest_file);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(src_path);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public void ZipMultipeFile(List<String> srcPaths, String dest_path, OnMultipleZipListener onMultipleZipListener) throws IOException {
//        List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
        FileOutputStream fos = new FileOutputStream(dest_path);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        int i = 0;
        for (String srcPath : srcPaths) {
            onMultipleZipListener.onMultipleZip_progress(i);
            File fileToZip = new File(srcPath);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
            i++;
        }
        zipOut.close();
        fos.close();
    }

    public static void zipDirectory(File fileToZip, String fileName, ZipOutputStream zipOut, OnDirectoryZipListener onDirectoryZipListener, int i) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        onDirectoryZipListener.onDirectoryZipProgress(i);
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipDirectory(childFile, fileName + "/" + childFile.getName(), zipOut, onDirectoryZipListener, i++);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static void share_Zip(Activity context, File zip_file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zip_file));
        intent.setType("application/zip");
//        intent.setDataAndType(Uri.fromFile(pdf_file),"application/pdf");
        Intent.createChooser(intent, "share " + zip_file.getName() + "with...");
        context.startActivity(intent);
    }

    public interface OnMultipleZipListener {
        void onMultipleZip_progress(int i);
    }

    public interface OnDirectoryZipListener {
        void onDirectoryZipProgress(int i);
    }

    public static int calculateSampleSize(BitmapFactory.Options options, int destWidth, int destHeight) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    public static Bitmap getDownSizedBitmapFromPath(String path, int width, int height) {
        Bitmap selectedBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options, width, height);
        selectedBitmap = BitmapFactory.decodeFile(path, options);
        return selectedBitmap;
    }


    public static String getDatabasePath(String database_name) {
        String database_path = Environment.getExternalStorageDirectory()
                + File.separator + "UdusHandout" + File.separator
                + "Databases";
        File database_dir = new File(database_path);
        if (!database_dir.exists()) //noinspection ResultOfMethodCallIgnored
            database_dir.mkdirs();
        return database_dir.getAbsolutePath() + File.separator + database_name;
    }

    public static String getUdusAppRootDirectory() {
        File root = new File(Environment.getExternalStorageDirectory(), "UdusHandout");
        if (!root.exists()) {
            root.mkdir();
        }
        return root.getAbsolutePath();
    }

    public static String getBookFilesPath(String book_name) {
        String book_path = Environment.getExternalStorageDirectory()
                + File.separator + "UdusHandout" + File.separator
                + "Book Files" + File.separator + book_name;
        File book_dir = new File(book_path);
        if (!book_dir.exists()) //noinspection ResultOfMethodCallIgnored
            book_dir.mkdirs();
        return book_dir.getAbsolutePath();
    }

    public static String getBookCoverPath(String book_name) {
        return getBookFilesPath(book_name)+ File.separator + "cover_page.cover";
    }

    public static String getBooksFilesPath() {
        String book_path = Environment.getExternalStorageDirectory()
                + File.separator + "UdusHandout" + File.separator
                + "Book Files";
        File book_dir = new File(book_path);
        if (!book_dir.exists()) //noinspection ResultOfMethodCallIgnored
            book_dir.mkdirs();
        return book_dir.getAbsolutePath();
    }

    public static String getBucketFilesPath(String book_name) {
        String bucket_path = getBookFilesPath(book_name) + File.separator + "Bucket_images";
        File bucket_dir = new File(bucket_path);
        if (!bucket_dir.exists()) //noinspection ResultOfMethodCallIgnored
            bucket_dir.mkdirs();
        return bucket_dir.getAbsolutePath();
    }

    public static String getImagePagesFilePath(String book_name) {
        String pages_file_path = getBookFilesPath(book_name) + File.separator + "Image_pages";
        File pages_dir = new File(pages_file_path);
        if (!pages_dir.exists()) //noinspection ResultOfMethodCallIgnored
            pages_dir.mkdirs();
        return pages_dir.getAbsolutePath();
    }


    public static String getPdfsFilePath() {
        String path = getUdusAppRootDirectory() + File.separator + "PDFs";

        File book_dir = new File(path);
        if (!book_dir.exists()) //noinspection ResultOfMethodCallIgnored
            book_dir.mkdirs();
        return book_dir.getAbsolutePath();
    }

    public static String getDownloadFilePath() {
        String path = getUdusAppRootDirectory() + File.separator + "Downloaded_Books";
        File book_dir = new File(path);
        if (!book_dir.exists()) //noinspection ResultOfMethodCallIgnored
            book_dir.mkdirs();
        return book_dir.getAbsolutePath();
    }

    public static String getUploadFilePath() {
        String path = getUdusAppRootDirectory() + File.separator + "Uploaded_Books";
        File book_dir = new File(path);
        if (!book_dir.exists()) //noinspection ResultOfMethodCallIgnored
            book_dir.mkdirs();
        return book_dir.getAbsolutePath();
    }

    public static final int CAMERA = 0;
    public static final int GALLERY = 1;
    public static final int PDFS = 2;

    public static String getRealDateAndTime(int type) {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == CAMERA) {
            return "CAMERA_" + timeStamp + "_";
        } else if (type == GALLERY) {
            return "GALLERY_" + timeStamp + "_";
        }
        return "PDF_" + timeStamp + "_";
    }


    public static File saveImage(Bitmap bitmap, File saveFile, int quality) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
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

    public static File saveScaledImage(File dest, File src, int require_width, int require_height) {
        Bitmap bitmap = getDownSizedBitmapFromPath(src.getPath(), require_width, require_height);
        return saveImage(bitmap, dest, 100);
    }

    public static File saveScaledImageSpecific(File dest, File src) {
        Bitmap bitmap = getDownSizedBitmapFromPath(src.getPath(), 800, 800);
        return saveImage(bitmap, dest, 100);
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("uri", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap takeScreenshot(View view,Context context) {
        int width = view.getWidth();
        int height = view.getHeight();
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(measuredWidth,measuredHeight );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        if (measuredWidth > 0 && measuredHeight> 0) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

}
