package com.example.abumuhsin.udusmini_library.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.adapters.Gallery_recyler_adapter;
import com.example.abumuhsin.udusmini_library.tasks.DeviceGalleryAsyncTask;
import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class GalleryBook_fragment extends Fragment implements Gallery_recyler_adapter.OnFolderCLickListener, DeviceGalleryAsyncTask.OnLoadingImages {

    private View view;
    private RecyclerView gallery_RecyclerView;
    public ArrayList<Model_images> images_folder;
    private Gallery_recyler_adapter gallery_recyler_adapter;

    public GalleryBook_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_book_recycler_layout, container, false);
        init(view);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        new DeviceGalleryAsyncTask(this.requireContext(), this).execute();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    private void intiViews(View view) {
        gallery_RecyclerView = view.findViewById(R.id.recycler);
    }

    private void initAdapters() {
        images_folder = new ArrayList<>();
        gallery_recyler_adapter = new Gallery_recyler_adapter(getActivity(), images_folder, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4, RecyclerView.VERTICAL, false);
//        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        gallery_RecyclerView.setLayoutManager(gridLayoutManager);
        gallery_RecyclerView.setAdapter(gallery_recyler_adapter);
    }

    public void updateList(Model_images images_folder) {
        this.images_folder.add(images_folder);
        gallery_recyler_adapter.notifyDataSetChanged();
    }

    public static final boolean FROM_GALLERY = true;
    public static final String MSG_EXTRA = "msg_indicator";
    public static final String BOOK_NAME_EXTRA = "msg_book";
    public static final String IMAGES_EXTRA = "msg_images";
    public static final String GALLERY_TAG = "galleryDEBUG";

    @Override
    public void onFolderClick(String folder_name, int position, ArrayList<String> images) {
        Intent intent = new Intent(getContext(), FlipBooKActivity.class);
        intent.putExtra(MSG_EXTRA, FROM_GALLERY);
        intent.putExtra(BOOK_NAME_EXTRA, folder_name);
        intent.putExtra(IMAGES_EXTRA, images);
        Log.i(GALLERY_TAG, "before starting Flip Activity");
        startActivity(intent);
        Log.i(GALLERY_TAG, "Flip Activity Started");
    }
    private boolean is_pic_for_existing_folder = false;
    private int int_position = 0;


    @Override
    public void onPreImageLoad() {
        is_pic_for_existing_folder = false;
        int_position = 0;
        if (!images_folder.isEmpty()) {
            images_folder.clear();
        }
    }

    @Override
    public void onImageANDFolderLoading(String path_of_folder, String absolutePathOfImage) {
        for (int i = 0; i < images_folder.size(); i++) {
            is_pic_for_existing_folder = images_folder.get(i).getStr_folder().equals(path_of_folder);
            if (is_pic_for_existing_folder) {
                int_position = i;
                break;
            }
        }
        if (is_pic_for_existing_folder) {
            ArrayList<String> al_path = new ArrayList<>(images_folder.get(int_position).getAl_imagepath());
            al_path.add(absolutePathOfImage);
            images_folder.get(int_position).setAl_imagepath(al_path);
        } else {
            ArrayList<String> al_path = new ArrayList<>();
            al_path.add(absolutePathOfImage);
            final Model_images obj_model = new Model_images();
            obj_model.setStr_folder(path_of_folder);
            obj_model.setAl_imagepath(al_path);
            images_folder.add(obj_model);
        }
    }

    @Override
    public void onLoadFinished() {
        gallery_recyler_adapter.notifyDataSetChanged();
    }

}
