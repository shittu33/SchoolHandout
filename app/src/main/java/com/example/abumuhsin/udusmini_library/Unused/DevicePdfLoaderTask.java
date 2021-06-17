//package com.example.abumuhsin.udusmini_library.Unused;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.webkit.MimeTypeMap;
//
//
//public final class DevicePdfLoaderTask implements Runnable {
//    private static DevicePdfLoaderTask _object;
//    private Thread pdf_thread;
//    private Context context;
//    public boolean do_stop_thread = false;
//    public OnLoadingPdfs onLoadingPdfs;
//
//
//    public static DevicePdfLoaderTask get(Context context, OnLoadingPdfs onLoadingPdfs) {
//        if (_object == null) {
//            _object = new DevicePdfLoaderTask(context,onLoadingPdfs);
//        }
//        return _object;
//    }
//
//    private DevicePdfLoaderTask(Context context,OnLoadingPdfs onLoadingPdfs) {
//        this.context = context;
//        this.onLoadingPdfs = onLoadingPdfs;
//    }
//
////    ArrayList<String> pdf_paths = new ArrayList<>();
//
//    public void LoadPdfsFromStorage() {
//        do_stop_thread = false;
//        pdf_thread = new Thread(this);
//        Log.i("pdf_bitmap", "before starting the Loadthread");
//        pdf_thread.start();
//        Log.i("pdf_bitmap", "after starting the Loadthread");
//    }
//
//    @Override
//    public void run() {
//        int int_position = 0;
//        Uri uri;
//        Cursor cursor;
//        int column_index_data, column_index_folder_name;
//        boolean boolean_folder = false;
//        String absolutePathOfPdfs;
////        String path_of_folder;
//
//        uri = MediaStore.Files.getContentUri("external");
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
//        String[] selectionArgsPdf = new String[]{mimeType};
////        if (context != null && context.isAdded()) {
//            cursor = context.getContentResolver().query(uri, projection, selectionMimeType, selectionArgsPdf, orderBy + " DESC");
//            if (cursor != null) {
//                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
////                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
////                pdf_paths.clear();
//                while (cursor.moveToNext()) {
//                    absolutePathOfPdfs = cursor.getString(column_index_data);
//                    onLoadingPdfs.onPdfAdded(absolutePathOfPdfs);
////                    pdf_paths.add(absolutePathOfPdfs);
////                Log.i(GALLERY_TAG, "original path is " + absolutePathOfPdfs);
////                    path_of_folder = cursor.getString(column_index_folder_name);
//                }
//                onLoadingPdfs.onLoadFinished();
////                if (context != null && context.isAdded()) {
////                    context.getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (context != null && context.isAdded()) {
////                                context.pdf_paths.clear();
////                                for (String pdf : pdf_paths) {
////                                    context.updateList(pdf);
////                                                            Log.i(GALLERY_TAG, "duplicate path is " + pdf);
////                                }
////                            }
////                        }
////                    });
////                }
////            Log.i(GALLERY_TAG, "before cursor is close");
//                cursor.close();
////            Log.i(GALLERY_TAG, "cursor is closed");
//
////            } else {
////            Log.i(GALLERY_TAG, "cursor is null");
////            }
//        }
//    }
//
//    public interface OnLoadingPdfs {
//        void onPdfAdded(String pdf_name);
//        void onLoadFinished();
////        boolean onPdfAdded(String pdf_name);
//    }
//}
