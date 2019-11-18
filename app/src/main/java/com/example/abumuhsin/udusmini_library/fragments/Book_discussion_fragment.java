package com.example.abumuhsin.udusmini_library.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.Message;
import com.example.abumuhsin.udusmini_library.adapters.User;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.example.amazing_picker.models.Model_images;
import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class Book_discussion_fragment extends Fragment {

    private View view;
    private RecyclerView likes_RecyclerView;
    public ArrayList<Model_images> images_folder;

    public Book_discussion_fragment(Handout handout) {
        this.handout = handout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_recycler, container, false);
        init(view);
        return view;
    }


    private void initViews(View view) {
        messageInput = view.findViewById(R.id.input);
        messagesList = view.findViewById(R.id.messagesList);
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                Message message = new Message(currentUserUid, "fdsfsd", new User("me", "shittu", "fdsf"), new Date(32423));
                message.setText(input.toString());
                if (handout != null) {
                    firebaseHandoutOperation.DiscussHandout(handout, input.toString());
                }
                adapter.addToStart(message, true);
                return true;
            }
        });

    }

    MessagesListAdapter<Message> adapter;
    FirebaseHandoutOperation firebaseHandoutOperation;
    Handout handout;

    private void init(View view) {
        initViews(view);
        firebaseHandoutOperation = new FirebaseHandoutOperation(this.requireContext());
        final String handout_id = handout.getHandout_id();
        if (handout_id != null) {
            firebaseHandoutOperation.LoadOnlineHandout(handout_id, new FirebaseHandoutOperation.OnGetOnlineHandout() {
                @Override
                public void getOnlineHandout(Handout handout) {
                    Book_discussion_fragment.this.handout = handout;
                    ListenToDiscussions(handout);
                }
            });
        }
        Message message = new Message("1", "fdsfsd", new User("3", "shittu", null), new Date(32423));
        Message message2 = new Message("2", "fdsfsd", new User("3", "shittu", null), new Date(32423));
        Message message3 = new Message("3", "fdsfsd", new User("3", "shittu", null), new Date(32423));
    }

    String currentUserUid;

    private void ListenToDiscussions(Handout handout) {
        FirebaseUser currentUser = FirebaseLoginOperation.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
            adapter = new MessagesListAdapter<>(currentUserUid, new ImageLoader() {
                @Override
                public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                    GlideApp.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.trimed_logo)
                            .into(imageView);
                }
            });
            messagesList.setAdapter(adapter);
            firebaseHandoutOperation.LoadHandoutMessages(handout, new FirebaseHandoutOperation.OnReadDisscussionMessage() {
                @Override
                public void onDiscussionAdded(Message message, int message_position) {
                    adapter.addToStart(message, true);
                }

                @Override
                public void onDiscussionSenderImageAdded(User user, int message_position) {
                }
            });
        }
    }

    MessageInput messageInput;
    MessagesList messagesList;
}