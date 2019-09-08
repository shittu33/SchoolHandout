package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.abumuhsin.udusmini_library.R;

public class GridOptionButton extends AppCompatImageView {
    private int item_position;

    public GridOptionButton(Context context) {
        super(context);
        init(context);

    }

    public GridOptionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridOptionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(context.getResources().getColor(R.color.transparent));
    }

    public int getItem_position() {
        return item_position;
    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }
}
