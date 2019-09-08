//package com.example.amazing_picker;
//
//import android.app.Activity;
//import android.os.AsyncTask;
//
//import com.example.amazing_picker.activities.Picker_Activity;
//import com.example.amazing_picker.models.Model_images;
//import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;
//
//import java.util.ArrayList;
//
//public final class Picker_CursorLoaderTask extends AsyncTask<ArrayList<Model_images>, ArrayList<Model_images>, ArrayList<Model_images>> {
//
//    private static Picker_CursorLoaderTask _object;
//    private Picker_Activity context;
//
//    public static Picker_CursorLoaderTask get(Activity context) {
//        if (_object == null) {
//            _object = new Picker_CursorLoaderTask(context);
//        }
//        return _object;
//    }
//
//    private Picker_CursorLoaderTask(Activity context) {
//        this.context = (Picker_Activity) context;
//    }
//
//    @Override
//    protected ArrayList<Model_images> doInBackground(ArrayList<Model_images>[] arrayLists) {
//        return ImageCursorLoaderUtils.getImages_and_Folders(context);
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<Model_images> folder_data) {
//        super.onPostExecute(folder_data);
//        context.setFolders_and_images(folder_data);
//        context.setUpDirAdapter(context.getFolder_imageList());
//    }
//
//    public void LoadData() {
//        //noinspection unchecked
//        execute();
//    }
//
//    public void StopLoading_Data() {
//        if (!this.isCancelled()) {
//            cancel(true);
//        }
//    }
//}
