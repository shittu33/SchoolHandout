package com.example.abumuhsin.udusmini_library.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Handout_Posted_fragment extends Fragment{

    private View view;
    private RecyclerView likes_RecyclerView;
    public ArrayList<Student> students;

    public Handout_Posted_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.book_data_layout, container, false);
        FirebaseLoginOperation.get(requireActivity());
        init(view);
        return view;
    }


    private void init(View view) {
        intiViews(view);
        initAdapters();
    }

    private void intiViews(View view) {
        likes_RecyclerView = view.findViewById(R.id.recycler);
    }

    private void initAdapters() {
        students = new ArrayList<>();
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        likes_RecyclerView.setLayoutManager(gridLayoutManager);
        FirebaseLoginOperation.RequestCurrentStudentInfo(new FirebaseLoginOperation.OnLogInOperation() {
            @Override
            public void onStudentInfoRetrieved(Student student) {

            }

            @Override
            public void onStudentInfoRetrivalFailed(DatabaseError databaseError) {

            }
        });
        likes_RecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
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
