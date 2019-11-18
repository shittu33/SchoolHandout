package com.example.abumuhsin.udusmini_library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Data;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.FcmMessage;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FCM;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.Message;
import com.example.abumuhsin.udusmini_library.adapters.User;
import com.example.abumuhsin.udusmini_library.fragments.Discussion_fragment;
import com.example.abumuhsin.udusmini_library.fragments.OnlineBook_fragment;
import com.example.abumuhsin.udusmini_library.utils.GlideApp;
import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiscussionActivity extends AppCompatActivity {
    public static final String DISCUSSION_NOTIFICATION = "Discussion_Notification";
    String mServerKey;
    ArrayList<String> mTokens = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_recycler);

        FirebaseHandoutOperation.getServerKey(new FirebaseHandoutOperation.OnServerKeyRetrieved() {
            @Override
            public void onServerKeyRetrieved(String key) {
                mServerKey = key;
            }
        });
        FirebaseHandoutOperation.getUserTokens(new FirebaseHandoutOperation.OnUserTokenRetrieved() {
            @Override
            public void onUserTokenRetrieved(String token) {
                Log.i(TAG, "token " + token + " inside onUserTokenRetrieved");
                if (!mTokens.contains(token)) {
                    mTokens.add(token);
                    Log.i(TAG, "token " + token + " is added");
                } else {
                    Log.i(TAG, "token " + token + " is already added");
                }
            }
        });
        init();
    }

    MessagesListAdapter<Message> adapter;
    FirebaseHandoutOperation firebaseHandoutOperation;
    Handout handout;

    private void init() {
        initViews();
        firebaseHandoutOperation = new FirebaseHandoutOperation(this);
        Intent intent = getIntent();
        final String handout_id = intent.getStringExtra(Discussion_fragment.IS_FROM_DISUSSION_FRAGMENT);
        if (handout_id != null) {
            firebaseHandoutOperation.LoadOnlineHandout(handout_id, new FirebaseHandoutOperation.OnGetOnlineHandout() {
                @Override
                public void getOnlineHandout(Handout handout) {
                    DiscussionActivity.this.handout = handout;
                    ListenToDiscussions(handout);
                }
            });
        } else {
            handout = (Handout) intent.getSerializableExtra(OnlineBook_fragment.HANDOUT_EXTRA);
            ListenToDiscussions(handout);
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
                    GlideApp.with(getApplicationContext())
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

    private void initViews() {
        messageInput = findViewById(R.id.input);
        messagesList = findViewById(R.id.messagesList);
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
//                Message message = new Message(currentUserUid, "fdsfsd", new User("me", "shittu", "fdsf"), new Date(32423));
//                message.setText(input.toString());
                if (handout != null) {
                    firebaseHandoutOperation.DiscussHandout(handout, input.toString());
                    SendDiscussionNotification(handout.getHandout_title(), input.toString());
//                adapter.addToStart(message, true);
                }
                return true;
            }
        });
    }

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private static final String TAG = "DiscussionActivity";

    private void SendDiscussionNotification(String handout_title, String text) {
        Log.i(TAG, "Sending notifications to users");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FCM fcm = retrofit.create(FCM.class);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Key=" + mServerKey);
//        for (String token : mTokens) {
//            Log.i(TAG, "Sending Notification to token " + token);
            Log.i(TAG, "Sending Notification to token " + "CMP");
            Data data = new Data();
            data.setData_type(DISCUSSION_NOTIFICATION);
            data.setMessage(text);
            data.setTitle(handout_title);
            FcmMessage fcmMessage = new FcmMessage();
            fcmMessage.setData(data);
//            fcmMessage.setTo(token);
            fcmMessage.setTo("CMP");
            Call<ResponseBody> call = fcm.send(headers, fcmMessage);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.i(TAG, "onResponse: Server Response " + response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAG, "onFailure: Server Response filed");
                }
            });
        }
//    }
}
