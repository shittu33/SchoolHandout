package com.example.abumuhsin.udusmini_library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.Device_pdf_recycler_adapter;
import com.example.abumuhsin.udusmini_library.tasks.DevicePdfAsyncTask;
import com.example.abumuhsin.udusmini_library.test.TFlipPdfActivity;

import java.util.ArrayList;

public class MyPdfActivity extends AppCompatActivity implements Device_pdf_recycler_adapter.PdfCLickListener, DevicePdfAsyncTask.OnLoadingPdfs {

    private RecyclerView pdf_RecyclerView;
    private Device_pdf_recycler_adapter pdf_recycler_adapter;
    public ArrayList<String> pdf_paths = new ArrayList<>();
    public static final boolean FROM_PDF = true;
    public static final String PDF_MSG_EXTRA = "pdf_msg_indicator";
    public static final String PDF_BOOK_NAME_EXTRA = "pdf_msg_book";
    public static final String GALLERY_TAG = "galleryDEBUG";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_book_recycler_layout);
        init();
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            pdf_paths.clear();
            new DevicePdfAsyncTask(this,this).execute();
//            DevicePdfLoaderTask.get(this, this).LoadPdfsFromStorage();
            Log.i(GALLERY_TAG, "Loading started");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(GALLERY_TAG, "Loading failed");
        }
    }

    private void init() {
        intiViews();
        initAdapter();
    }

    private void intiViews() {
        pdf_RecyclerView = findViewById(R.id.recycler);
    }

    private void initAdapter() {
        Log.i(GALLERY_TAG, "before setting adapter");
        pdf_recycler_adapter = new Device_pdf_recycler_adapter(this, pdf_paths, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        pdf_RecyclerView.setLayoutManager(gridLayoutManager);
        pdf_RecyclerView.setAdapter(pdf_recycler_adapter);
        Log.i(GALLERY_TAG, "adapter set");
    }

    @Override
    public void onPdfAdded(String pdf_name) {
        pdf_paths.add(pdf_name);
    }

    @Override
    public void onLoadFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdf_recycler_adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void OnPdfClicked(String pdf_name, int position) {
        Intent intent = new Intent(this, TFlipPdfActivity.class);
        intent.putExtra(PDF_MSG_EXTRA, FROM_PDF);
        intent.putExtra(PDF_BOOK_NAME_EXTRA, pdf_name);
        Log.i(GALLERY_TAG, "before starting Flip Activity");
        startActivity(intent);
        Log.i(GALLERY_TAG, "Flip Activity Started");
    }
}
