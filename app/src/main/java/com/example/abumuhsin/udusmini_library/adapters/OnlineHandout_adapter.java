package com.example.abumuhsin.udusmini_library.adapters;

/**
 * Created by Abu Muhsin on 26/09/2018.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.GridOptionButton;
import com.example.abumuhsin.udusmini_library.Views.LikeButton;
import com.example.abumuhsin.udusmini_library.models.OnlineHandout;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;

import java.util.ArrayList;

public class OnlineHandout_adapter extends BaseAdapter implements View.OnClickListener, LikeButton.OnLikeButtonListener {

    private Fragment fragment;
    private ArrayList<OnlineHandout> handouts;
    private GridOptionButton download_btn;
    private LikeButton like_btn;
    private GridOptionButton comment_btn;
    FirebaseHandoutOperation firebaseHandoutOperation;


    public OnlineHandout_adapter(Fragment fragment,ArrayList<OnlineHandout> handouts, OnBook_OptionsClickListener onBook_optionsClickListener) {
        firebaseHandoutOperation = new FirebaseHandoutOperation(fragment.requireContext());
        this.fragment = fragment;
        this.handouts = handouts;
//        ListenForAddedHandout();
        this.onBook_optionsClickListener = onBook_optionsClickListener;
    }
    @Override
    public int getCount() {
        return handouts.size();
    }

    @Override
    public Handout getItem(int i) {
        return handouts.get(i).getHandout();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    int position;

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView img;
        TextView title, pages;
        if (v == null) {
            v = LayoutInflater.from(fragment.getContext()).inflate(R.layout.online_book_grid_item, viewGroup, false);
        }
        title = v.findViewById(R.id.title);
        pages = v.findViewById(R.id.no_pages);
        img = v.findViewById(R.id.img);
        download_btn = v.findViewById(R.id.download_btn);
        like_btn = v.findViewById(R.id.like_btn);
        comment_btn = v.findViewById(R.id.comment_btn);
        ImageView check_img = v.findViewById(R.id.check_img);
        final String book_name = getItem(i).getHandout_title();
        int sum = getItem(i).getNo_of_pages();
        String no_of_pages = String.valueOf(sum) + (sum == 1 ? " page" : " pages");
        String image_path = getItem(i).getCover_url();
        if (getItem(i).isChecked()) {
            check_img.setVisibility(View.VISIBLE);
        } else {
            check_img.setVisibility(View.GONE);
        }
        title.setText(book_name);
        GlideApp.with(fragment)
                .load(image_path)
                .centerInside()
                .placeholder(R.drawable.trimed_logo)
                .into(img);
        pages.setText(no_of_pages);
        position = i;
        like_btn.setItem_position(i);
//        try {
        boolean is_user_like = handouts.get(i).isIs_user_like();
        long no_of_likers = getItem(i).getNo_of_likers();
        like_btn.forceToLike(is_user_like, no_of_likers);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//            Toast.makeText(fragment.requireContext(), "something is wrong with likes", Toast.LENGTH_SHORT).show();
//        }
        download_btn.setItem_position(i);
        comment_btn.setItem_position(i);
        like_btn.setOnLikeButtonListener(this);
        download_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);
        return v;
    }

    OnBook_OptionsClickListener onBook_optionsClickListener;

    public OnBook_OptionsClickListener getOnBook_optionsClickListener() {
        return onBook_optionsClickListener;
    }

    @Override
    public void onClick(View view) {
        int position = 0;
        if (view instanceof GridOptionButton) {
            position = ((GridOptionButton) view).getItem_position();
        }
        switch (view.getId()) {
            case R.id.comment_btn:
//                Todo
                Toast.makeText(fragment.requireContext(), "about to comment on handout of index" + position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.download_btn:
//                Todo
                onBook_optionsClickListener.onDownloadBtnClick(position, getItem(position).getHandout_title());
                break;
        }
    }

    @Override
    public void onLike(final View view, final int position) {
        firebaseHandoutOperation.checkIfCurrentUserLikeHandout(getItem(position).getHandout_id(), new FirebaseHandoutOperation.OnUserLikeListener() {
            @Override
            public void onUserLiked(String handout_uid, boolean is_like) {
                if (is_like) {
                    firebaseHandoutOperation.UnLikeHandout(getItem(position));
                } else {
                    firebaseHandoutOperation.LikeHandout(getItem(position));
                }
            }
        });
    }

//    @Override
//    public void onUnlike(final View view, int position) {
//        firebaseHandoutOperation.UnLikeHandout(handouts.get(position));
//    }

    public interface OnBook_OptionsClickListener {
        void onDeleteClick(int position, String book_name);

        void onShareClick(int position, String book_name);

        void onDownloadBtnClick(int position, String book_name);

//      void onToPdfClick(int position, String book_name);

        void onOpenWith(int position, String book_name);

        void onSharePdf(int position, String book_name);

        void onShareBook(int position, String book_name);
    }
}
