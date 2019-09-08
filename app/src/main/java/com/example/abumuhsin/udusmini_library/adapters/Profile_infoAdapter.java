package com.example.abumuhsin.udusmini_library.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.fragments.Student_data_fragment;
import com.example.abumuhsin.udusmini_library.models.title_content_model;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class Profile_infoAdapter extends RecyclerView.Adapter {

    private Student_data_fragment student_data_fragment;
    ArrayList<title_content_model> list = new ArrayList<>();
    private Activity activity;

    public Profile_infoAdapter(Activity activity) {
        this.activity = activity;
//        list.add(new title_content_model("adm no", "34343"));
        FirebaseLoginOperation.RequestCurrentStudentInfo(new FirebaseLoginOperation.OnLogInOperation() {
            @Override
            public void onStudentInfoRetrieved(Student student) {
                list.add(new title_content_model("adm no", student.getAdm_no()));
                list.add(new title_content_model("sure name", student.getSurname()));
                list.add(new title_content_model("Email", student.getEmail()));
                list.add(new title_content_model("department", student.getStudent_department()));
                list.add(new title_content_model("Faculty", student.getStudent_faculty()));
                list.add(new title_content_model("Level", student.getStudent_level()));
                list.add(new title_content_model("Name", student.getFull_name()));
                notifyItemInserted(list.size() - 1);
            }

            @Override
            public void onStudentInfoRetrivalFailed(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.data_item, parent, false);
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
}
