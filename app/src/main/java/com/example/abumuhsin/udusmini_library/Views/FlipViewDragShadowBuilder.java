package com.example.abumuhsin.udusmini_library.Views;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class FlipViewDragShadowBuilder extends View.DragShadowBuilder {

    // The drag shadow image, defined as a drawable thing
    private static Drawable shadow;
    public FlipViewDragShadowBuilder(View v, String item) {

        super(v);
//        shadow = new ColorDrawable(Color.LTGRAY);
//        shadow = v.getResources().getDrawable(R.drawable.an138_b);
    shadow = BitmapDrawable.createFromPath(item);
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        int width, height;
        width = getView().getWidth() / 4;
        height = getView().getHeight() / 4;
        shadow.setBounds(0, 0, width, height);
        size.set(width, height);

        touch.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
