package com.example.abumuhsin.udusmini_library.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.fragments.Book_downloads_fragment;
import com.example.abumuhsin.udusmini_library.models.title_content_model;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;

import java.util.ArrayList;

import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.OPERATION_TAG;

public class DownloaderAdapter extends RecyclerView.Adapter {
    private Handout handout;
    ArrayList<title_content_model> downloders = new ArrayList<>();
    private Book_downloads_fragment book_downloads_fragment;

    public DownloaderAdapter(Book_downloads_fragment book_downloads_fragment, Handout handout) {
        this.book_downloads_fragment = book_downloads_fragment;
        this.handout = handout;
        if (handout != null) {
            new FirebaseHandoutOperation(book_downloads_fragment.requireContext()).getLikers(handout.getHandout_id(), new FirebaseHandoutOperation.OnGetLikers() {
                @Override
                public void onGetLiker(String handout_uid, Student liker) {
                    Log.i(OPERATION_TAG, "A liker  wad added");
                    downloders.add(new title_content_model(liker.getFull_name(), liker.getStudent_image_path()));
                    notifyItemInserted(downloders.size()-1);
                }

                @Override
                public void onLikerRemoved(String handout_uid, Student liker) {

                }

                @Override
                public void onGetLikerFailed(Object exception) {

                }
            });
        } else {
            Log.i(OPERATION_TAG, "handout is null");
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(book_downloads_fragment.requireContext()).inflate(R.layout.image_name_lay, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(downloders.get(position).getContent(), downloders.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return downloders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView student_img;
        private TextView student_name_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            student_img = itemView.findViewById(R.id.user_img);
            student_name_tv = itemView.findViewById(R.id.user_full_name);
        }

        public void bindData(String img_path, String student_name) {
            student_name_tv.setText(student_name);
            GlideApp.with(book_downloads_fragment.requireContext())
                    .load(img_path)
                    .placeholder(R.drawable.p2_480)
                    .into(student_img);
        }
    }

}
