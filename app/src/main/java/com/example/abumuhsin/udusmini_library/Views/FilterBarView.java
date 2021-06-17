package com.example.abumuhsin.udusmini_library.Views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.adapters.Second_filter_recycler_adapter;
import com.example.abumuhsin.udusmini_library.Views.adapters.Top_filter_recycler_adapter;
import com.example.abumuhsin.udusmini_library.models.top_filter_model;

import java.util.ArrayList;
import java.util.LinkedList;

public class FilterBarView extends LinearLayout {
    RecyclerView second_level_filter_recycler;
    RecyclerView top_level_filter_recycler;
    Top_filter_recycler_adapter top_filter_recycler_adapter;
    Second_filter_recycler_adapter second_filter_recycler_adapter;
    LinkedList<top_filter_model> top_list = new LinkedList<>();
    ArrayList<top_filter_model> second_list = new ArrayList<>();
    public FilterBarView(Context context) {
        super(context);
        init(context, null);
    }

    public FilterBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FilterBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setUpFIlterRecyclers(context);
    }


    private void setUpFIlterRecyclers(Context context) {
        setOrientation(VERTICAL);
        Resources resources = context.getResources();
        //Top level recycler
        top_level_filter_recycler = new RecyclerView(context);
        LayoutParams top_layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int top_level_padding = (int) resources.getDimension(R.dimen.top_level_filter_padding);
        top_level_filter_recycler.setPadding(top_level_padding, top_level_padding, top_level_padding, top_level_padding);
        top_level_filter_recycler.setBackgroundColor(resources.getColor(R.color.white));
        top_level_filter_recycler.setLayoutParams(top_layoutParams);
        //Second level recycelr
        second_level_filter_recycler = new RecyclerView(context);
        LayoutParams second_layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int second_level_margin = (int) resources.getDimension(R.dimen.second_level_margin);
        int second_level_margin_top = (int) resources.getDimension(R.dimen.second_level_margin_tòp);
        second_layoutParams.topMargin = second_level_margin_top;
        second_layoutParams.leftMargin = second_level_margin;
        second_layoutParams.rightMargin = second_level_margin;
        second_layoutParams.bottomMargin = second_level_margin;
        second_level_filter_recycler.setLayoutParams(second_layoutParams);
        addView(top_level_filter_recycler);
        addView(second_level_filter_recycler);
    }


    public void setUpTopFilterAdapter(final LinkedList<top_filter_model> topList) {
        //First adapter
        top_list = topList;
        LinearLayoutManager topLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        top_level_filter_recycler.setLayoutManager(topLayoutManager);
        top_filter_recycler_adapter = new Top_filter_recycler_adapter(getContext(), top_list, new Top_filter_recycler_adapter.OnTopFilterItemClick() {
            @Override
            public void onTopFilterItemClicked(int position, TextView textView) {
                onFilterItemClick.onTopFilterItemClicked(position,topList.get(position).getFilter_column(), textView.getText().toString(),topList);
            }
        });
        top_level_filter_recycler.setAdapter(top_filter_recycler_adapter);

    }

    public void setUpSecondFilterAdapter(final ArrayList<top_filter_model> secondList) {
        //second adapter
        second_list = secondList;
        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        second_level_filter_recycler.setLayoutManager(secondLayoutManager);
        second_filter_recycler_adapter = new Second_filter_recycler_adapter(getContext(), second_list, new Second_filter_recycler_adapter.OnSecondFilterItemClick() {
            @Override
            public void onSecondFilterItemClicked(int position, TextView textView) {
                String filter_column = secondList.get(position).getFilter_column();
                onFilterItemClick.onSecondFilterItemClicked(position,filter_column,textView.getText().toString(),secondList);
            }
        });
        second_level_filter_recycler.setAdapter(second_filter_recycler_adapter);
    }
    public void ReloadTopFilters(LinkedList<top_filter_model> top_list) {
        this.top_list = top_list;
        top_filter_recycler_adapter.notifyDataSetChanged();
//        top_filter_recycler_adapter.notifyItemInserted(top_list.size()-1);
    }
    public void ReloadSecondFilters(ArrayList<top_filter_model> secondList) {
        this.second_list = secondList;
//        second_filter_recycler_adapter.notifyDataSetChanged();
        second_filter_recycler_adapter.notifyItemInserted(secondList.size()-1);
    }
    OnFilterItemClick onFilterItemClick;

    public void setOnFilterItemClick(OnFilterItemClick onFilterItemClick) {
        this.onFilterItemClick = onFilterItemClick;
    }
    protected void onTopFilterItemClicked(int position, String fetched_column, String clicked_text){

    }
    protected void onSecondFilterItemClicked(int position, String fetched_column, String clicked_text){

    }

    public interface OnFilterItemClick {
        void onTopFilterItemClicked(int position, String fetched_column, String clicked_text, LinkedList<top_filter_model> topList);
        void onSecondFilterItemClicked(int position, String fetched_column, String clicked_text, ArrayList<top_filter_model> secondList);
    }
}
