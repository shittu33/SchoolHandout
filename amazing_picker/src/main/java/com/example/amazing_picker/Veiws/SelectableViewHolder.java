package com.example.amazing_picker.Veiws;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amazing_picker.R;
import com.example.amazing_picker.models.Selectable_image;


public class SelectableViewHolder extends RecyclerView.ViewHolder {
    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;

    public Selectable_image mImage;
    public OnRecylerItemSelected itemSelectedListener;
    public AppCompatRadioButton radio_btn;
    public ImageView img;

    public SelectableViewHolder(View view, OnRecylerItemSelected listener) {
        super(view);

        img = itemView.findViewById(R.id.img);
        radio_btn = itemView.findViewById(R.id.radio_btn);
        itemSelectedListener = listener;
        radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mImage.isSelected() && getItemViewType() == MULTI_SELECTION) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                itemSelectedListener.onImageSelected(mImage);

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mImage.isSelected() && getItemViewType() == MULTI_SELECTION) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                itemSelectedListener.onImageSelected(mImage);

            }
        });
    }

    public void setChecked(boolean value) {
        if (value) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                img.setImageAlpha(155);
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                img.setImageAlpha(255);
            }
        }
        mImage.set_selected(value);
        radio_btn.setChecked(value);

    }

    public interface OnRecylerItemSelected {

        void onImageSelected(Selectable_image image);
    }

}
