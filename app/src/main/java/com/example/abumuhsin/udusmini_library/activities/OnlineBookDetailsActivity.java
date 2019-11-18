package com.example.abumuhsin.udusmini_library.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.Book_Coordinator_layout;
import com.example.abumuhsin.udusmini_library.Views.LikeButton;
import com.example.abumuhsin.udusmini_library.adapters.Handout_detail_adapter;
import com.example.abumuhsin.udusmini_library.fragments.Book_data_fragment;
import com.example.abumuhsin.udusmini_library.fragments.Book_downloads_fragment;
import com.example.abumuhsin.udusmini_library.fragments.Book_discussion_fragment;
import com.example.abumuhsin.udusmini_library.fragments.Book_likes_fragment;
import com.example.abumuhsin.udusmini_library.fragments.OnlineBook_fragment;
import com.google.firebase.database.DatabaseError;

import java.io.File;

public class OnlineBookDetailsActivity extends AppCompatActivity {
    public static final String ONLINE_BOOK_EXTRA = "online book extra";
    public static final String HANDOUT_COVER = "handout_cover";
    public static final String HANDOUT_POSTER = "handout_poster";
    public static final String HANDOUT_NO_PAGES = "no_of_pages";
    public static final String LIKERS = "Likers";
    FirebaseHandoutOperation.OnUserLikeListener onUserLikeListener = new FirebaseHandoutOperation.OnUserLikeListener() {
        @Override
        public void onUserLiked(String handout_uid, boolean is_like) {
            if (is_like) {
                like_btn.forceToLike(true, no_of_likers);
            } else {
                like_btn.forceToLike(false, no_of_likers);
            }
        }
    };
    ViewPager viewPager;
    Book_Coordinator_layout book_coordinator_layout;
    String book_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onlinebook_details_layout);
        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            book_title = intent.getStringExtra(ONLINE_BOOK_EXTRA);
        }
        initAdapters();
    }

    private void initViews() {
        viewPager = findViewById(R.id.pager);
        book_coordinator_layout = findViewById(R.id.cordinate_tab);
    }
    long no_of_likers;
    LikeButton like_btn;
    private Handout handout;

    public Handout getHandout() {
        return handout;
    }

    private void initAdapters() {
        Intent intent = getIntent();
        handout = (Handout) intent.getSerializableExtra(OnlineBook_fragment.HANDOUT_EXTRA);
        if (handout != null) {
            String image_path = handout.getCover_url();
            String no_of_page = String.valueOf(handout.getNo_of_pages());
            String handout_poster = handout.getPoster();
            no_of_likers = handout.getNo_of_likers();
            book_coordinator_layout.setBackEnable(true);
            Handout_detail_adapter handout_detail_adapter = new Handout_detail_adapter(this.getSupportFragmentManager());
            Book_likes_fragment book_likes_fragment = new Book_likes_fragment(handout);
            handout_detail_adapter.addFragments(book_likes_fragment, "Likes");
            handout_detail_adapter.addFragments(new Book_discussion_fragment(handout), "Discussion");
            handout_detail_adapter.addFragments(new Book_downloads_fragment(handout), "Description");
            handout_detail_adapter.addFragments(new Book_data_fragment(handout), "About");
            viewPager.setAdapter(handout_detail_adapter);
            book_coordinator_layout.setTitle(book_title);
            book_coordinator_layout.setupWithViewPager(viewPager);
            book_coordinator_layout.setHandoutImage(image_path);
            book_coordinator_layout.setPoster("Posted by " + handout_poster);
            book_coordinator_layout.setNo_pages(no_of_page + " pages");
            like_btn = book_coordinator_layout.getLike_btn();
            final FirebaseHandoutOperation firebaseHandoutOperation = new FirebaseHandoutOperation(this);
            firebaseHandoutOperation.checkIfCurrentUserLikeHandout(handout.getHandout_id(), onUserLikeListener);
            book_coordinator_layout.getDownload_btn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadHandout(handout);
                }
            });
            like_btn.setOnLikeButtonListener(new LikeButton.OnLikeButtonListener() {
                @Override
                public void onLike(View view, int position) {
                    firebaseHandoutOperation.checkIfCurrentUserLikeHandout(handout.getHandout_id(), new FirebaseHandoutOperation.OnUserLikeListener() {
                        @Override
                        public void onUserLiked(String handout_uid, boolean is_like) {
                            if (is_like) {
                                firebaseHandoutOperation.UnLikeHandout(handout);
                                like_btn.forceToLike(false, no_of_likers - 1 >= 0 ? no_of_likers - 1 : no_of_likers);
                            } else {
                                firebaseHandoutOperation.LikeHandout(handout);
                                like_btn.forceToLike(true, no_of_likers + 1);
                            }
                        }
                    });
                }
            });
        }
    }
    private ProgressDialog progressDialog;

    private void DownloadHandout(Handout handout) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Handout Download");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        new FirebaseHandoutOperation(this).DownloadHandout(handout, new FirebaseHandoutOperation.OnCompleteHandoutDownload() {
            @Override
            public void onHandoutDownloaded(Handout handout, File dest_file) {
                progressDialog.dismiss();
                Toast.makeText(OnlineBookDetailsActivity.this, "handout is completely downloaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHandoutDownloadStarted(Handout handout) {
                progressDialog.show();
            }

            @Override
            public void onHandoutDownloadProgress(Handout handout, double progress_byte, double total_byte) {
                int percent = (int) ((100 * progress_byte) / total_byte);
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "on progress " + percent);
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "current_byte is " + progress_byte);
                Log.i(FirebaseHandoutOperation.OPERATION_TAG, "total byte is " + total_byte);
                progressDialog.setProgress(percent);
            }

            @Override
            public void onHandoutDownloadFailed(Object error_obj) {
                progressDialog.dismiss();
                if (error_obj instanceof DatabaseError) {
                    Toast.makeText(OnlineBookDetailsActivity.this, ((DatabaseError) error_obj).getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error_obj instanceof Exception) {
                    Toast.makeText(OnlineBookDetailsActivity.this, ((Exception) error_obj).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
