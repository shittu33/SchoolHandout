package com.example.abumuhsin.udusmini_library.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abumuhsin.udusmini_library.R;
import com.example.amazing_picker.models.Model_images;

import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Book_description_fragment extends Fragment{

    private View view;
    TextView descr_tv;

    public Book_description_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.description_fragment_layout, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        intiViews(view);
    }

    private void intiViews(View view) {
        descr_tv = view.findViewById(R.id.txt);
    }


}
