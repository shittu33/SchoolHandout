package com.example.abumuhsin.udusmini_library.adapters;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.amazing_picker.models.Model_images;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 17/12/2018.
 */

public class Gallery_recyler_adapter extends RecyclerView.Adapter<Gallery_recyler_adapter.ViewHolder> {
    private static final String recyclerTAG = "recycler";
    Activity context;
    ArrayList<Model_images> dir_data;
    private int position;
    OnFolderCLickListener onFolderCLickListener;

    public void setPosition(int position) {
        this.position = position;
    }

    public Gallery_recyler_adapter(Activity context, ArrayList<Model_images> dir_data, OnFolderCLickListener onFolderCLickListener) {
        Log.i(recyclerTAG, "Recycler Adapter constructor: called ");
        this.context = context;
        this.dir_data = dir_data;
        this.onFolderCLickListener = onFolderCLickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(recyclerTAG, " onCreateViewHolder: called ");
        View v = LayoutInflater.from(context).inflate(R.layout.dir_book_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i(recyclerTAG, " onBindViewHolder: called ");
        final Model_images model_images = dir_data.get(position);
        final String first_image = model_images.getAl_imagepath().get(0);
        String folder_name = model_images.getStr_folder();
        if (folder_name.length()>19){
            folder_name=folder_name.substring(0,19) +"...";
        }
        try {
            GlideApp.with(context)
                    .load(Uri.fromFile(new File(first_image)))
                    .placeholder(R.drawable.ic_folder_open_black_24dp)
                    .into(holder.img);
            } catch (Exception e) {
            Log.i(FlipBooKActivity.TAG, "picasso has problem");
        }
        holder.txt.setText(folder_name);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFolderCLickListener.onFolderClick(model_images.getStr_folder(), holder.getAdapterPosition(), model_images.getAl_imagepath());
            }
        });
    }

    public int getPosition() {
        return position;
    }


    public interface OnFolderCLickListener {
        void onFolderClick(String folder_name, int position, ArrayList<String> images);
    }

    @Override
    public int getItemCount() {
        return dir_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.txt);
        }
    }
}
