package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.GridOptionButton;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.LocalHandout;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;

import java.util.ArrayList;

public class Grid_adapter extends BaseAdapter implements View.OnClickListener, Filterable {

    MyBook_fragment fragment;
    ArrayList<LocalHandout> localHandouts;
    private GridOptionButton share_btn;
    private GridOptionButton upload_btn;
    private GridOptionButton pdf_btn;
    private ArrayFilter mFilter;

    public Grid_adapter(MyBook_fragment fragment, ArrayList<LocalHandout> localHandouts, OnBook_OptionsClickListener onBook_optionsClickListener) {
        this.fragment = fragment;
        this.localHandouts = localHandouts;
        this.onBook_optionsClickListener = onBook_optionsClickListener;
    }

    @Override
    public int getCount() {
        return localHandouts.size();
    }

    @Override
    public LocalHandout getItem(int i) {
        return localHandouts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    int click_index;

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        View v = view;
        ImageView img;
        TextView title, pages;
        if (v == null) {
            v = LayoutInflater.from(fragment.getContext()).inflate(R.layout.book_grid_item_two, viewGroup, false);
        }
        title = v.findViewById(R.id.title);
        pages = v.findViewById(R.id.no_pages);
        img = v.findViewById(R.id.img);
        share_btn = v.findViewById(R.id.share_btn);
        pdf_btn = v.findViewById(R.id.pdf_btn);
        upload_btn = v.findViewById(R.id.upload_btn);
        ImageView check_img = v.findViewById(R.id.check_img);
        String book_name = localHandouts.get(i).getTitle();
        int sum = localHandouts.get(i).getPage_no();
        String no_of_pages = String.valueOf(sum) + (sum == 1 ? " page" : " pages");
        String image_path = localHandouts.get(i).getCover();
        if (localHandouts.get(i).isChecked()) {
            check_img.setVisibility(View.VISIBLE);
        } else {
            check_img.setVisibility(View.GONE);
        }
        title.setText(book_name);
        GlideApp.with(fragment)

                .load(image_path)
                .centerCrop()
                .placeholder(R.drawable.trimed_logo)
                .into(img);
        pages.setText(no_of_pages);
        click_index = i;
        share_btn.setItem_position(i);
        upload_btn.setItem_position(i);
        pdf_btn.setItem_position(i);
        share_btn.setOnClickListener(this);
        upload_btn.setOnClickListener(this);
        pdf_btn.setOnClickListener(this);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onGridItemClickListener.onClick(((AdapterView) viewGroup),view,i,view.getId());
//                Toast.makeText(fragment.requireContext(), "this is just a bullshit", Toast.LENGTH_SHORT).show();
//            }
//        });
//        v.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                return onGridItemClickListener.onLongClick(((AdapterView) viewGroup),view,i,view.getId());
//            }
//        });
        return v;
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }

        return mFilter;

    }
    private ArrayList<LocalHandout> mOriginalValues;
    private final Object mLock = new Object();

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {

                synchronized (mLock) {

                    mOriginalValues = new ArrayList<>(localHandouts);

                }

            }

            if (prefix == null || prefix.length() == 0) {

                ArrayList<LocalHandout> list;

                synchronized (mLock) {

                    list = new ArrayList<>(mOriginalValues);

                }

                results.values = list;

                results.count = list.size();

            } else {

                String prefixString = prefix.toString().toLowerCase();

                ArrayList<LocalHandout> values;

                synchronized (mLock) {

                    values = new ArrayList<>(mOriginalValues);

                }

                final int count = values.size();

                final ArrayList<LocalHandout> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {

                    final LocalHandout value = values.get(i);

                    final String valueText = value.getTitle().toLowerCase();

                    final String course_code = value.getCourse_code().toLowerCase();
                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString) || course_code.contains(prefixString)) {

                        newValues.add(value);

                    } else {

                        final String[] words = valueText.split(" ");

                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)

                        for (int k = 0; k < wordCount; k++) {

                            if (words[k].startsWith(prefixString)) {

                                newValues.add(value);

                                break;

                            }

                        }

                    }

                }

                results.values = newValues;

                results.count = newValues.size();

            }

            return results;

        }

        @Override

        protected void publishResults(CharSequence constraint, FilterResults results) {

            localHandouts = (ArrayList<LocalHandout>) results.values;

            if (results.count > 0) {

                notifyDataSetChanged();

            } else {

                notifyDataSetInvalidated();

            }

        }

    }

    OnBook_OptionsClickListener onBook_optionsClickListener;

    public OnBook_OptionsClickListener getOnBook_optionsClickListener() {
        return onBook_optionsClickListener;
    }

    @Override
    public void onClick(View view) {
        final int i = ((GridOptionButton) view).getItem_position();
        switch (view.getId()) {
            case R.id.share_btn:
                onBook_optionsClickListener.onSharePdf(i, getItem(i).getTitle());
                break;
            case R.id.pdf_btn:
                onBook_optionsClickListener.onOpenWith(i, getItem(i).getTitle());
                break;
            case R.id.upload_btn:
                onBook_optionsClickListener.onUploadClick(i, getItem(i).getTitle());
                break;
        }
    }

    public interface OnBook_OptionsClickListener {
        void onDeleteClick(int position, String book_name);

        void onShareClick(int position, String book_name);

        void onUploadClick(int position, String book_name);

//      void onToPdfClick(int position, String book_name);

        void onOpenWith(int position, String book_name);

        void onSharePdf(int position, String book_name);

        void onShareBook(int position, String book_name);
    }
//    OnGridItemClickListener onGridItemClickListener;
//
//    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
//        this.onGridItemClickListener = onGridItemClickListener;
//    }

//    public interface OnGridItemClickListener{
//        void onClick(AdapterView<?> adapterView,View view,int position,long id);
//        boolean onLongClick(AdapterView<?> adapterView,View view,int position,long id);
//    }
}
