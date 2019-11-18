package com.example.abumuhsin.udusmini_library.firebaseStuff.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessageService extends FirebaseMessagingService {
    public static final String TOKEN_NODE = "token";
    private static final String TAG = "FirebaseMessageService";
    public static final String USERS_TOKEN_NODE = "users_token";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG,"inside firebase Message service");
        String notification_title = "";
        String notification_body = "";
        String notification_data = "";

        try {
            notification_title = remoteMessage.getNotification().getTitle();
            notification_body = remoteMessage.getNotification().getBody();
            notification_data = remoteMessage.getData().toString();
        } catch (NullPointerException e) {
            Log.i(TAG, "onMessageReceived: NullPointerException:" + e.getMessage());
        }
        Log.i(TAG, "onMessageReceived: title:" + notification_title);
        Log.i(TAG, "onMessageReceived: body:" + notification_body);
        Log.i(TAG, "onMessageReceived: data:" + notification_data);

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.i("newToken", newToken);
                SendRegTokenToDatabase(newToken);
            }
        });
    }

    private void SendRegTokenToDatabase(String newToken) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference.child(USERS_TOKEN_NODE)
                    .child(currentUser.getUid())
                    .child(TOKEN_NODE)
                    .setValue(newToken);
        }else {
            Log.e(TAG, "No user Login yet");
        }
    }

}
