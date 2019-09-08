package com.example.amazing_picker.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amazing_picker.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 17/12/2018.
 */

public class Pdf_recyler_adapter extends RecyclerView.Adapter<Pdf_recyler_adapter.ViewHolder> {
    private static final String recyclerTAG = "recycler";
    Activity context;
    ArrayList<String> pdf_paths;
    private int position;
    PdfCLickListener pdfCLickListener;

    public void setPosition(int position) {
        this.position = position;
    }

    public Pdf_recyler_adapter(Activity context, ArrayList<String> pdf_paths, PdfCLickListener pdfCLickListener) {
        Log.i(recyclerTAG, "Recycler Adapter constructor: called Pdfs");
        this.context = context;
        this.pdf_paths = pdf_paths;
        this.pdfCLickListener = pdfCLickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(recyclerTAG, " onCreateViewHolder: called pdfs");
        View v = LayoutInflater.from(context).inflate(R.layout.pdf_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i(recyclerTAG, " onBindViewHolder: called pdfs");
        final String pdf_path = pdf_paths.get(position);
        File file = new File(pdf_path);
        String pdf_name = file.getName();
        if (pdf_name.length()>19){
            pdf_name=pdf_name.substring(0,19) +"...";
        }
        holder.txt.setText(pdf_name);
        holder.pdf_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfCLickListener.OnPdfClicked(pdf_path,position);
            }
        });

    }

    public int getPosition() {
        return position;
    }


    public interface PdfCLickListener {
        void OnPdfClicked(String pdf_name, int position);
    }
    @Override
    public int getItemCount() {
        return pdf_paths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt;
        private View pdf_view;

        public ViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
            pdf_view = itemView;
        }
    }
}
