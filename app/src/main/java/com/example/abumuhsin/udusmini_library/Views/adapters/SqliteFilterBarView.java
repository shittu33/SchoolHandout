package com.example.abumuhsin.udusmini_library.Views.adapters;

import android.content.Context;
import android.util.AttributeSet;
import com.example.abumuhsin.udusmini_library.Views.FilterBarView;

public class SqliteFilterBarView extends FilterBarView {

    public SqliteFilterBarView(Context context) {
        super(context);
    }

    public SqliteFilterBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqliteFilterBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTopFilterItemClicked(int position, String fetched_column, String clicked_text) {
        super.onTopFilterItemClicked(position, fetched_column, clicked_text);
    }

    @Override
    protected void onSecondFilterItemClicked(int position, String fetched_column, String clicked_text) {
        super.onSecondFilterItemClicked(position, fetched_column, clicked_text);
    }

    private void FillUpTopFilter(String clicked_text, String fetched_column) {
    }


}
