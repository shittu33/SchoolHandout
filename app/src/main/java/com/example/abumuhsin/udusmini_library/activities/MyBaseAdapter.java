package com.example.abumuhsin.udusmini_library.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.abumuhsin.udusmini_library.R;

class MyBaseAdapter extends ArrayAdapter<String> {
    private MainActivity mainActivity;
    private final String[] filters;

    public MyBaseAdapter(MainActivity mainActivity, int res, String[] filters) {
        super(mainActivity, res, filters);
        this.mainActivity = mainActivity;
        this.filters = filters;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        if (view == null) {
            view = mainActivity.getLayoutInflater().inflate(R.layout.spinner_item, viewGroup);
//            view = LayoutInflater.from(mainActivity).inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup);
        }
        view.findViewById(R.id.txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivity, "its fine", Toast.LENGTH_SHORT).show();
            }
        });
        switch ((int) getItemId(position)) {
            case 0:
//                new AlertDialog.Builder(mainActivity).set
                break;
        }
        return view;
    }
}
