package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;

import java.util.ArrayList;

import androidx.appcompat.widget.ViewStubCompat;

public class Sample_adapter extends BaseAdapter {

    FlipBooKActivity context;
    Button number_button;
    TextView title, pages;

    ArrayList<Integer> layouts;

    public Sample_adapter(FlipBooKActivity context, ArrayList<Integer> layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @Override
    public int getCount() {
        return layouts.size();
    }

    @Override
    public Integer getItem(int i) {
        return layouts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        View v = view;
        LinearLayout v;


        if (view == null) {
            v = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.stub, null);
//            v = LayoutInflater.from(context).inflate(R.layout.stub, null);

            ViewStubCompat stub;

            if (layouts.get(i) == 0) {
                return v.getChildAt(3);
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.rotation);
//            stub.inflate();

            } else if (layouts.get(i) == 1) {
                return v.getChildAt(6);

//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.header);
//            stub.inflate();

            } else if (layouts.get(i) == 2) {
                return v.getChildAt(5);
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.sample_item);
//            stub.inflate();
//        }

            } else if (layouts.get(i) == 3) {
                return v.getChildAt(3);
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.about);
//            stub.inflate();

            } else if (layouts.get(i) == 4) {
                return v.getChildAt(2);
            }
//        }
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.online_book_layout);
//            stub.inflate();

//        }else if (pages_data.get(i)==5){
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.header);
//            stub.inflate();
//
//        }else if (pages_data.get(i)==6){
//            stub = (ViewStubCompat)v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.header);
//            stub.inflate();
//
//        }else if (pages_data.get(i)==7){
//            stub = (ViewStubCompat) v.findViewById(R.id.stub);
//            stub.setLayoutResource(R.layout.book_grid_item);
//            stub.inflate();
//
//        }
        }
        return view/*.getChildAt(2)*/;
    }

    static class ViewHolder {
        View view1;
        View view2;
        View view3;
        View view4;
        View view5;
        View view6;
        View view7;
    }

    public TextView getTitle() {
        return title;
    }

    public Button getBtn() {
        return number_button;
    }
}

