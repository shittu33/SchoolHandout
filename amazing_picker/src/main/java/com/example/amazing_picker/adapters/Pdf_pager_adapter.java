package com.example.amazing_picker.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.TouchImageView;
import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.models.SelectablePdf_page;
import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;

import java.io.File;
import java.util.ArrayList;

import static com.example.amazing_picker.utilities.PdfUtils.getPdfPageBitmapWithLow_quality;

public class Pdf_pager_adapter extends PagerAdapter {
    ArrayList<SelectablePdf_page> selectablePdf_pages;
    ArrayList<SelectablePdf_page> tmp_selectable_pages = new ArrayList<>();
    private int count;
    Picker_Activity context;

    public Pdf_pager_adapter(Activity c, ArrayList<SelectablePdf_page> list) {
        super();
        selectablePdf_pages = list;
        context = (Picker_Activity) c;
        if (!selectablePdf_pages.isEmpty()) {
            context.getSelect_btn().setChecked(selectablePdf_pages.get(0).is_page_selected());
            context.getSelect_btn().setText(selectablePdf_pages.get(0).is_page_selected() ? "Deselect" : "Select");
            tmp_selectable_pages.addAll(selectablePdf_pages);
        }
        updateSelectedNo(tmp_selectable_pages.size());
    }

    public void updateSelectedNo(int i) {
        String btn_txt_builder = "COPY" +
                "(" +
                i +
                ")";
        context.getCopy_btn().setText(btn_txt_builder);
    }

    public ArrayList<SelectablePdf_page> getSelectablePdf_pages() {
        return selectablePdf_pages;
    }

    public ArrayList<SelectablePdf_page> getTempSelected_PdfPages() {
        return tmp_selectable_pages;
    }

    public ArrayList<SelectablePdf_page> getNonSelected_PdfPages() {
        ArrayList<SelectablePdf_page> non_selected_pages = new ArrayList<>();
        if (!selectablePdf_pages.isEmpty()) {
            for (SelectablePdf_page selectablePdf_page : selectablePdf_pages) {
                if (!selectablePdf_page.is_page_selected()) {
                    non_selected_pages.add(selectablePdf_page);
                }
            }
        }
        return non_selected_pages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "TouchImageView is clear");
        final TouchImageView img = new TouchImageView(container.getContext());
        SelectablePdf_page selectablePdf_page = selectablePdf_pages.get(position);
        String page_path = selectablePdf_page.getPdf_page_path();
        File page_file = new File(page_path);
        if (page_file.exists()) {

//            Picasso.with(container.getContext())
//                    .load(page_file)
//                    .resize(220, 180).centerInside()
//                    .placeholder(R.drawable.trimed_logo)
//                    .noFade()
//                    .into(img);
            Glide.with(context)
                    .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
                    .load(page_file)
                    .placeholder(R.drawable.trimed_logo)
                    .thumbnail(0.3f)
                    .into(img);
        } else {
            img.setImageBitmap(getPdfPageBitmapWithLow_quality(context,context.getPdf_path(),position));

        }
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "image is set");
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Log.i(ImageCursorLoaderUtils.PIC_TAG, "image added to the container");
        return img;

    }


    @Override
    public int getCount() {
        return selectablePdf_pages.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "page " + (position + 1);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
