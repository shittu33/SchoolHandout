package com.example.abumuhsin.udusmini_library.fragments;


import android.content.Context;
import android.content.Intent;
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
import com.example.abumuhsin.udusmini_library.adapters.Device_pdf_recycler_adapter;
import com.example.abumuhsin.udusmini_library.utils.DevicePdfLoaderTask;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;

import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class PDFBook_fragment extends Fragment implements Device_pdf_recycler_adapter.PdfCLickListener {

    private View view;
    private RecyclerView pdf_RecyclerView;
    private Device_pdf_recycler_adapter pdf_recycler_adapter;
    public ArrayList<String> pdf_paths = new ArrayList<>();

    public PDFBook_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pdf_book_recycler_layout, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            DevicePdfLoaderTask.get(PDFBook_fragment.this).LoadPdfsFromStorage();
            Log.i(GALLERY_TAG, "Loading started");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(GALLERY_TAG, "Loading failed");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        DevicePdfLoaderTask.get(PDFBook_fragment.this).LoadPdfsFromStorage();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        saveState();
//    }

    private void init(View view) {
        intiViews(view);
        initAdapter();
    }

    private void intiViews(View view) {
        pdf_RecyclerView = view.findViewById(R.id.recycler);
    }

    private void initAdapter() {
        Log.i(GALLERY_TAG, "before seeting adapter");
        pdf_recycler_adapter = new Device_pdf_recycler_adapter(getActivity(), pdf_paths, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4, RecyclerView.VERTICAL, false);
        pdf_RecyclerView.setLayoutManager(gridLayoutManager);
        pdf_RecyclerView.setAdapter(pdf_recycler_adapter);
        Log.i(GALLERY_TAG, "adapter set");
    }

    public void updateList(String pdf) {
        this.pdf_paths.add(pdf);
        Log.i(GALLERY_TAG, "A pdf added");
        pdf_recycler_adapter.notifyDataSetChanged();
    }

    public static final boolean FROM_PDF = true;
    public static final String PDF_MSG_EXTRA = "pdf_msg_indicator";
    public static final String PDF_BOOK_NAME_EXTRA = "pdf_msg_book";
    public static final String GALLERY_TAG = "galleryDEBUG";

    @Override
    public void OnPdfClicked(String pdf_name, int position) {
        Intent intent = new Intent(getContext(), FlipBooKActivity.class);
        intent.putExtra(PDF_MSG_EXTRA, FROM_PDF);
        intent.putExtra(PDF_BOOK_NAME_EXTRA, pdf_name);
        Log.i(GALLERY_TAG, "before starting Flip Activity");
        startActivity(intent);
        Log.i(GALLERY_TAG, "Flip Activity Started");
    }

    private void saveState() {
        new Fragment_Utils().saveState(this, "yes");
    }

    private void restoreState() {
        new Fragment_Utils().RestoreState(getFragmentManager(), this, "yes");
    }

}
