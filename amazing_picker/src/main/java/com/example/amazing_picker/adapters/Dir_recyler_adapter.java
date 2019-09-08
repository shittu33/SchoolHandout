package com.example.amazing_picker.adapters;

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

import com.bumptech.glide.Glide;
import com.example.amazing_picker.R;
import com.example.amazing_picker.models.Folder_image;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 17/12/2018.
 */

public class Dir_recyler_adapter extends RecyclerView.Adapter<Dir_recyler_adapter.ViewHolder> {
    private static final String recyclerTAG = "recycler";
    Activity context;
    ArrayList<Folder_image> dir_info;
    private int position;
    FolderCLickListener folderCLickListener;

    public void setPosition(int position) {
        this.position = position;
    }

    public Dir_recyler_adapter(Activity context, ArrayList<Folder_image> dir_info, FolderCLickListener folderCLickListener) {
        Log.i(recyclerTAG, "Recycler Adapter constructor: called ");
        this.context = context;
        this.dir_info = dir_info;
        this.folderCLickListener = folderCLickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(recyclerTAG, " onCreateViewHolder: called ");
        View v = LayoutInflater.from(context).inflate(R.layout.dir_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i(recyclerTAG, " onBindViewHolder: called ");
//        holder.root_view.getLayoutParams().height = 115;
        final Folder_image folder_model = dir_info.get(position);
        final String first_image = folder_model.getFirst_image();
        String folder_name = folder_model.getName();
        if (folder_name.length()>19){
            folder_name=folder_name.substring(0,19) +"...";
        }
        holder.img.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        try {

            Glide.with(context)
                    .load(!first_image.equals("pdf")?Uri.fromFile(new File(first_image)):R.drawable.ic_folder_open_black_24dp)
//                    .thumbnail(0.3f)
                    .into(holder.img);

//            GlideApp.with(context)
//                    .load(Uri.fromFile(new File(first_image)))
//                    .placeholder(R.drawable.ic_folder_open_black_24dp)
//                    .into(holder.img);
            } catch (Exception e) {
        }
        holder.txt.setText(folder_name);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderCLickListener.onFolderClick(folder_model.getName(), holder.getAdapterPosition(), first_image.equals("pdf"));
            }
        });
    }

    public int getPosition() {
        return position;
    }


    public interface FolderCLickListener {
        void onFolderClick(String folder_name, int position, boolean is_pdf_folder);
    }

    @Override
    public int getItemCount() {
        return dir_info.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt;
        private ImageView img;
        View root_view;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.txt);
            root_view = itemView.findViewById(R.id.dir_recycler_item);
        }
    }
}
