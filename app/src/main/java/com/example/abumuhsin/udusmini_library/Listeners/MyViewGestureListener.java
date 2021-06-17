package com.example.abumuhsin.udusmini_library.Listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.adapters.FlipBook_adapter;

/**
 * Created by Abu Muhsin on 30/11/2018.
 */

public final class MyViewGestureListener extends GestureDetector.SimpleOnGestureListener {
    FlipBooKActivity flipBooKActivity;
    FlipBook_adapter flipBook_adapter;
    TextView title;

    public MyViewGestureListener(FlipBooKActivity flipBooKActivity, TextView title) {
        this.flipBooKActivity = flipBooKActivity;
        flipBook_adapter = flipBooKActivity.flipBook_adapter;
        this.title = title;

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return flipBook_adapter.OnDown(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        flipBook_adapter.OnLongPress(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        title = flipGalleryBookAdapter.titles.get(flipBooKActivity.mAdapterFlipView.getSelectedItemPosition());
//        title = flipBooKActivity.mAdapterFlipView.getSelectedView().findViewById(R.id.title);
        title = flipBooKActivity.current_page.findViewById(R.id.title);
        return false;
    }

}
