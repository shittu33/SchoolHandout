package com.example.abumuhsin.udusmini_library.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by Abu Muhsin on 17/12/2018.
 */
public class Recyler_adapter extends RecyclerView.Adapter<Recyler_adapter.ViewHolder> {
    private static final String recyclerTAG = "recycler";
    Context context;
    FlipBooKActivity flipBooKActivity;
    ArrayList<String> images;
    public ViewHolder viewHolder;

    public Recyler_adapter(Context context, ArrayList<String> images) {
        Log.i(recyclerTAG, "Recycler Adapter constructor: called ");
        this.context = context;
        this.images = images;
        flipBooKActivity = (FlipBooKActivity) context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(recyclerTAG, " onCreateViewHolder: called ");
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    public int adapter_position;
    public String imageTAG;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i(recyclerTAG, " onBindViewHolder: called ");
        viewHolder = holder;
        String image_path = images.get(position);
        try {

            Glide.with(context)
                    .load(!image_path.equals("add") ? new File(image_path) : R.drawable.ic_add_circle_outline_black)
                    .into(holder.img);

//            GlideApp.with(context)
//                    .load(!image_path.equals("add")?new File(image_path):R.drawable.ic_add_circle_outline_black)
//                    .placeholder(R.drawable.trimed_logo)
//                    .into(holder.img);

        } catch (Exception e) {
            Log.i(FlipBooKActivity.TAG, "Glide has issue");
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (images.get(holder.getAdapterPosition()).equals("add")) {
                        try {
                            flipBooKActivity.StartDeviceCamera();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });
        final String msg = "dragDebug";
        holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adapter_position = holder.getAdapterPosition();
                Log.d(msg, "imageLongPressed");
                imageTAG = images.get(adapter_position);
                ClipData clipData = new ClipData(imageTAG/*holder.img.getTag().toString()*/
                        , new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}
                        , new ClipData.Item(/*(CharSequence) holder.img.getTag()*/imageTAG));
                Log.d(msg, "after clipData");
                View.DragShadowBuilder mViewShadow = new View.DragShadowBuilder(v);
                Log.d(msg, "after shadow");
                v.startDrag(clipData
                        , mViewShadow
                        , v
                        , 0);

//                ((FlipBooKActivity) context).findViewById(R.id.float_crop).setVisibility(View.VISIBLE);
//                ((FlipBooKActivity) context).findViewById(R.id.float_delete).setVisibility(View.VISIBLE);
                Log.d(msg, "after drag fired");
                return true;
            }
        });

        holder.img.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determines if this View can accept the dragged data
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            adapter_position = holder.getAdapterPosition();
                            Log.d("drag_imageDebug", "ACTION_DRAG_STARTED");
//                            Log.i("drag_imageDebug", "ImageView is " + v.getTag().toString());
                            return true;
                        } else {
                            return false;
                        }

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d("drag_imageDebug", "ACTION_DRAG_ENTERED");
                        showRecyclerzoomIndicatorBonds((ViewGroup) v.getParent().getParent());
//                        Log.i("drag_imageDebug", "ImageView " + v.getTag().toString() + " entered");
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
//                        Log.d(msg, "ACTION_DRAG_EXITED");
                        HideRecyclerzoomIndicatorBonds((ViewGroup) v.getParent().getParent());
                        return true;

                    case DragEvent.ACTION_DROP:
                        int current_page_index = flipBooKActivity.current_page_index;
                        View dragged_view = (View) event.getLocalState();
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String path = item.getText().toString();
                        if (path.equals(flipBooKActivity.getFlip_list().get(current_page_index).getImage_path())) {
//                            flipBooKActivity.saveAPathAsPages_Bucket(path, adapter_position);
//                            MyBook_fragment.page_table.deletePictureFromThisPage(flipBooKActivity
//                                    , flipBooKActivity.getFlip_list().get(current_page_index));
//                            Toast.makeText(context, "you just drop data to recycler", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
//                        ((ImageView) v).clearColorFilter();
//                        v.invalidate();
                        HideRecyclerzoomIndicatorBonds((ViewGroup) v.getParent().getParent());
//                        page_editingActivity.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        ((FlipBooKActivity) context).findViewById(R.id.float_crop).setVisibility(View.GONE);
//                        ((FlipBooKActivity) context).findViewById(R.id.float_delete).setVisibility(View.GONE);
                        if (event.getResult()) {
                            Toast.makeText(context, "drop is okay", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "drop failed", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                }

                return false;
            }
        });

    }

    public void showRecyclerzoomIndicatorBonds(ViewGroup viewGroup) {
        viewGroup.findViewById(R.id.recycler_zoom_indicator).setVisibility(View.VISIBLE);
    }

    public void HideRecyclerzoomIndicatorBonds(ViewGroup viewGroup) {
        viewGroup.findViewById(R.id.recycler_zoom_indicator).setVisibility(GONE);
    }
    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);

        }
    }
}
