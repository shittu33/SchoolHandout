//package com.example.udushandoutadmin;
//
//import android.app.Activity;
//
//import androidx.annotation.NonNull;
//
//import com.firebase.ui.auth.AuthUI;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class FirebaseUtils {
//    public static final String DEALS = "deals";
//    public static final int RC_SIGN_IN = 344;
//    private static FirebaseUtils firebaseUtils;
//    public static FirebaseDatabase firebaseDatabase;
//    public static DatabaseReference databaseReference;
//    public static StorageReference storageReference;
//    public static FirebaseAuth firebaseAuth;
//    public static FirebaseAuth.AuthStateListener authStateListener;
////    public static ArrayList<TravelDeal> traveldeals;
//    private static Activity caller_activity;
//    public static boolean isAdim;
//
//    public FirebaseUtils() {
//
//    }
//
//
//    public static FirebaseUtils get(final Activity activity) {
//        FirebaseUtils.caller_activity = activity;
//        if (firebaseUtils == null) {
//            firebaseUtils = new FirebaseUtils();
//            firebaseAuth = FirebaseAuth.getInstance();
//        }
//        return firebaseUtils;
//    }
//
//
//    public static void OpenFbReference(String ref, final Activity activity) {
//        FirebaseUtils.caller_activity = activity;
//        if (firebaseUtils == null) {
//            firebaseUtils = new FirebaseUtils();
//            firebaseDatabase = FirebaseDatabase.getInstance();
//            firebaseAuth = FirebaseAuth.getInstance();
//            authStateListener = new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                    if (currentUser == null) {
//                        FirebaseUtils.SignIn();
////                        Toast.makeText(caller_activity.getBaseContext(), "you are welcome", Toast.LENGTH_SHORT).show();
//                    }else {
////                        checkAdmin(currentUser.getUid());
//                    }
//                }
//            };
//        }
////        traveldeals = new ArrayList<>();
////        databaseReference = firebaseDatabase.getReference().child(ref);
////        ConnectToStorage();
//    }
//
//    public static void SignIn() {
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//
//        caller_activity.startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
//    }
//    public static void ConnectToStorage() {
//        storageReference = FirebaseStorage.getInstance().getReference().child(DEALS);
//    }
//
//    public static void detachListener() {
//        firebaseAuth.removeAuthStateListener(authStateListener);
//    }
//
//    public static void attachListener() {
//        firebaseAuth.addAuthStateListener(authStateListener);
//    }
//}
