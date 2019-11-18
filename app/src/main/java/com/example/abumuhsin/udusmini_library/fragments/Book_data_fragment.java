package com.example.abumuhsin.udusmini_library.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.models.title_content_model;
import com.example.abumuhsin.udusmini_library.utils.DividerDecoration;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;

import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Book_data_fragment extends Fragment {

    private View view;
    private RecyclerView details_recycler;
    private Handout handout;
//    public ArrayList<Model_images> images_folder;

    public Book_data_fragment(Handout handout) {
        this.handout = handout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.book_data_layout, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    private void intiViews(View view) {
        details_recycler = view.findViewById(R.id.recycler);
    }

    ArrayList<title_content_model> list = new ArrayList<>();

    private void initAdapters() {
        list.add(new title_content_model("Course title", handout.getHandout_title()));
        list.add(new title_content_model("Course code", handout.getCourse_code()));
        list.add(new title_content_model("Author", handout.getPoster()));
        list.add(new title_content_model("Level", handout.getStudent_level()));
        list.add(new title_content_model("Course name", handout.getDepartment()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        DividerDecoration dividerItemDecoration = new DividerDecoration(Book_data_fragment.this.requireContext(),R.drawable.divider);
        details_recycler.addItemDecoration(dividerItemDecoration);
        details_recycler.setLayoutManager(linearLayoutManager);

        details_recycler.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.data_item, parent, false);
                return new ViewHolder(view);
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((ViewHolder) holder).bindData(list.get(position));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView value;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_tv);
            value = itemView.findViewById(R.id.value_tv);
        }

        public void bindData(title_content_model info) {
            title.setText(info.getTitle());
//            title.append(":");
            value.setText(info.getContent());
        }
    }

    public static final String MSG_EXTRA = "msg_indicator";
    public static final String BOOK_NAME_EXTRA = "msg_book";
    public static final String IMAGES_EXTRA = "msg_images";
    public static final String GALLERY_TAG = "galleryDEBUG";

    private void saveState() {
        new Fragment_Utils().saveState(this, "yes");
    }

    private void restoreState() {
        new Fragment_Utils().RestoreState(getFragmentManager(), this, "yes");
    }

}
