package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.abumuhsin.udusmini_library.R;

public class PageHeaderView extends FrameLayout implements View.OnClickListener {
    private ImageButton b_more, b_edit, b_add, b_remove;
    private androidx.appcompat.widget.Toolbar toolbar;

    public PageHeaderView(@NonNull Context context) {
        super(context);
        Log.e(TAG, " PageHeaderView context b4 init" );
        init();
        Log.e(TAG, " PageHeaderView context" );
    }

    private static final String TAG = "PageHeaderView";
    public PageHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG, " PageHeaderView attrs b4 init" );
        init();
        Log.e(TAG, " PageHeaderView attrs" );

    }

    public PageHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e(TAG, " PageHeaderView attrs defStyleAttr b4 init" );
        init();
        Log.e(TAG, " PageHeaderView attrs defStyleAttr" );
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void init() {
        Context context = getContext();
        inflate(context, R.layout.page_header, this);
        toolbar = findViewById(R.id.toolbar);
        b_more = findViewById(R.id.more_btn);
        b_add = findViewById(R.id.add_btn);
        b_remove = findViewById(R.id.remove__btn);
        b_edit = findViewById(R.id.edit_btn);
        b_more.setOnClickListener(this);
        b_edit.setOnClickListener(this);
        b_add.setOnClickListener(this);
        b_remove.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onControlButtonListener.onAnyControlClick(view);
        switch (view.getId()) {
            case R.id.add_btn:
                onControlButtonListener.onAddClick(view);
                break;
            case R.id.remove__btn:
                onControlButtonListener.onRemoveClick(view);
                break;
            case R.id.edit_btn:
                onControlButtonListener.onEditClick(view);
                break;
            case R.id.more_btn:
                onControlButtonListener.onMoreClick(view);
                break;
        }
    }

    OnControlButtonListener onControlButtonListener;

    public void setOnHeaderButtonListener(OnControlButtonListener onControlButtonListener) {
        this.onControlButtonListener = onControlButtonListener;
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    private void setMovingColor(ImageButton nav_btn) {
        nav_btn.setColorFilter(Color.BLUE);
    }

    private void setIdleColor(ImageButton view) {
        view.setColorFilter(view.getContext().getResources().getColor(R.color.colorPrimary));
    }

    public interface OnControlButtonListener {
        void onAddClick(View v);

        void onRemoveClick(View v);

        void onEditClick(View v);

        void onMoreClick(View v);

        void onAnyControlClick(View view);

    }

    public void DisableFuntions() {
        b_edit.setVisibility(GONE);
        b_remove.setVisibility(GONE);
        b_add.setVisibility(GONE);
    }
}
