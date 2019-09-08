package com.example.abumuhsin.udusmini_library.Listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.adapters.Zoom_adapter;

/**
 * Created by Abu Muhsin on 30/11/2018.
 */

public final class MyViewGestureListener extends GestureDetector.SimpleOnGestureListener {
    FlipBooKActivity flipBooKActivity;
    Zoom_adapter zoom_adapter;
    TextView title;

    public MyViewGestureListener(FlipBooKActivity flipBooKActivity, TextView title) {
        this.flipBooKActivity = flipBooKActivity;
        zoom_adapter = flipBooKActivity.zoom_adapter;
        this.title = title;

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return zoom_adapter.OnDown(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return zoom_adapter.OnSingleTapConfirmed();
    }

    @Override
    public void onLongPress(MotionEvent e) {
        zoom_adapter.OnLongPress(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        title = zoom_adapter.titles.get(flipBooKActivity.mAdapterFlipView.getSelectedItemPosition());
//        title = flipBooKActivity.mAdapterFlipView.getSelectedView().findViewById(R.id.title);
        title = flipBooKActivity.current_page.findViewById(R.id.title);
        return zoom_adapter.OnDoubleTap(e,title);
    }

}
