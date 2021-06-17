//package com.example.abumuhsin.udusmini_library.Unused;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.util.Log;
//
//import androidx.fragment.app.FragmentActivity;
//
//import com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment;
//import com.example.amazing_picker.models.Model_images;
//
//import java.util.ArrayList;
//
//import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;
//
//
//public final class DeviceGalleryLoaderTask implements Runnable {
//    private static DeviceGalleryLoaderTask _object;
//    private Thread pdf_thread;
//    private GalleryBook_fragment galleryBook_fragment;
//    public boolean do_stop_thread = false;
//
//
//    public static DeviceGalleryLoaderTask get(GalleryBook_fragment context) {
//        if (_object == null) {
//            _object = new DeviceGalleryLoaderTask(context);
//        }
//        return _object;
//    }
//
//    private DeviceGalleryLoaderTask(GalleryBook_fragment context) {
//        this.galleryBook_fragment = (GalleryBook_fragment) context;
//    }
//
//    //String tmp_book;
//
//    public void LoadDirrectoryFromStorage() {
//        do_stop_thread = false;
//        pdf_thread = new Thread(this);
//        Log.i("pdf_bitmap", "before starting the Loadthread");
//        pdf_thread.start();
//        Log.i("pdf_bitmap", "after starting the Loadthread");
//    }
//
//
//    @Override
//    public void run() {
//        final ArrayList<Model_images> al_images_and_folder = new ArrayList<>();
//        int int_position = 0;
//
//        Uri uri;
//        Cursor cursor;
//        int column_index_data, column_index_folder_name;
//        boolean boolean_folder = false;
//        String absolutePathOfImage;
//        String path_of_folder;
//
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
//        if (galleryBook_fragment != null && galleryBook_fragment.isAdded()) {
//            Context context = galleryBook_fragment.requireContext();
//            cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
//            if (cursor != null) {
//                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//                galleryBook_fragment.images_folder.clear();
//                while (cursor.moveToNext()) {
//                    absolutePathOfImage = cursor.getString(column_index_data);
//                    path_of_folder = cursor.getString(column_index_folder_name);
////                Log.e(PIC_TAG, path_of_folder);
//                    for (int i = 0; i < al_images_and_folder.size(); i++) {
//                        if (al_images_and_folder.get(i).getStr_folder().equals(path_of_folder)) {
////                        Log.e(PIC_TAG, "inside loop" + "index i is " + i);
//                            boolean_folder = true;
//                            int_position = i;
//                            break;
//                        } else {
//                            boolean_folder = false;
//                        }
//                    }
//
//
//                    if (boolean_folder) {
////                    Log.e(PIC_TAG, "folder named " + path_of_folder + " already exist");
//                        ArrayList<String> al_path = new ArrayList<>(al_images_and_folder.get(int_position).getAl_imagepath());
////                    Log.e(PIC_TAG, "adding it images....");
//                        al_path.add(absolutePathOfImage);
////                    Log.e(PIC_TAG, absolutePathOfImage);
//                        al_images_and_folder.get(int_position).setAl_imagepath(al_path);
//                    } else {
//                        ArrayList<String> al_path = new ArrayList<>();
//                        al_path.add(absolutePathOfImage);
//                        final Model_images obj_model = new Model_images();
//                        obj_model.setStr_folder(path_of_folder);
////                    Log.e(PIC_TAG, "folder named " + path_of_folder + " was added");
//                        obj_model.setAl_imagepath(al_path);
//                        al_images_and_folder.add(obj_model);
//                        FragmentActivity activity = galleryBook_fragment.getActivity();
//                        if (galleryBook_fragment != null && galleryBook_fragment.isAdded()) {
//                            assert activity != null;
//                            activity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    galleryBook_fragment.updateList(obj_model);
//                                }
//                            });
//                        }
//                    }
//                }
////            Log.i(PIC_TAG, "before cursor is close");
//                cursor.close();
////            Log.i(PIC_TAG, "cursor is closed");
//            } else {
////            Log.i(PIC_TAG, "cursor is null");
//            }
//        }
//    }
//
//
//    public void stop_pdfThread_and_delete_tmp_pages() {
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
//                } catch (InterruptedException e) {
//                }
//
//            }
//        }
//        if (pdf_thread.isAlive()) {
//            Log.d(PIC_TAG, "Thread is still alive after waited 1.5s!");
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        //delete even when the Thread is dead.
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
//}
