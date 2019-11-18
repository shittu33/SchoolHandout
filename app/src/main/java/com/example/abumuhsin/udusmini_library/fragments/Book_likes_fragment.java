package com.example.abumuhsin.udusmini_library.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.LikerAdapter;
import com.example.abumuhsin.udusmini_library.utils.DividerDecoration;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Book_likes_fragment extends Fragment {

    private View view;
    private RecyclerView likes_RecyclerView;
//    public ArrayList<Model_images> images_folder;
    Handout handout;

    public Book_likes_fragment(Handout handout) {
        this.handout = handout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.book_likes_fragment_layout, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    private void intiViews(View view) {
        likes_RecyclerView = view.findViewById(R.id.recycler);
    }

    RecyclerView.Adapter likers_adapter;

    private void initAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        likes_RecyclerView.setLayoutManager(linearLayoutManager);
        likers_adapter = new LikerAdapter(this,handout);
        DividerDecoration dividerItemDecoration = new DividerDecoration(Book_likes_fragment.this.requireContext(),R.drawable.divider);
        likes_RecyclerView.addItemDecoration(dividerItemDecoration);
        likes_RecyclerView.setAdapter(likers_adapter);
    }


    public static final String MSG_EXTRA = "msg_indicator";
    public static final String BOOK_NAME_EXTRA = "msg_book";
    public static final String IMAGES_EXTRA = "msg_images";
    public static final String GALLERY_TAG = "galleryDEBUG";

    private void saveState() {
        new Fragment_Utils().saveState(this, "yes");
    }

    private void restoreState() {
        new Fragment_Utils().RestoreState(getFragmentManager(), this, "yes");
    }

}
