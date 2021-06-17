package com.example.abumuhsin.udusmini_library.Listeners;

import android.view.ScaleGestureDetector;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.MyScrollView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.adapters.FlipBook_adapter;

/**
 * Created by Abu Muhsin on 30/11/2018.
 */

public final class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    FlipBooKActivity flipBooKActivity;
    FlipBook_adapter flipBook_adapter;
    TextView title;
    MyScrollView scrollView;

    public MyScaleGestureListener(FlipBooKActivity flipBooKActivity, TextView title, MyScrollView scrollView) {
        this.flipBooKActivity = flipBooKActivity;
        flipBook_adapter = flipBooKActivity.flipBook_adapter;
        this.title = title;
        this.scrollView = scrollView;

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

//        scrollView = flipGalleryBookAdapter.scrollViews.get(flipBooKActivity.mAdapterFlipView.getSelectedItemPosition());
//        scrollView = flipBooKActivity.mAdapterFlipView.getSelectedView().findViewById(R.id.scrollView);
        scrollView = flipBooKActivity.current_page.findViewById(R.id.scrollView);
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
//        scrollView = flipGalleryBookAdapter.scrollViews.get(flipBooKActivity.mAdapterFlipView.getSelectedItemPosition());
//        scrollView = flipBooKActivity.mAdapterFlipView.getSelectedView().findViewById(R.id.scrollView);
        scrollView = flipBooKActivity.current_page.findViewById(R.id.scrollView);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        title = flipGalleryBookAdapter.titles.get(flipBooKActivity.mAdapterFlipView.getSelectedItemPosition());
//        title = flipBooKActivity.mAdapterFlipView.getSelectedView().findViewById(R.id.title);
        title = flipBooKActivity.current_page.findViewById(R.id.title);
        return false;
    }
}
