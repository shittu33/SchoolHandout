package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.TouchImageView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.abumuhsin.udusmini_library.utils.GlideRequest;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.mysqlitedbconnection.csv.Constants;

import java.io.File;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity.TAG;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;


public class FlipBook_adapter extends CustomFlipViewBaseAdapter {
    FlipBooKActivity context;
    private LinkedList<Flip_model> pages_data;
    private TouchImageView image;

    private TextView page_no;
    boolean page_contains_a_picture = false;
    boolean is_image_fuly_loaded = false;
    public FlipBook_adapter(FlipBooKActivity context, LinkedList<Flip_model> pages_data) {
        this.context = context;
        this.pages_data = pages_data;
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
        return is_image_fuly_loaded;
    }

    @Override
    public void set_viewContentLoaded(boolean is_view_loaded) {
        is_image_fuly_loaded = is_view_loaded;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int i, final View view, ViewGroup viewGroup) {
        View v = view;
        Log.i(FlipBooKActivity.TAG, "getView: about to getView of index " + i);
        int type = getItemViewType(i);
        Log.i(FlipBooKActivity.TAG, "getView: view type is " + type);
        if (type == Constants.COVER_TYPE) {
            String book_cover = context.book_cover;
            v = LayoutInflater.from(context).inflate(R.layout.book_cover, null);
            ImageView imgView = v.findViewById(R.id.cover_img);
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            LoadWithGlideAppEditable(book_cover).into(imgView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnSingleTapConfirmed();
                }
            });
        } else if (type == Constants.LAST_PAGE_TYPE) {
            v = LayoutInflater.from(context).inflate(R.layout.book_back_cover, null);
            v.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.SwitchImportBtnDisplay();
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnSingleTapConfirmed();
                }
            });
        } else if (type == Constants.SINGLE_PAGE_TYPE) {
            v = LayoutInflater.from(context).inflate(R.layout.sample2_item, null);
            page_contains_a_picture = pages_data.get(i).getPic_count() == SINGLE_PAGE;
            page_no = v.findViewById(R.id.page_no);
            image = v.findViewById(R.id.z_image);
            int end_no = getCount() - 2;
            String no = i + "/" + end_no;
            Log.i(FlipBooKActivity.TAG, "getView: page no is " + no);
            page_no.setText(no);
            File image_file = new File(getItem(i));
            Log.i(FlipBooKActivity.TAG, "getView: got imageFile of path " + image_file.getPath());
            context.is_view_fully_loaded = false;
            set_viewContentLoaded(false);
            if ((page_contains_a_picture)) {
                LoadWithGlideAppEditable(image_file).into(image);
            } else {
                image.setImageResource(R.drawable.empty_background);
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

    private GlideRequest<Bitmap> LoadWithGlideAppEditable(Object image_file) {
        return GlideApp.with(context.getApplicationContext())
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
                        if (!context.is_image_seeking && context.mAdapterFlipView.is_to_flip()) {
                            context.AutoRefreshAfter(100);
                        }
                        return false;
                    }
                })
                .thumbnail(0.3f)
//                            .transform(new SepiaFilterTransformation())
                .placeholder(R.drawable.empty_background);
    }


    public boolean OnDown(MotionEvent e) {
        return true;
    }

    public void switch_ImageZoomFlipMode() {
        if (context.mAdapterFlipView.is_to_flip()) {
            context.mAdapterFlipView.setIs_to_flip(false);
            context.CancelAutoRefresh();
            String page_path = context.getFlip_list().get(context.current_page_index).getImage_path();
            int dest_width = View_Utils.getScreenResolution(context).width * 2;
            int dest_height = View_Utils.getScreenResolution(context).height * 2;
            Bitmap page_bitmap = FileUtils.getDownSizedBitmapFromPath(page_path, dest_width, dest_height);
            ((ImageView) context.current_page.findViewById(R.id.z_image)).setImageBitmap(page_bitmap);
            context.mAdapterFlipView.hideFlipView();
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {

            context.mAdapterFlipView.ShowFlipView();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.mAdapterFlipView.setIs_to_flip(true);
                }
            }, 500);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void OnLongPress(final MotionEvent e) {
        if (context.is_click_not_toFlip(e)) {
            Log.i(TAG
                    , "View was long clicked");
        }
    }

    public boolean OnSingleTapConfirmed() {
        View_Utils.hideSystemUI(context);
        if (!context.dont_hide) {
            if (context.is_nav_visible()) {

                if (context.getHandler() != null && context.getRunnable() != null) {
                    context.getHandler().removeCallbacks(context.getRunnable());
                }
                context.HideNavigations();
            } else {
                context.PostDisplayNavigations(5000);
            }
//        }
        }

        if (context.mAdapterFlipView.is_to_flip()) {
            context.ManuallyRefresh_Page(50);
        }
        return false;
    }

}

