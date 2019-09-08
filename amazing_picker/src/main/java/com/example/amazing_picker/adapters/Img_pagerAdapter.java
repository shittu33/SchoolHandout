package com.example.amazing_picker.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.TouchImageView;
import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.models.Selectable_image;
import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;

import java.io.File;
import java.util.ArrayList;

public class Img_pagerAdapter extends PagerAdapter {

    ArrayList<Selectable_image> images;
    ArrayList<Selectable_image> tmp_selected_images = new ArrayList<>();
    //        List<String> tmp_selected_images = new ArrayList<>();
    Picker_Activity picker_activity;

    //        private static int[] image = {R.drawable.p1_480, R.drawable.p2_480, R.drawable.p3_480, R.drawable.p5_720, R.drawable.p6_480};
    public Img_pagerAdapter(Picker_Activity picker_activity, ArrayList<Selectable_image> images) {
        this.images = images;
        this.picker_activity = picker_activity;
        if (!images.isEmpty()) {
            picker_activity.getSelect_btn().setChecked(images.get(0).isSelected());
            picker_activity.getSelect_btn().setText(images.get(0).isSelected() ? "Deselect" : "Select");
            tmp_selected_images.clear();
            tmp_selected_images.addAll(images);
        }
        updateSelectedNo(tmp_selected_images.size());
    }

    public void updateSelectedNo(int i) {
        String btn_txt_builder = "COPY" +
                "(" +
                i +
                ")";
        picker_activity.getCopy_btn().setText(btn_txt_builder);
    }


    public ArrayList<Selectable_image> getImages() {
        return images;
    }

    public ArrayList<Selectable_image> getTmp_selected_images() {
        return tmp_selected_images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String image_name = new File(images.get(position).getSelectable_path()).getName();
        return image_name.length() > 15 ? image_name.substring(0, 15) + "..." : image_name;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public View instantiateItem(ViewGroup container, int position) {
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "TouchImageView is clear");
        TouchImageView img = new TouchImageView(container.getContext());
        img.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        Selectable_image selectable_image = images.get(position);
        String image_path = selectable_image.getSelectable_path();
        Glide.with(container.getContext())
                .load(Uri.fromFile(new File(image_path)))
                    .placeholder(R.drawable.trimed_logo)
                .thumbnail(0.3f)
                .into(img);
//            Picasso.with(container.getContext())
//                    .load(Uri.fromFile(new File(image_path)))
//                    .resize(220, 180).centerInside()
//                    .placeholder(R.drawable.trimed_logo)
//                    .noFade()
//                    .into(img);
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "image is set");
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "image added to the container");
        return img;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}