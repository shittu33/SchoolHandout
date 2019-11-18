package com.example.abumuhsin.udusmini_library.firebaseStuff.util;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

public class FirebaseLoginOperation {
    public static final String DEALS = "deals";
    public static final int RC_SIGN_IN = 344;
    private static FirebaseLoginOperation firebaseLoginOperation;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static StorageReference storageReference;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    private static Activity caller_activity;
    public static boolean isAdim;
    private static FirebaseStorage firebaseStorage;

    public FirebaseLoginOperation() {

    }


    public static FirebaseLoginOperation get(final Activity activity) {
        FirebaseLoginOperation.caller_activity = activity;
        if (firebaseLoginOperation == null) {
            firebaseLoginOperation = new FirebaseLoginOperation();
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            databaseReference = firebaseDatabase.getReference();
            storageReference = firebaseStorage.getReference();
        }
        return firebaseLoginOperation;
    }

    private static FirebaseUser currentUser;

    public static boolean isUserLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null;
    }

    public static FirebaseUser getCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        return currentUser;
    }


    public static void RequestCurrentStudentInfo(final OnLogInOperation onLogInOperation) {
        if (isUserLoggedIn()) {
            String student_uid = currentUser.getUid();
            databaseReference.child("Students Info").child(student_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        onLogInOperation.onStudentInfoRetrieved(student);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onLogInOperation.onStudentInfoRetrivalFailed(databaseError);
                }
            });
        }
    }

    public static void uploadImage(Uri filePath, final String student_adm_no, final OnStudentImageUpload onStudentImageUpload) {

        if (filePath != null) {

//            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            final StorageReference ref = storageReference.child("student_images/" + student_adm_no);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    onStudentImageUpload.onStudentImageUploaded(uri, student_adm_no);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onStudentImageUpload.onStudentImageUploadFailed(e);
                        }
                    });
        }
    }

    public static void SignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller_activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private OnLogInOperation logInOperation;

    public interface OnLogInOperation {
        void onStudentInfoRetrieved(Student student);

        void onStudentInfoRetrivalFailed(DatabaseError databaseError);

    }

    public interface OnStudentImageUpload {
        void onStudentImageUploaded(Uri uri, String student_adm_no);

        void onStudentImageUploadFailed(Object object);

    }

}
