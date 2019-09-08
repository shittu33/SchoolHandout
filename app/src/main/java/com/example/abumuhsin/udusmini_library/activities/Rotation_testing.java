package com.example.abumuhsin.udusmini_library.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.abumuhsin.udusmini_library.R;

import androidx.annotation.Nullable;

/**
 * Created by Abu Muhsin on 29/11/2018.
 */

public class Rotation_testing extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotation);
        FrameLayout frame_view = (FrameLayout)findViewById(R.id.frame_view);
        FrameLayout frame_view2 = (FrameLayout)findViewById(R.id.frame_view2);
        float scale = getResources().getDisplayMetrics().density;
        frame_view.setCameraDistance(900*scale);
        frame_view.setRotationX(60);
        frame_view2.setCameraDistance(900*scale);
        frame_view2.setRotationX(60);
    }
}
