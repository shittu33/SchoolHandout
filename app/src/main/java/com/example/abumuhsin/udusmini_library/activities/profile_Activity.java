package com.example.abumuhsin.udusmini_library.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.Coordinator_layout;
import com.example.abumuhsin.udusmini_library.adapters.Profile_infoAdapter;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by Abu Muhsin on 14/11/2018.
 */

public class profile_Activity extends AppCompatActivity {
    public static final String ONLINE_BOOK_EXTRA = "online book extra";
    Coordinator_layout coordinator_layout;
    ArrayList<String> front_pages = new ArrayList<>();
    RecyclerView.Adapter handout_adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_cordinate_lay);
        initViews();
        initAdapters();
    }


    private void initViews() {
        coordinator_layout = findViewById(R.id.cordinate_lay);
    }

    private void initAdapters() {
        FirebaseHandoutOperation firebaseHandoutOperation = new FirebaseHandoutOperation(this);
        RecyclerView info_recycler = findViewById(R.id.info_recycler);
        RecyclerView gallery_recycler = findViewById(R.id.gallery_recycler);
        LinearLayoutManager info_linear = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager gallery_linear = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        info_recycler.setLayoutManager(info_linear);
        gallery_recycler.setLayoutManager(gallery_linear);
        coordinator_layout.setTitle("My profile");
        coordinator_layout.setBackEnable(true);
        info_recycler.setAdapter(new Profile_infoAdapter(this));
        FirebaseUser current_user = FirebaseLoginOperation.getCurrentUser();
        final ImageView cover_image = coordinator_layout.getCover_img();
        final TextView display_tv = coordinator_layout.getUser_full_name();
        //Set user photo
        firebaseHandoutOperation.LoadCurrentStudentImage(new FirebaseHandoutOperation.OnStudentImageLoaded() {
            @Override
            public void StudentImageLoaded(String student_image) {
                try {
                    GlideApp.with(profile_Activity.this).load(student_image).placeholder(R.drawable.trimed_logo).into(cover_image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "profile pic loading succeed");
            }

            @Override
            public void StudentImageLoadFailed(Object error) {
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "profile pic loading failed");
            }
        });

        firebaseHandoutOperation.LoadCurrentStudentName(new FirebaseHandoutOperation.OnStudentNameLoaded() {
            @Override
            public void StudentNameLoaded(String student_name) {
                display_tv.setText(student_name);
            }

            @Override
            public void StudentNameLoadFailed(Object error) {

            }
        });
        if (current_user != null) {
            firebaseHandoutOperation.LoadStudentHandouts(current_user.getUid(), new FirebaseHandoutOperation.OnGetUserHandouts() {
                @Override
                public void onGetUserHandouts(Handout handout) {
                    front_pages.add(handout.getCover_url());
                    handout_adapter.notifyItemInserted(front_pages.size() - 1);
                }
            });
        }
        handout_adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(profile_Activity.this).inflate(R.layout.profile_book_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (!front_pages.isEmpty()) {
                    ((ViewHolder) holder).bindData(front_pages.get(position));
                }
            }

            @Override
            public int getItemCount() {
                return front_pages.size();
            }
        };
        gallery_recycler.setAdapter(handout_adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.handout_img);
        }

        public void bindData(String img_path) {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img_path);
            GlideApp.with(profile_Activity.this)
                    .load(img_path)
                    .into(imageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
