package com.example.abumuhsin.udusmini_library.Views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.models.top_filter_model;

import java.util.ArrayList;

public class Second_filter_recycler_adapter extends RecyclerView.Adapter {

    ArrayList<top_filter_model> list;
    private Context context;

    public Second_filter_recycler_adapter(Context context, ArrayList<top_filter_model> list,OnSecondFilterItemClick onSecondFilterItemClick) {
        this.context = context;
        this.list = list;
        this.onSecondFilterItemClick = onSecondFilterItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(list.get(position).getFilter());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt;

        public ViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.filer_txt);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSecondFilterItemClick.onSecondFilterItemClicked(getAdapterPosition(),txt);
                }
            });
        }

        public void bindData(String filter) {
            txt.setText(filter);
        }
    }
OnSecondFilterItemClick onSecondFilterItemClick;
    public interface OnSecondFilterItemClick {
        void onSecondFilterItemClicked(int position, TextView textView);
    }
}
