package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.FlipViewDragShadowBuilder;
import com.example.abumuhsin.udusmini_library.Views.MyScrollView;
import com.example.abumuhsin.udusmini_library.Views.TouchImageView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.flip_model;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.abumuhsin.udusmini_library.utils.GlideRequest;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.mysqlitedbconnection.csv.Constants;

import java.io.File;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity.TAG;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;


public class Zoom_adapter extends CustomFlipViewBaseAdapter {
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
    boolean is_image_fuly_loaded = false;
//    public SparseBooleanArray view_loading_status = new SparseBooleanArray();

    //    private ViewHolder viewHolder;
    public Zoom_adapter(FlipBooKActivity context, LinkedList<flip_model> pages_data) {
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
    public void set_viewContentLoaded(boolean is_view_loaded) {
        is_image_fuly_loaded = is_view_loaded;
    }

    //    private static class ViewHolder {
//        TouchImageView touchImageView;
//        TextView page_number;
//
//    }
    boolean is_addNav_viaible = true;

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
            String book_cover = context.book_cover;
            v = LayoutInflater.from(context).inflate(R.layout.book_cover, null);
            ImageView imgView = v.findViewById(R.id.cover_img);
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            LoadWithGlideAppEditable(book_cover, R.drawable.empty_background).into(imgView);
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
            int start_no = context.is_pics_from_device ? i + 1 : i;
            int end_no = context.is_pics_from_device ? getCount() : getCount() - 2;
            String no = start_no + "/" + end_no;
            Log.i(FlipBooKActivity.TAG, "getView: page no is " + no);
            page_no.setText(no);
            File image_file = new File(getItem(i));
            Log.i(FlipBooKActivity.TAG, "getView: got imageFile of path " + image_file.getPath());

            context.is_view_fully_loaded = false;
            set_viewContentLoaded(false);
            if ((page_contains_a_picture)) {
                Log.i(FlipBooKActivity.TAG, "getView: about to load pictures");
                LoadWithGlideAppEditable(image_file, R.drawable.empty_background).into(image);
                Log.i(FlipBooKActivity.TAG, "getView: after a picture is loaded");
            } else {
                image.setImageResource(R.drawable.empty_background);
            }

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    page_contains_a_picture = pages_data.get(i).getPic_count() == SINGLE_PAGE;
                    image.setTag(page_contains_a_picture && !TextUtils.isEmpty(getItem(i)) ? getItem(i) : "img_drag");
                    drag_img_path = getItem(i);
                    Log.d(msg, "imageLongPressed");
                    if (pages_data.get(i).getPic_count() != EMPTY_PAGE) {
                        ClipData clipData = new ClipData(drag_img_path
                                , new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}
                                , new ClipData.Item(drag_img_path));
                        Log.d(msg, "after clipData");
                        FlipViewDragShadowBuilder mViewShadow = new FlipViewDragShadowBuilder(image, getItem(i));
                        Log.d(msg, "after shadow");
                        v.startDrag(clipData
                                , mViewShadow
                                , v
                                , 0);
                        Log.d(msg, "after drag fired");
//                        context.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                        View_Utils.ShowView_with_ZoomOut((ViewGroup) context.findViewById(R.id.float_crop).getParent());
                        context.findViewById(R.id.float_crop).setVisibility(View.VISIBLE);
                        context.findViewById(R.id.float_delete).setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });
            image.setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    page_contains_a_picture = pages_data.get(i).getPic_count() == SINGLE_PAGE;
                    image.setTag(page_contains_a_picture && !TextUtils.isEmpty(getItem(i)) ? getItem(i) : "img_drag");
                    drag_img_path = getItem(i);
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            dragged_view = (View) event.getLocalState();
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                Log.d("drag_imageDebug", "Zoom_adapter: ACTION_DRAG_STARTED");
                                context.mAdapterFlipView.hideFlipView();
                                return true;
                            } else {
                                return false;
                            }

                        case DragEvent.ACTION_DRAG_ENTERED:
                            Log.d("drag_imageDebug", "ACTION_DRAG_ENTERED");
                            if (v != dragged_view) {
                                ((ImageView) v).setColorFilter(context.LIGTH_BLUE);
                                context.setDragBondWidthAndColor((ViewGroup) v.getParent());
                            } else {
                                context.setZoomBondWidthAndColor((ViewGroup) v.getParent());
                            }
                            context.showFlipImageDragBonds((ViewGroup) v.getParent());
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:
                            context.HideFlipImageDragBonds((ViewGroup) v.getParent());
                            ((ImageView) v).clearColorFilter();
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DROP:
                            dragged_view = (View) event.getLocalState();
                            if (dragged_view == v) {
//                                context.getBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                            ClipData.Item item = event.getClipData().getItemAt(0);
                            String dragData = item.getText().toString();
                            if (dragged_view != v && dragData.equals(context.getRecyler_adapter().imageTAG)) {
                                MyBook_fragment.page_table.add_A_PictureToCurrentPage(context, dragData);
                            }
                            context.HideFlipImageDragBonds((ViewGroup) v.getParent());
                            ((ImageView) v).clearColorFilter();
                            v.invalidate();
                            Toast.makeText(context, "you just drop data to flip", Toast.LENGTH_SHORT).show();
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
//                        if (v!=dragged_view) {
                            context.HideFlipImageDragBonds((ViewGroup) v.getParent());
                            ((ImageView) v).clearColorFilter();
                            v.invalidate();
//                        }
                            context.mAdapterFlipView.ShowFlipView();
                            View_Utils.hideView_with_ZoomIn((ViewGroup) context.findViewById(R.id.float_crop).getParent());
                            context.findViewById(R.id.float_crop).setVisibility(View.GONE);
                            context.findViewById(R.id.float_delete).setVisibility(View.GONE);
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

    private GlideRequest<Bitmap> LoadWithGlideAppEditable(Object image_file, int empty_background) {
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
                        if (/*context.mAdapterFlipView.is_selected_view && */!context.is_image_seeking && context.mAdapterFlipView.is_to_flip()) {
                            context.AutoRefreshAfter(100);
//                                context.mAdapterFlipView.is_selected_view = false;
                        }
                        return false;
                    }
                })
                .thumbnail(0.3f)
//                            .transform(new SepiaFilterTransformation())
                .placeholder(empty_background);
    }


    public boolean OnDown(MotionEvent e) {
        return true;
    }

    public boolean OnDoubleTap(MotionEvent e, TextView title) {
        Log.i(TAG, "onViewDoubleTapped");
        switch_EditMode(title);
        return false;
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
//            context.mAdapterFlipView.MakeAdapterVisible();
        } else {

            context.mAdapterFlipView.ShowFlipView();
//            notifyDataSetChanged();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.mAdapterFlipView.setIs_to_flip(true);
                }
            }, 500);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public boolean OnScale(ScaleGestureDetector detector, TextView title) {
        scale *= detector.getScaleFactor();
        scale = Math.max(1.0f, Math.min(scale, 3.2f));
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, default_txt_size * scale);
//        matrix.setScale(scale, scale);
        Log.i(TAG, "onView Scaled");
        return true;
    }

    public boolean OnScaleBegin(ScaleGestureDetector detector, MyScrollView scrollView) {
        is_scaling = true;
        scrollView.setScrolling(false);
        return true;
    }

    public void OnScaleEnd(ScaleGestureDetector detector, MyScrollView scrollView) {
        is_scaling = false;
        scrollView.setScrolling(true);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.i(TAG, "onScroll");
        return scrollView.onInterceptTouchEvent(e1);
    }

    public void GlideThings(String image_file) {
        Glide.with(context)
                .load(image_file)
                .into(image);
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

