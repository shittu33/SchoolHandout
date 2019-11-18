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
import com.example.abumuhsin.udusmini_library.adapters.DownloaderAdapter;
import com.example.abumuhsin.udusmini_library.utils.DividerDecoration;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Book_downloads_fragment extends Fragment {

    private Handout handout;
    private View view;
    RecyclerView downloader_recycler;

    public Book_downloads_fragment(Handout handout) {
        this.handout = handout;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.downloads_fragment_layout, container, false);
        init(view);
        return view;
    }

    private void initAdapters() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        downloader_recycler.setLayoutManager(linearLayoutManager);
        DownloaderAdapter downloaderAdapter = new DownloaderAdapter(this, handout);
        DividerDecoration dividerItemDecoration = new DividerDecoration(this.requireContext(), R.drawable.divider);
        downloader_recycler.addItemDecoration(dividerItemDecoration);
        downloader_recycler.setAdapter(downloaderAdapter);
    }

    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    private void intiViews(View view) {
        downloader_recycler = view.findViewById(R.id.downloaders_recycler);
    }


}
