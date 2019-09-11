package com.example.abumuhsin.udusmini_library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.Gallery_recyler_adapter;
import com.example.abumuhsin.udusmini_library.utils.DeviceGalleryAsyncTask;
import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;

public class MyGalleryActivity extends AppCompatActivity implements Gallery_recyler_adapter.OnFolderCLickListener, DeviceGalleryAsyncTask.OnLoadingImages {

    private RecyclerView gallery_RecyclerView;
    public ArrayList<Model_images> images_folder;
    private Gallery_recyler_adapter gallery_recyler_adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_book_recycler_layout);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        new DeviceGalleryAsyncTask(this, this).execute();
//        DeviceGalleryLoaderTask.get(this).LoadDirrectoryFromStorage();
    }

    private void init() {
        intiViews();
        initAdapters();
    }

    private void intiViews() {
        gallery_RecyclerView = findViewById(R.id.recycler);
    }

    private void initAdapters() {
        images_folder = new ArrayList<>();
        gallery_recyler_adapter = new Gallery_recyler_adapter(this, images_folder, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
//        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        gallery_RecyclerView.setLayoutManager(gridLayoutManager);
        gallery_RecyclerView.setAdapter(gallery_recyler_adapter);
    }

    public static final boolean FROM_GALLERY = true;
    public static final String MSG_EXTRA = "msg_indicator";
    public static final String BOOK_NAME_EXTRA = "msg_book";
    public static final String IMAGES_EXTRA = "msg_images";
    public static final String GALLERY_TAG = "galleryDEBUG";

    @Override
    public void onFolderClick(String folder_name, int position, ArrayList<String> images) {
        Intent intent = new Intent(this, FlipBooKActivity.class);
        intent.putExtra(MSG_EXTRA, FROM_GALLERY);
        intent.putExtra(BOOK_NAME_EXTRA, folder_name);
        intent.putExtra(IMAGES_EXTRA, images);
        Log.i(GALLERY_TAG, "before starting Flip Activity");
        startActivity(intent);
        Log.i(GALLERY_TAG, "Flip Activity Started");
    }

    @Override
    public void onImageFolderAdded(String folder, ArrayList<String> images, boolean is_folder, int int_position) {
        if (is_folder) {
            images_folder.get(int_position).setAl_imagepath(images);
        }else {
            Model_images model_images = new Model_images();
            model_images.setAl_imagepath(images);
            model_images.setStr_folder(folder);
            images_folder.add(model_images);

        }
    }


    @Override
    public void onLoadFinished() {
        gallery_recyler_adapter.notifyDataSetChanged();
    }
}
