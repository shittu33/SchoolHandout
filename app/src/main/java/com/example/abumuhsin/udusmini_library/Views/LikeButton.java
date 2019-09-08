package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;

public class LikeButton extends LinearLayout {
    private int item_position;

    LinearLayout.LayoutParams linear_param;
    LinearLayout.LayoutParams img_param;
    GridOptionImageView gridOptionImageView;
    TextView txt;
    Resources resources;

    public LikeButton(Context context) {
        super(context);
        init(context);

    }

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    boolean is_liked = false;

    private void init(final Context context) {
        resources = context.getResources();
        setUpLinearLayout(context);
        setUpImageView(context);
        setTextView(context);
        addView(gridOptionImageView);
        addView(txt);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    onLikeButtonListener.onLike(view, item_position);
            }
        });
    }

    public void forceToLike(boolean is_liked, long number) {
//        this.is_liked = is_liked;
        setLikeHighlight(is_liked);
        increaseLikeNumber(number);
    }

    private void increaseLikeNumber(long number) {
        StringBuilder likeBuilder = new StringBuilder(String.valueOf(number));
        likeBuilder.append(" Likes");
        txt.setText(likeBuilder);
    }

    public void setLikeHighlight(boolean is_liked){
        if (is_liked){
            gridOptionImageView.setColorFilter(resources.getColor(R.color.handout_grid_btns_color_unselected));
            gridOptionImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            txt.setTextColor(resources.getColor(R.color.handout_grid_btns_color));
        }else {
            gridOptionImageView.setColorFilter(resources.getColor(R.color.handout_grid_btns_color_unselected));
            gridOptionImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            txt.setTextColor(resources.getColor(R.color.handout_grid_btns_color_unselected));
        }
    }
    private void setTextView(Context context) {
        txt = new TextView(context);
        txt.setLayoutParams(img_param);
        txt.setText("0 likes");
        txt.setGravity(Gravity.CENTER);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        txt.setTextColor(resources.getColor(R.color.handout_grid_btns_color_unselected));
    }

    private void setUpImageView(Context context) {
        gridOptionImageView = new GridOptionImageView(context);
        img_param = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gridOptionImageView.setColorFilter(resources.getColor(R.color.handout_grid_btns_color_unselected));
        gridOptionImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        gridOptionImageView.setLayoutParams(img_param);
    }

    private void setUpLinearLayout(Context context) {
        linear_param = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        setLayoutParams(linear_param);
    }

    public int getItem_position() {
        return item_position;
    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }

    OnLikeButtonListener onLikeButtonListener;

    public void setOnLikeButtonListener(OnLikeButtonListener onLikeButtonListener) {
        this.onLikeButtonListener = onLikeButtonListener;
    }

    public interface OnLikeButtonListener {
        void onLike(View view, int position);

//        void onUnlike(View view, int position);
    }
}
