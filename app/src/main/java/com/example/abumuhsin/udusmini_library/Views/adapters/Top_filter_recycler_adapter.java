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

import java.util.LinkedList;

public class Top_filter_recycler_adapter extends RecyclerView.Adapter {

    LinkedList<top_filter_model> list;
    private Context context;

    public Top_filter_recycler_adapter(Context context, LinkedList<top_filter_model> list, OnTopFilterItemClick onTopFilterItemClick) {
        this.context = context;
        this.list = list;
        this.onTopFilterItemClick = onTopFilterItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item, parent, false);
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

        public ViewHolder(final View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.filer_txt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTopFilterItemClick.onTopFilterItemClicked(getAdapterPosition(), txt);
                }
            });
        }

        public void bindData(String filter) {
            txt.setText(filter);
        }
    }

    OnTopFilterItemClick onTopFilterItemClick;

    public interface OnTopFilterItemClick {
        void onTopFilterItemClicked(int position, TextView textView);
    }
}
