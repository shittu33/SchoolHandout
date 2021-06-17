package com.example.abumuhsin.udusmini_library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.DiscussionActivity;
import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.models.Dialog;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.OPERATION_TAG;

public class Discussion_fragment extends Fragment {

    public static final String IS_FROM_DISUSSION_FRAGMENT = "is_from_disussion_fragment";
    private DialogsList handout_msgList_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.requireContext()).inflate(R.layout.frag_book_discussion_list, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        initViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initViews(View view) {
        handout_msgList_view = view.findViewById(R.id.dialogsList);
    }

    private void initAdapter() {
        final FirebaseHandoutOperation firebaseHandoutOperation = new FirebaseHandoutOperation(this.requireContext());
        final DialogsListAdapter<Dialog> dialogsListAdapter = new DialogsListAdapter<>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                GlideApp.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.trimed_logo)
                        .into(imageView);
            }

        });
//        Dialog dialog = new Dialog();
//        dialogsListAdapter.addItems(dialog);
        dialogsListAdapter.setDatesFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return DateFormatter.format(date, DateFormatter.Template.TIME);
                } else if (DateFormatter.isYesterday(date)) {
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }

            }
        });
        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {
                String handout_id = dialog.getId();
                Intent intent = new Intent(Discussion_fragment.this.requireContext()
                        , DiscussionActivity.class);
                intent.putExtra(IS_FROM_DISUSSION_FRAGMENT, handout_id);
                startActivity(intent);
            }
        });
        firebaseHandoutOperation.LoadHandoutDisscussList(new FirebaseHandoutOperation.OnDiscussHandoutList() {
            @Override
            public void onDiscussHandoutAdded(Dialog dialog) {
                Log.i(OPERATION_TAG, dialog.getLastMessage() + " id is added");
                dialogsListAdapter.addItem(dialog);
            }

            @Override
            public void onDiscussHandoutUpdated(Dialog dialog) {

            }
        });
        handout_msgList_view.setAdapter(dialogsListAdapter);
    }
}
