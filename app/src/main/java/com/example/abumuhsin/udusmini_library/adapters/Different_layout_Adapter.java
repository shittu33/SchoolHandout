package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.TouchImageView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;

import java.util.ArrayList;

public class Different_layout_Adapter extends BaseAdapter {
    public static final String DIFFERENT_TAG = "DifferentAdapter";

    FlipBooKActivity context;
    Button number_button;
    TextView title, pages;
    private View leaveView;
    ArrayList<Integer> layouts;
    private ViewHolder viewHolder;

    public Different_layout_Adapter(Activity context, ArrayList<Integer> layouts) {
        this.context = (FlipBooKActivity) context;
        this.layouts = layouts;
        leaveView = getBookLeaveView();
    }

    @Override
    public int getViewTypeCount() {
        return layouts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return layouts.get(position);
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
        View v;
        int type = getItemViewType(i);
        viewHolder = new ViewHolder();
        if (type == 0) {
            v = getBookCoverView();
        } else if (type == 1) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an139);
        } else if (type == 2) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an140);
        } else if (type == 3) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an141);
        } else if (type == 4) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an142);
        } else if (type == 5) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an143);
        } else if (type == 6) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an144);
        } else if (type == 7) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an145);
        } else if (type == 8) {
            v = loadImageForLayoutType(R.layout.sample1, R.drawable.an146);
        } else if (type == getCount() - 1) {
            v = getBookBackView();
        } else {
            v = getBookLeaveView();
            viewHolder.touchImageView = v.findViewById(R.id.z_image);
            try {

                Glide.with(context)
                        .load(R.drawable.an138_original)
                        .into(viewHolder.touchImageView);

//                GlideApp.with(context)
//                        .load(R.drawable.an138_original)
//                        .centerCrop()
//                        .thumbnail(getThumbnail(R.drawable.an138))
//                        .placeholder(R.drawable.bac)
//                        .into(viewHolder.touchImageView);
            } catch (Exception e) {
                Log.i(DIFFERENT_TAG, "glide has problem");
            }
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.RefreshPage();
//                Toast.makeText(context, "you just click on the view", Toast.LENGTH_SHORT).show();
            }
        });
        v.setTag(viewHolder);
        return v;
    }

    private View loadImageForLayoutType(int layout_id, int image) {
        View v = LayoutInflater.from(context).inflate(layout_id, null);
        viewHolder.imageView = v.findViewById(R.id.img);
        try {

            Glide.with(context)
                    .load(image)
                    .into(viewHolder.imageView);


//            GlideApp.with(context)
//                    .load(image)
//                    .centerCrop()
//                    .thumbnail(getThumbnail(image))
//                    .placeholder(R.drawable.bac)
//                    .into(viewHolder.imageView);
        } catch (Exception e) {
            Log.i(DIFFERENT_TAG, "glide has problem");
        }
        return v;
    }

//    private RequestBuilder<Drawable> getThumbnail(int image) {
//        return GlideApp
//                .with(context)
//                .load(image)
//                .thumbnail(0.4f)
//                .centerCrop();
//
//    }

    static class ViewHolder {
        ImageView imageView;
        TouchImageView touchImageView;

    }

    public View getBookCoverView() {
        return LayoutInflater.from(context).inflate(R.layout.book_cover_lay1, null);
    }

    public View getBookLeaveView() {
        return LayoutInflater.from(context).inflate(R.layout.sample2_item, null);
    }

    public View getBookBackView() {
        return LayoutInflater.from(context).inflate(R.layout.sample10, null);
    }
}

