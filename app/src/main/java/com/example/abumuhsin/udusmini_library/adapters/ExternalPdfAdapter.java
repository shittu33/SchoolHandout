package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.example.abumuhsin.udusmini_library.Views.MyScrollView;
import com.example.abumuhsin.udusmini_library.Views.TouchImageView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.models.flip_model;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.mysqlitedbconnection.csv.Constants;

import java.io.File;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_ZoomableFile;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.getPdfPageBitmapWithLow_quality;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;

public class ExternalPdfAdapter extends CustomFlipViewBaseAdapter {
    FlipBooKActivity context;
    Button number_button;
    TextView title, pages;
    private MyScrollView scrollView;
    public LinkedList<TextView> titles = new LinkedList<>();
    public LinkedList<MyScrollView> scrollViews = new LinkedList<>();
    private LinkedList<flip_model> pages_data;
    private boolean is_scaling;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private TouchImageView image;

    private float default_txt_size;
    private float dx;

    private float dy;
    private GestureDetector general_Gestures;
    private ScaleGestureDetector scaleGestureDetector;
    private float last_position;
    private TextView page_no;
    private RelativeLayout.LayoutParams layoutParams;
    final String msg = "dragDebug";
    boolean page_contains_a_picture = false;
    String drag_img_path = "img_drag";
    Thread pdf_bitmap_thread;
    private boolean stop_running = false;
    boolean is_image_fuly_loaded = false;

    //    public SparseBooleanArray view_loading_status = new SparseBooleanArray();
    //    private ViewHolder viewHolder;
    public ExternalPdfAdapter(FlipBooKActivity context, LinkedList<flip_model> pages_data) {
        this.context = context;
        this.pages_data = pages_data;
        Log.i(FlipBooKActivity.TAG, "Adapter initialized");
    }

    View dragged_view;

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
    public void set_viewContentLoaded(boolean is_content_loaded) {
        is_image_fuly_loaded = is_content_loaded;
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
            v = LayoutInflater.from(context).inflate(R.layout.book_cover_lay1, null);
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
            Log.i(FlipBooKActivity.TAG, " getView: Book leave");
            v = LayoutInflater.from(context).inflate(R.layout.sample2_item, null);
            Log.i(FlipBooKActivity.TAG, "getView: layout inflated");
            page_contains_a_picture = pages_data.get(i).getPic_count() == SINGLE_PAGE;
            Log.i(FlipBooKActivity.TAG, "getView: this page contain picture");
            page_no = v.findViewById(R.id.page_no);
            image = v.findViewById(R.id.z_image);
            Log.i(FlipBooKActivity.TAG, "getView: b4 page_no");
            String no = (i + 1) + "/" + (getCount());
            Log.i(FlipBooKActivity.TAG, "getView: page no is " + no);
            page_no.setText(String.valueOf(no));
            File image_file = new File(getItem(i));
            Log.i(FlipBooKActivity.TAG, "getView: got imageFile of path " + image_file.getPath());
            /**
             * if the page of index i is not yet saved on the device due to fast flipping, load it bitmap instantly
             *
             * */
            context.is_view_fully_loaded = false;
            set_viewContentLoaded(false);
            if (!context.is_seeking) {
                if (image_file.exists()) {
                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: about to load pictures");
//                        Glide.with(context.getApplicationContext())
//                                .asBitmap()/*.format(DecodeFormat.PREFER_ARGB_8888)*/
//                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
//                                .load(image_file).addListener(new RequestListener<Bitmap>() {
                    GlideApp.with(context.getApplicationContext())
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
                            context.AutoRefreshAfter(200);
                            context.mAdapterFlipView.is_selected_view = false;
                            return false;
                        }

                    })
                            .thumbnail(0.3f)
                            //                            .transform(new SepiaFilterTransformation())
                            .placeholder(R.drawable.empty_background)//error_bac
                            .into(image);
                    Log.i(FlipBooKActivity.TAG, "getView: after a picture is loaded");

                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: after a picture loaded");
                } else {
                    image.setImageBitmap(getPdfPageBitmapWithLow_quality(context, context.pdf_path, i));
                    set_viewContentLoaded(false);
                    Log.i(FlipBooKActivity.TAG, "ExternalPdf-> getView: after a picture is loaded");
                }
            } else {
                Glide.with(context.getApplicationContext())
                        .load(R.drawable.empty_background)
                        .into(image);
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

    private void switch_EditMode(final TextView title) {
        if (context.mAdapterFlipView.is_to_flip()) {
            context.mAdapterFlipView.setIs_to_flip(false);
            context.mAdapterFlipView.hideFlipView();
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            context.mAdapterFlipView.ShowFlipView();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.mAdapterFlipView.setIs_to_flip(true);
                }
            }, 200);
        }
    }

    public boolean is_waiting_for_image = false;

    public void switch_ImageZoomFlipMode() {
//        final TouchImageView image = context.current_page.findViewById(R.id.z_image);
        if (context.mAdapterFlipView.is_to_flip()) {
            context.mAdapterFlipView.setIs_to_flip(false);
            context.CancelAutoRefresh();
//            View_Utils.showSystemUI(context);
//            context.mAdapterFlipView.hideFlipView();
            final File page_file = create_new_ZoomableFile(context.tmp_book + "_dir", context.current_page_index);
            if (!page_file.exists()) {
                is_waiting_for_image = true;
            } else {
                ((ImageView) context.current_page.findViewById(R.id.z_image)).setImageBitmap(BitmapFactory.decodeFile(page_file.getPath()));
                is_waiting_for_image = false;
                context.mAdapterFlipView.hideFlipView();
                context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                context.mAdapterFlipView.MakeAdapterVisible();
            }
        } else {
//            notifyDataSetChanged();
            context.mAdapterFlipView.ShowFlipView();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.mAdapterFlipView.setIs_to_flip(true);
                }
            }, 700);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        }
        if (context.mAdapterFlipView.is_to_flip()) {
            context.ManuallyRefresh_Page(50);
        }
        return true;
    }
}

