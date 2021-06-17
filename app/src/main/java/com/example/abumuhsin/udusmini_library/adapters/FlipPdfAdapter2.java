package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.FlipBooKView;
import com.example.abumuhsin.udusmini_library.Views.TouchImageView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.mysqlitedbconnection.csv.Constants;

import java.io.File;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_ZoomableFile;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfPageBitmapWithLow_quality;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;

public class FlipPdfAdapter2 extends CustomFlipViewBaseAdapter {
    Context context2;
    FlipBooKView flipBooKView;
    private LinkedList<Flip_model> pages_data;
    private TouchImageView image;
    private TextView page_no;
    boolean page_contains_a_picture = false;
    private String pdf_path;
    boolean isImageFulyLoaded = false;

    public FlipPdfAdapter2(Context context, LinkedList<Flip_model> pages_data, FlipBooKView flipBooKView, String pdf_path) {
        this.context2 = context;
        this.flipBooKView = flipBooKView;
        this.pages_data = pages_data;
        this.pdf_path=pdf_path;
        Log.i(FlipBooKActivity.TAG, "Adapter initialized");
    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return pages_data.get(position).getType();
    }


    @Override
    public int getCount() {
        return pages_data.size();
    }

    @Override
    public String getItem(int i) {
        return pages_data.get(i).getImage_path();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean is_viewFullyLoaded() {
        return isImageFulyLoaded;
    }

    @Override
    public void set_viewContentLoaded(boolean is_content_loaded) {
        isImageFulyLoaded = is_content_loaded;
    }

    //    private static class ViewHolder {
//        TouchImageView touchImageView;
//        TextView page_number;
//
//    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int i, final View view, ViewGroup viewGroup) {
        View v = view;
        Log.i(FlipBooKActivity.TAG, "getView: about to getView of index " + i);
//        viewHolder = new ViewHolder();
//        Log.i(FlipBooKActivity.TAG, "preparing to get View");
        int type = getItemViewType(i);
        Log.i(FlipBooKActivity.TAG, "getView: view type is " + type);
        if (type == Constants.COVER_TYPE) {
            v = LayoutInflater.from(context2).inflate(R.layout.book_cover_lay1, null);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnSingleTapConfirmed();
                }
            });
        } else if (type == Constants.LAST_PAGE_TYPE) {
            v = LayoutInflater.from(context2).inflate(R.layout.book_back_cover, null);
            v.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnSingleTapConfirmed();
                }
            });
        } else if (type == Constants.SINGLE_PAGE_TYPE) {
            Log.i(FlipBooKActivity.TAG, " getView: Book leave");
            v = LayoutInflater.from(context2).inflate(R.layout.sample2_item, null);
            Log.i(FlipBooKActivity.TAG, "getView: layout inflated");
            page_contains_a_picture = pages_data.get(i).getPic_count() == SINGLE_PAGE;
            Log.i(FlipBooKActivity.TAG, "getView: this page contain picture");
            page_no = v.findViewById(R.id.page_no);
            image = v.findViewById(R.id.z_image);
            Log.i(FlipBooKActivity.TAG, "getView: b4 page_no");
            String no = (i + 1) + "/" + (getCount());
            Log.i(FlipBooKActivity.TAG, "getView: page no is " + no);
            page_no.setText(no);
            File image_file = new File(getItem(i));
            Log.i(FlipBooKActivity.TAG, "getView: got imageFile of path " + image_file.getPath());
            /**
             * if the page of index i is not yet saved on the device due to fast flipping, load it bitmap instantly
             *
             * */
            flipBooKView.is_view_fully_loaded = false;
            set_viewContentLoaded(false);
            if (!flipBooKView.is_seeking) {
                if (image_file.exists()) {
                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: about to load pictures");
                    GlideApp.with(context2.getApplicationContext())
                            .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
                            .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                            .load(image_file).addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            set_viewContentLoaded(true);
                            flipBooKView.AutoRefreshAfter(200);
                            flipBooKView.mAdapterFlipView.is_selected_view = false;
                            return false;
                        }

                    })
                            .thumbnail(0.3f)
                            //                            .transform(new SepiaFilterTransformation())
                            .placeholder(R.drawable.empty_background)//error_bac
                            .into(image);
                    Log.i(FlipBooKActivity.TAG, "getView: after a picture is loaded");

                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: after a picture loaded");
            flipBooKView.RefreshBookData();
                } else {
                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: image_file doesn't exist");
                    image.setImageBitmap(getPdfPageBitmapWithLow_quality(context2, pdf_path, i));
                    set_viewContentLoaded(false);
                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: after a picture is loaded");
                }
            } else {
                Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: image LOAD start");
                Glide.with(context2.getApplicationContext())
                        .load(R.drawable.empty_background)
                        .into(image);
                Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: image LOAD end");
            }

            image.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return OnSingleTapConfirmed();
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    switch_ImageZoomFlipMode();
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }
            });
        }
        return v;
    }

    public boolean is_waiting_for_image = false;

    public void switch_ImageZoomFlipMode() {
//        final TouchImageView image = context.current_page.findViewById(R.id.z_image);
        if (flipBooKView.mAdapterFlipView.is_to_flip()) {
            flipBooKView.mAdapterFlipView.setIs_to_flip(false);
            flipBooKView.CancelAutoRefresh();
            final File page_file = create_new_ZoomableFile(flipBooKView.book_title + "_dir", flipBooKView.current_page_index);
            if (!page_file.exists()) {
                is_waiting_for_image = true;
            } else {
                ((ImageView) flipBooKView.current_page.findViewById(R.id.z_image)).setImageBitmap(BitmapFactory.decodeFile(page_file.getPath()));
                is_waiting_for_image = false;
                flipBooKView.mAdapterFlipView.hideFlipView();
//                context2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } else {
//            notifyDataSetChanged();
            flipBooKView.mAdapterFlipView.ShowFlipView();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flipBooKView.mAdapterFlipView.setIs_to_flip(true);
                }
            }, 700);
//            context2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    public boolean OnSingleTapConfirmed() {
//        View_Utils.hideSystemUI(context2);
        if (!flipBooKView.dont_hide) {
            if (flipBooKView.is_nav_visible()) {
                if (flipBooKView.getHandler() != null && flipBooKView.getRunnable() != null) {
                    flipBooKView.getHandler().removeCallbacks(flipBooKView.getRunnable());
                }
                flipBooKView.HideNavigations();
            } else {
                flipBooKView.PostDisplayNavigations(5000);
            }
        }
        if (flipBooKView.mAdapterFlipView.is_to_flip()) {
            flipBooKView.ManuallyRefresh_Page(50);
        }
        return true;
    }
}

