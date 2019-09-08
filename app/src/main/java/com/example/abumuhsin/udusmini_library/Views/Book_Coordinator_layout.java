package com.example.abumuhsin.udusmini_library.Views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import cn.hugeterry.coordinatortablayout.utils.SystemView;

public class Book_Coordinator_layout extends CoordinatorLayout {
    private CollapsingToolbarLayout mCollapseToolBar;
    private TextView user_full_name;
    private ImageView cover_img;
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private TabLayout tab_layout;
    private Button Download_btn;
    private LikeButton like_btn;
    private ImageView book_image;
    private TextView no_pages_tv,poster_tv;

    public Book_Coordinator_layout(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public Book_Coordinator_layout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public Book_Coordinator_layout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    private void initView(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.view_coordinatortablayout, this, true);
        LayoutInflater.from(context).inflate(R.layout.view_book_coordinator_tab_lay, this, true);
        initToolbar(context);
        mCollapseToolBar = findViewById(R.id.collapsingtoolbarlayout);
        cover_img =  findViewById(R.id.cover_img);
        user_full_name = findViewById(R.id.user_full_name);
        tab_layout = findViewById(R.id.tabLayout);
        poster_tv = findViewById(R.id.poster);
        no_pages_tv = findViewById(R.id.no_pages_tv);
        Download_btn = findViewById(R.id.download_btn);
        like_btn = findViewById(R.id.like_btn);
    }

    public Button getDownload_btn() {
        return Download_btn;
    }

    public LikeButton getLike_btn() {
        return like_btn;
    }

    private void initToolbar(Context context) {
        mToolbar = findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(mToolbar);
        mActionbar = ((AppCompatActivity) context).getSupportActionBar();

    }
    private void init(Context context, AttributeSet attrs) {
        initView(context);
        if (attrs!=null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs
                    , R.styleable.CoordinatorLayout);

            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int contentScrimColor = typedArray.getColor(
                    R.styleable.CoordinatorLayout_contentScrim, typedValue.data);
            mCollapseToolBar.setContentScrimColor(contentScrimColor);

//        int tabIndicatorColor = typedArray.getColor(R.styleable.CoordinatorTabLayout_tabIndicatorColor, Color.WHITE);
//        mTabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
//
//        int tabTextColor = typedArray.getColor(R.styleable.CoordinatorTabLayout_tabTextColor, Color.WHITE);
//        mTabLayout.setTabTextColors(ColorStateList.valueOf(tabTextColor));
            typedArray.recycle();
        }
    }
    public Book_Coordinator_layout setupWithViewPager(ViewPager viewPager) {
        tab_layout.setupWithViewPager(viewPager);
        return this;
    }
public void setHandoutImage(String img_path){
    GlideApp.with(getContext()).load(img_path).placeholder(R.drawable.trimed_logo).into(cover_img);
}
    public TabLayout getTab_layout() {
        return tab_layout;
    }

    public Book_Coordinator_layout setTranslucentStatusBar(@NonNull Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return this;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (mToolbar != null) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mToolbar.getLayoutParams();
            layoutParams.setMargins(
                    layoutParams.leftMargin,
                    layoutParams.topMargin + SystemView.getStatusBarHeight(activity),
                    layoutParams.rightMargin,
                    layoutParams.bottomMargin);
        }

        return this;
    }

    public ActionBar getmActionbar() {
        return mActionbar;
    }

    public ImageView getCover_img() {
        return cover_img;
    }

    public TextView getUser_full_name() {
        return user_full_name;
    }

    public Book_Coordinator_layout setTitle(String title) {
        if (mActionbar != null) {
            mActionbar.setTitle(title);
        }
        return this;
    }

    public CoordinatorLayout setBackEnable(Boolean canBack) {
        if (canBack && mActionbar != null) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
            mActionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        }
        return this;
    }
    public Book_Coordinator_layout setTranslucentNavigationBar(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return this;
        } else {
            mToolbar.setPadding(0, SystemView.getStatusBarHeight(activity) >> 1, 0, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return this;
    }

    public void setNo_pages(String no_pages) {
        no_pages_tv.setText(no_pages);
    }

    public void setPoster(String poster) {
        poster_tv.setText(poster);
    }
}
