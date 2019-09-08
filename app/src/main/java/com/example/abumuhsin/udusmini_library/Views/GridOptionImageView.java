package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class GridOptionImageView extends AppCompatImageView {
    private int item_position;

    public GridOptionImageView(Context context) {
        super(context);
        init(context);

    }

    public GridOptionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridOptionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }

    public int getItem_position() {
        return item_position;
    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }
}
