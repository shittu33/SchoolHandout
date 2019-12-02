package com.example.abumuhsin.udusmini_library.firebaseStuff;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.abumuhsin.udusmini_library.adapters.Message;
import com.example.abumuhsin.udusmini_library.adapters.User;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.DiscussData;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.DiscussMessage;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.RegisteredStudent;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.fragments.Dialog;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.example.abumuhsin.udusmini_library.firebaseStuff.services.FirebaseMessageService.TOKEN_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.services.FirebaseMessageService.USERS_TOKEN_NODE;

public class FirebaseHandoutOperation {
    public static final String OPERATION_TAG = "handout_opera_tag";
    public static final String DEPARTMENT_CAT_NODE = "department";
    public static final String FACULTY_CAT_NODE = "faculty";
    public static final String LEVEL_CAT_NODE = "level";
    public static final String COURSE_CODE_CAT_NODE = "course_code";
    public static final String FILTER_NODE = "handout filters";
    public static final String CATEGORY_NODE = "handout category";
    public static final String UPLOAD_TIME_STAMP_CAT_NODE = "upload_time_stamp";

    private Context context;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference course_code_reference;
    DatabaseReference dept_refrence;
    DatabaseReference fact_refrence;
    DatabaseReference level_refrence;
    DatabaseReference discussion_data_ref;
    DatabaseReference discussion_messages_ref;
    private final DatabaseReference handout_cat_ref;

    public FirebaseHandoutOperation(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
        handout_cat_ref = databaseReference.child(CATEGORY_NODE);
        dept_refrence = databaseReference.child(FILTER_NODE).child(DEPARTMENT_CAT_NODE);
        fact_refrence = databaseReference.child(FILTER_NODE).child(FACULTY_CAT_NODE);
        level_refrence = databaseReference.child(FILTER_NODE).child(LEVEL_CAT_NODE);
        course_code_reference = databaseReference.child(FILTER_NODE).child(COURSE_CODE_CAT_NODE);
        discussion_data_ref = databaseReference.child("discussion data");
        discussion_messages_ref = databaseReference.child("discussion messages");

    }

    public void LoadCurrentStudentImage(boolean is_from_cache, final OnStudentImageLoaded onStudentImageLoaded) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            LoadStudentImage(currentUser.getUid(), is_from_cache, onStudentImageLoaded);
        }
    }

    public void LoadCurrentStudentImageUrl(final OnStudentImageLoaded onStudentImageLoaded) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            LoadStudentImageUrl(currentUser.getUid(), onStudentImageLoaded);
        }
    }

    public void LoadCurrentStudentName(final OnStudentNameLoaded onStudentNameLoaded) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            LoadStudentName(currentUser.getUid(), onStudentNameLoaded);
        }
    }

    public void LoadStudentImageUrl(final String uid, final OnStudentImageLoaded onStudentImageLoaded) {
        databaseReference.child("Students Info").child(uid).child("student_image_path").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                onStudentImageLoaded.StudentImageLoaded(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onStudentImageLoaded.StudentImageLoadFailed(databaseError);
            }
        });
    }

    public void LoadStudentImage(final String uid, final boolean is_from_cache, final OnStudentImageLoaded onStudentImageLoaded) {
        databaseReference.child("Students Info").child(uid).child("student_image_path").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                StorageReference student_image_bucket_file = null;
                if (url != null) {
                    student_image_bucket_file = firebaseStorage.getReferenceFromUrl(url);
                    final File dest_file = new File(FileUtils.getOnlineProfileFile(uid));
                    if (!dest_file.exists() || !is_from_cache) {
                        student_image_bucket_file.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                onStudentImageLoaded.StudentImageLoaded(dest_file.getPath());
                            }
                        });
                    } else {
                        onStudentImageLoaded.StudentImageLoaded(dest_file.getPath());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onStudentImageLoaded.StudentImageLoadFailed(databaseError);
            }
        });
    }

    public void LoadStudentName(final String uid, final OnStudentNameLoaded onStudentNameLoaded) {
        databaseReference.child("Students Info").child(uid).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String full_name = dataSnapshot.getValue(String.class);
                onStudentNameLoaded.StudentNameLoaded(full_name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onStudentNameLoaded.StudentNameLoadFailed(databaseError);
            }
        });
    }

    public void LoadStudentHandouts(String user_id, final OnGetUserHandouts onGetUserHandouts) {
        databaseReference.child("Student Handouts").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String added_handout_id = dataSnapshot.getKey();
                if (added_handout_id != null) {
                    DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts").child(added_handout_id);
                    handouts_reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Handout handout = dataSnapshot.getValue(Handout.class);
                            if (handout != null) {
                                handout.setHandout_id(added_handout_id);
                                final StorageReference cover_bucket_reference = firebaseStorage.getReferenceFromUrl(handout.getCover_url());
                                final File dest_file;
                                dest_file = new File(FileUtils.getOnlineHandoutFile(added_handout_id));
                                if (!dest_file.exists()) {
                                    cover_bucket_reference.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            handout.setCover_url(dest_file.getPath());
                                            onGetUserHandouts.onGetUserHandouts(handout);
                                            Log.i(OPERATION_TAG, "cover download success");
                                        }
                                    });
                                } else {
                                    handout.setCover_url(dest_file.getPath());
                                    onGetUserHandouts.onGetUserHandouts(handout);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                FileUtils.DeleteHandoutCoverImage(handout.getHandout_id());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void UploadHandout(final Handout handout, Uri handout_zip_uri, final OnHandoutUpload onHandoutUpload) {
        String time_name = String.valueOf(System.currentTimeMillis());
        String root = handout.getCourse_code() + "_" + time_name;
        String zip_file = "Handout" + "_" + time_name + ".zip";
        String cover_file = "cover" + ".png";
        final StorageReference handout_storage_ref = storageReference.child(root + "/" + zip_file);
        final StorageReference cover_storage_ref = storageReference.child(root + "/" + cover_file);
        //put handout inside bucket
        handout_storage_ref.putFile(handout_zip_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        handout_storage_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                if (!TextUtils.isEmpty(url)) {
                                    Log.i(OPERATION_TAG, "the uri of the saved handout path is " + url);
                                    handout.setHandout_url(url);
                                    //put cover inside bucket
                                    cover_storage_ref.putFile(Uri.fromFile(new File(handout.getCover_url()))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            cover_storage_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String url = uri.toString();
                                                    if (!TextUtils.isEmpty(url)) {
                                                        Log.i(OPERATION_TAG, "the uri of the saved cover path is " + url);
                                                        handout.setCover_url(url);
                                                        final DatabaseReference handouts_refrence = databaseReference.child("Handouts").push();
                                                        //Finally save handout information
                                                        handouts_refrence.setValue(handout)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        handouts_refrence.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                String handout_id = dataSnapshot.getKey();
                                                                                Log.i(OPERATION_TAG, "the user id is " + handout_id);
                                                                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                                                if (currentUser != null) {
                                                                                    String current_user_id = currentUser.getUid();
                                                                                    databaseReference.child("Student Handouts").child(current_user_id)
                                                                                            .child(handout_id).setValue(true);
                                                                                }
                                                                                handout.setHandout_id(handout_id);
                                                                                setHandoutCategory(handout);
                                                                                onHandoutUpload.onHandoutUploaded(handout);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        onHandoutUpload.onHandoutUploadFailed(handout, e);

                                                                    }
                                                                });
                                                    }

                                                }
                                            });
                                        }
                                    });

                                } else {
                                    Log.i(OPERATION_TAG, "the uri is empty");
                                }
                            }

                        });

                    }
                });

    }
    private void setHandoutCategory(Handout handout) {
        String handout_dept = handout.getDepartment();
        String handout_fact = handout.getFaculty();
        String handout_level = handout.getStudent_level();
        String handout_code = handout.getCourse_code()/*.split(" ")[1]*/;
        /**set Faculty filters**/
        fact_refrence.child(handout_fact).setValue(handout_fact);
        /**set depatments filters**/
        dept_refrence.child(handout_dept).child("filter_name").setValue(handout_dept);
        dept_refrence.child(handout_dept).child(handout_fact).setValue(true);
        /**set level filters**/
        level_refrence.child(handout_level).child("filter_name").setValue(handout_level);
        level_refrence.child(handout_level).child(handout_dept).setValue(true);
        level_refrence.child(handout_level).child(handout_fact).setValue(true);
        /**set course code filters**/
        course_code_reference.child(handout_code).setValue(handout_code);
        /**categorize handouts**/
        DatabaseReference handout_cat_reference = databaseReference.child(CATEGORY_NODE).child(handout.getHandout_id());
        handout_cat_reference.child(handout_dept).setValue(true);
        handout_cat_reference.child(handout_fact).setValue(true);
        handout_cat_reference.child(handout_level).setValue(true);
        handout_cat_reference.child(handout_code).setValue(true);
    }
    public void LoadOnlineHandout(String handout_id, final OnGetOnlineHandout onGetOnlineHandout) {
        DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts").child(handout_id);
        handouts_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                    onGetOnlineHandout.getOnlineHandout(handout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static final String TAG = "FirebaseHandoutOperatio";

    public void LoadAllHandouts(final onLoadingHandoutInformation onLoadingHandoutInformation) {
        Log.i(TAG, "b4 initializing handouts_reference");
        DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts");
        Log.i(TAG, "after initializing handouts_reference");
        handouts_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "inside onDataChange");
                onLoadingHandoutInformation.onAllHandoutAdded(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "inside onDataChange");
            }
        });
        Log.i(TAG, "b4 ChildEventListener");
        handouts_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "addChildEventListener: onChildAdded");
//                final Handout handout = dataSnapshot.getValue(Handout.class);
                LoadHandoutUnder(dataSnapshot, onLoadingHandoutInformation);
                Log.i(TAG, "onChildAdded: after LoadHandoutUnder ");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                }
                onLoadingHandoutInformation.onHandoutChanged(handout);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                    FileUtils.DeleteHandoutCoverImage(handout.getHandout_id());
                }
                onLoadingHandoutInformation.onHandoutRemoved(handout);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "addChildEventListener: onChildMoved ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "addChildEventListener: onCancelled ");
            }
        });
    }

    public void LoadAllHandoutsWhereFilter(final String filter, final String alternative_filter, final String fetched_column
            , final onLoadingHandoutInformation onLoadingHandoutInformation) {
        handout_cat_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts");
                handout_cat_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        onLoadingHandoutInformation.onAllHandoutAdded(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                String handout_id = dataSnapshot.getKey();
                if (handout_id != null) {
                    DataSnapshot handout_filter_shot = dataSnapshot.child(handout_id);
                    boolean is_faculty_and_department = fetched_column.equals(LEVEL_CAT_NODE) && handout_filter_shot.hasChild(filter)
                            && handout_filter_shot.hasChild(alternative_filter);
                    if (is_faculty_and_department || handout_filter_shot.hasChild(filter)) {
                        handouts_reference.child(handout_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                Handout handout = dataSnapshot.getValue(Handout.class);
                                LoadHandoutUnder(dataSnapshot, onLoadingHandoutInformation);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadAllHandoutsWhere(final String filter, final String alternative_filter, final String fetched_column, final onLoadingHandoutInformation onLoadingHandoutInformation) {
        DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts");
        handouts_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onLoadingHandoutInformation.onAllHandoutAdded(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        handouts_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                    String handout_id = handout.getHandout_id();
                    handout_cat_ref.child(handout_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean is_faculty_and_department = fetched_column.equals(LEVEL_CAT_NODE) && dataSnapshot.hasChild(filter) && dataSnapshot.hasChild(alternative_filter);
                            boolean is_normal_filter = !fetched_column.equals(LEVEL_CAT_NODE) && dataSnapshot.hasChild(filter);
                            if (is_faculty_and_department || is_normal_filter) {
                                LoadHandoutUnder(dataSnapshot, onLoadingHandoutInformation);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                }
                onLoadingHandoutInformation.onHandoutChanged(handout);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                    FileUtils.DeleteHandoutCoverImage(handout.getHandout_id());
                }
                onLoadingHandoutInformation.onHandoutRemoved(handout);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void LoadHandoutUnder(DataSnapshot dataSnapshot, final onLoadingHandoutInformation onLoadingHandoutInformation) {
            final Handout handout = dataSnapshot.getValue(Handout.class);
        if (handout != null) {
            Log.i(OPERATION_TAG, "handout is not null");
            handout.setHandout_id(dataSnapshot.getKey());
            Log.i(OPERATION_TAG, "LoadHandoutUnder: handout id is " + handout.getHandout_id());
            Log.i(OPERATION_TAG, "LoadHandoutUnder: handout path is " + handout.getHandout_url());
            Log.i(OPERATION_TAG, "LoadHandoutUnder: handout cover path is " + handout.getCover_url());
            Log.i(OPERATION_TAG, "LoadHandoutUnder: handout url is " + firebaseStorage.getReferenceFromUrl(handout.getHandout_url()));
            Log.i(OPERATION_TAG, "LoadHandoutUnder: handout cover url is " + firebaseStorage.getReferenceFromUrl(handout.getCover_url()));
            StorageReference cover_bucket_reference = null;
            try {
                cover_bucket_reference = firebaseStorage.getReferenceFromUrl(handout.getCover_url());
                Log.i(OPERATION_TAG, "LoadHandoutUnder: after cover_bucket_reference");
            } catch (Exception e) {
                Log.i(OPERATION_TAG, "LoadHandoutUnder: " + e.getMessage());
                e.printStackTrace();
            }
            final File dest_file;
            dest_file = new File(FileUtils.getOnlineHandoutFile(handout.getHandout_id()));
            Log.i(OPERATION_TAG, "LoadHandoutUnder: after dest_file");
            if (!dest_file.exists()) {
                Log.i(OPERATION_TAG, "LoadHandoutUnder: handout img doesn't exist");
                cover_bucket_reference.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        handout.setCover_url(dest_file.getPath());
                        String poster_adm_no = handout.getPoster();
                        databaseReference.child("registered student").child(poster_adm_no).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.i(OPERATION_TAG, "LoadHandoutUnder: inside onDataChange");
                                RegisteredStudent registeredStudent = dataSnapshot.getValue(RegisteredStudent.class);
                                if (registeredStudent != null) {
                                    handout.setPoster(registeredStudent.getFull_name());
                                    onLoadingHandoutInformation.onHandoutAdded(handout);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        Log.i(OPERATION_TAG, "cover download success");
                    }
                });
            } else {
                Log.i(OPERATION_TAG, "dest image doesn't exist");
                handout.setCover_url(dest_file.getPath());
                String poster_adm_no = handout.getPoster();
                databaseReference.child("registered student").child(poster_adm_no).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i(OPERATION_TAG, "LoadHandoutUnder: onDataChange");
                        RegisteredStudent registeredStudent = dataSnapshot.getValue(RegisteredStudent.class);
                        if (registeredStudent != null) {
                            Log.i(OPERATION_TAG, "LoadHandoutUnder: registeredStudent");
                            handout.setPoster(registeredStudent.getFull_name());
                            onLoadingHandoutInformation.onHandoutAdded(handout);
                            Log.i(OPERATION_TAG, "LoadHandoutUnder: onHandoutAdded");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(OPERATION_TAG, "LoadHandoutUnder: onCancelled");
                    }
                });
            }
        }
        Log.i(OPERATION_TAG, "escape LoadHandoutUnder");
    }
    public static void getServerKey(final OnServerKeyRetrieved onServerKeyRetrieved){
        Query query = FirebaseDatabase.getInstance().getReference().child("server")
                .orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String server_key = dataSnapshot.getChildren().iterator().next().getValue().toString();
                Log.i(OPERATION_TAG,"hey i found the key :" + server_key);
                onServerKeyRetrieved.onServerKeyRetrieved(server_key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUserTokens(final OnUserTokenRetrieved onUserTokenRetrieved){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USERS_TOKEN_NODE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(OPERATION_TAG,"About to find tokens");
                String key = dataSnapshot.getKey();
                if (key!=null) {
                    Log.i(OPERATION_TAG,"key is not null");
                    Log.i(OPERATION_TAG,"key is " + key);
                    String token = dataSnapshot.child(TOKEN_NODE).getValue(String.class);
                    Log.i(OPERATION_TAG,"token is " + token);
                    onUserTokenRetrieved.onUserTokenRetrieved(token);
                }else {
                    Log.i(OPERATION_TAG,"no user token is found");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void RequestRegisteredInfo(String adm_no, final OnRequestRegistrationInfo onRequestRegistrationInfo) {
        databaseReference.child("registered student").child(adm_no).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RegisteredStudent registeredStudent = dataSnapshot.getValue(RegisteredStudent.class);
                if (registeredStudent != null) {
                    onRequestRegistrationInfo.onStudentInfoRetrieved(registeredStudent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onRequestRegistrationInfo.onStudentInfoRetrivalFailed(databaseError);
            }
        });
    }

    public void DownloadHandout(final Handout handout,
                                final OnCompleteHandoutDownload onCompleteHandoutDownload) {
        onCompleteHandoutDownload.onHandoutDownloadStarted(handout);
        firebaseDatabase.getReference().child("Handouts").child(handout.getHandout_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Handout handout1 = dataSnapshot.getValue(Handout.class);
                if (handout1 != null) {
                    String handout_url = handout1.getHandout_url();
                    final StorageReference bucket_reference = firebaseStorage.getReferenceFromUrl(handout_url);
                    try {
                        final File dest_file = File.createTempFile("zip", "zip");
                        bucket_reference.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                onCompleteHandoutDownload.onHandoutDownloaded(handout1, dest_file);
                                Toast.makeText(context, "File " + dest_file.getName() + "is downloaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onCompleteHandoutDownload.onHandoutDownloadFailed(e);
                                Toast.makeText(context, "File " + dest_file.getName() + "has failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                double progress_byte = taskSnapshot.getBytesTransferred();
                                double total_byte = (taskSnapshot.getTotalByteCount());
                                onCompleteHandoutDownload.onHandoutDownloadProgress(handout, progress_byte, total_byte);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    final File dest_file = new File(FileUtils.getDownloadFilePath(),handout1.getHandout_title()+" "+ handout1.getPoster()+ ".zip");

                } else {
                    Log.i(OPERATION_TAG, "handout is null here men");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onCompleteHandoutDownload.onHandoutDownloadFailed(databaseError);
                Log.i(OPERATION_TAG, "handout is not present");
//                onCompleteHandoutDownload.onHandoutDownloadFailed(databaseError);
            }
        });
    }

    public void DeleteHandout(final Handout handout,
                              final OnHandoutDeleteListener onHandoutDeleteListener) {
        final DatabaseReference handouts_data_reference = firebaseDatabase.getReference().child("Handouts").child(handout.getHandout_id());
        handouts_data_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Handout online_handout = dataSnapshot.getValue(Handout.class);
                if (online_handout != null) {
                    final String handout_url = online_handout.getHandout_url();
                    final StorageReference handout_storage_reference = firebaseStorage.getReferenceFromUrl(handout_url);
                    handout_storage_reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            handouts_data_reference.removeValue();
                            onHandoutDeleteListener.onHandoutDeleted(online_handout, storageReference.getPath());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onHandoutDeleteListener.onHandoutDeletionFailed(e);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onHandoutDeleteListener.onHandoutDeletionFailed(databaseError);
            }
        });
    }

    public void getLikers(final String handout_uid, final OnGetLikers onGetLikers) {

        final DatabaseReference handouts_like_reference = firebaseDatabase.getReference().child("Handout_Likes").child(handout_uid);
        handouts_like_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid = dataSnapshot.getKey();
                Log.i(OPERATION_TAG, "liker id is" + uid);
                if (uid != null) {
                    firebaseDatabase.getReference().child("Students Info").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Student liker = dataSnapshot.getValue(Student.class);
                            if (liker != null) {
                                Log.i(OPERATION_TAG, "Student name is" + liker.getFull_name());
                                StorageReference student_image_bucket_file = firebaseStorage.getReferenceFromUrl(liker.getStudent_image_path());
//                                try {
//                                    final File dest_file = File.createTempFile("student", "png");
                                final File dest_file = new File(FileUtils.getOnlineProfileFile(liker.getAdm_no()));
                                if (!dest_file.exists()) {
                                    student_image_bucket_file.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            liker.setStudent_image_path(dest_file.getPath());
                                            onGetLikers.onGetLiker(handout_uid, liker);
                                            Log.i(OPERATION_TAG, "liker image download success");
                                        }
                                    });
                                } else {
                                    liker.setStudent_image_path(dest_file.getPath());
                                    onGetLikers.onGetLiker(handout_uid, liker);
                                    Log.i(OPERATION_TAG, "liker image download success");
                                }
//                                    }
//                                } catch (IOException e) {
//                                    Log.i(OPERATION_TAG, "something happen to the file");
//                                    e.printStackTrace();
//                                }
                            } else {
                                Log.i(OPERATION_TAG, "liker is null");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getKey();
                if (uid != null) {
                    firebaseDatabase.getReference().child("Students Info").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Student liker = dataSnapshot.getValue(Student.class);
                            FileUtils.DeleteProfileImage(liker.getAdm_no());
                            onGetLikers.onLikerRemoved(handout_uid, liker);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onGetLikers.onGetLikerFailed(databaseError);
            }
        });
    }

    public boolean is_Liked(DataSnapshot dataSnapshot, String student_id) {
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            String online_id = dataSnapshot1.getValue(String.class);
            if (online_id != null && online_id.equals(student_id)) {
                Log.i(OPERATION_TAG, "user is in the loop");
                return true;
            }
        }
        Log.i(OPERATION_TAG, "user is not in the loop");
        return false;
    }

    public void checkIfCurrentUserLikeHandout(final String handout_id,
                                              final OnUserLikeListener onUserLikeListener) {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
//            final String current_user_id = currentUser.getUid();
            final DatabaseReference handouts_like_reference = firebaseDatabase.getReference().child("Handout_Likes").child(handout_id)/*.child(current_user_id)*/;
            handouts_like_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        String current_user_id = currentUser.getUid();
                        if (!dataSnapshot.exists()) {
                            Log.i(OPERATION_TAG, "user " + current_user_id + "haven't like this before");
                            onUserLikeListener.onUserLiked(handout_id, false);

                        } else {
                            if (dataSnapshot.hasChild(current_user_id)) {
                                onUserLikeListener.onUserLiked(handout_id, true);
                            } else {
                                Log.i(OPERATION_TAG, "user " + current_user_id + "haven't like this before");
                                onUserLikeListener.onUserLiked(handout_id, false);

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    onUserLikeListener.onUserLiked(handout_id, false);
                }
            });
        }
    }

    public void LikeHandout(final Handout handout) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            final String uid = currentUser.getUid();
            final DatabaseReference handouts_like_reference = firebaseDatabase.getReference().child("Handout_Likes").child(handout.getHandout_id()).child(uid);
            handouts_like_reference.setValue(true);
            final DatabaseReference handout_ref = firebaseDatabase.getReference().child("Handouts").child(handout.getHandout_id()).child("no_of_likers");
            handout_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long no_of_likers = dataSnapshot.getValue(Long.class);
                    if (no_of_likers != null) {
                        handout_ref.setValue(no_of_likers + 1);
                    } else {
                        Log.i(OPERATION_TAG, "No of likers is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(context, "you are not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void UnLikeHandout(final Handout handout) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            final String uid = currentUser.getUid();
            final DatabaseReference handouts_like_reference = firebaseDatabase.getReference().child("Handout_Likes").child(handout.getHandout_id()).child(uid);
            handouts_like_reference.removeValue();
            final DatabaseReference handout_ref = firebaseDatabase.getReference().child("Handouts").child(handout.getHandout_id()).child("no_of_likers");
            handout_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long no_of_likers = dataSnapshot.getValue(Long.class);
                    if (no_of_likers != null && no_of_likers > 0) {
                        handout_ref.setValue(no_of_likers - 1);
                    } else {
                        Log.i(OPERATION_TAG, "No of likers is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(context, "you are not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void ListenForLikes(final String handout_uid,
                               final OnHandoutLikeListener onHandoutLikeListener) {
        final DatabaseReference handouts_like_reference = firebaseDatabase.getReference().child("Handout_Likes").child(handout_uid);
        handouts_like_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String liker_uid = dataSnapshot.getValue(String.class);
                onHandoutLikeListener.onHandoutLiked(handout_uid, liker_uid, true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String liker_uid = dataSnapshot.getValue(String.class);
                onHandoutLikeListener.onHandoutLiked(handout_uid, liker_uid, false);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onHandoutLikeListener.onHandoutLikeFailed(databaseError);
            }
        });
    }

    public boolean isMessageUrl(String message) {
        return URLUtil.isHttpUrl(message) || URLUtil.isHttpsUrl(message) || URLUtil.isDataUrl(message);
    }

    public void DiscussHandout(final Handout handout, final String message) {
        LoadCurrentStudentName(new OnStudentNameLoaded() {
            @Override
            public void StudentNameLoaded(String student_name) {
                final String handout_id = handout.getHandout_id();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;
                final DiscussMessage discussMessage = new DiscussMessage(currentUser.getUid()
                        , student_name, message, System.currentTimeMillis(), isMessageUrl(message));
                final DiscussData discussData = new DiscussData(handout.getHandout_title(), System.currentTimeMillis(), handout.getCover_url());
                LoadOnlineHandout(handout_id, new OnGetOnlineHandout() {
                    @Override
                    public void getOnlineHandout(final Handout handout) {
                        discussData.setHandout_url(handout.getCover_url());
                        LoadCurrentStudentImageUrl(new OnStudentImageLoaded() {
                            @Override
                            public void StudentImageLoaded(String student_image) {
                                discussMessage.setSender_url(student_image);
                                discussData.setLast_message(message);
                                discussData.setLast_sender_url(student_image);
                                discussion_data_ref.child(handout_id).setValue(discussData);
                                discussion_messages_ref.child(handout_id).push().setValue(discussMessage);
                            }

                            @Override
                            public void StudentImageLoadFailed(Object error) {
                            }
                        });
                    }
                });

            }

            @Override
            public void StudentNameLoadFailed(Object error) {

            }
        });
    }

    public void LoadHandoutDisscussList(final OnDiscussHandoutList onDiscussHandoutList) {
        firebaseDatabase.getReference().child("discussion data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String handout_id = dataSnapshot.getKey();
                Log.i(OPERATION_TAG, handout_id + " id is added");
                DiscussData handout_discuss_data = dataSnapshot.getValue(DiscussData.class);
                if (handout_discuss_data != null) {
                    Dialog dialog = new Dialog(handout_id, handout_discuss_data.getHandout_url(), handout_discuss_data.getHandout_title());
                    long time_stamp = handout_discuss_data.getTime_stamp();
                    User last_user = new User(String.valueOf(System.currentTimeMillis())
                            , "shittu", handout_discuss_data.getLast_sender_url());
                    ArrayList<User> users = new ArrayList<>();
                    users.add(last_user);
                    last_user.setId(String.valueOf(System.currentTimeMillis()));
                    users.add(last_user);
                    dialog.setUsers(users);
                    dialog.setLastMessage(new Message(String.valueOf(System.currentTimeMillis())
                            , handout_discuss_data.getLast_message(), last_user, new Date(time_stamp)));
                    Log.i(OPERATION_TAG, dialog.getDialogName() + " is added");
                    onDiscussHandoutList.onDiscussHandoutAdded(dialog);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int message_position;

    public void LoadHandoutMessages(Handout handout, final OnReadDisscussionMessage onReadDisscussionMessage) {
        discussion_messages_ref.child(handout.getHandout_id()).orderByChild("time_stamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final DiscussMessage discussMessage = dataSnapshot.getValue(DiscussMessage.class);
                if (discussMessage != null) {
                    final Message message = new Message(dataSnapshot.getKey(), discussMessage.getMessage()
                            , new User(discussMessage.getSender_id(), discussMessage.getSender_name(), discussMessage.getSender_url())
                            , new Date(discussMessage.getTime_stamp()));
//                    onReadDisscussionMessage.onDiscussionAdded(message,message_position);
                    LoadStudentImage(discussMessage.getSender_id(), true, new OnStudentImageLoaded() {
                        @Override
                        public void StudentImageLoaded(String student_image) {
                            User user = new User(discussMessage.getSender_id(), discussMessage.getSender_name(), student_image);
                            message.setUser(user);
                            onReadDisscussionMessage.onDiscussionAdded(message, message_position);

                            onReadDisscussionMessage.onDiscussionSenderImageAdded(user, message_position);
                        }

                        @Override
                        public void StudentImageLoadFailed(Object error) {

                        }
                    });
                }
                message_position++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadCourseCodeFilters(final String dept, final char level_first_char, final OnLoadCourseCode onLoadCourseCode) {
        course_code_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String code = dataSnapshot.getKey();
                if (code != null) {
                    String i_dept = code.split(" ")[0];
                    String i_code = code.split(" ")[1];
                    char i_year = i_code.charAt(0);
                    if (dept.equals(i_dept) && level_first_char == i_year) {
                        Log.i(OPERATION_TAG, code + " code added");
                        onLoadCourseCode.onCodeAdded(code);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadLevelFilters(final String dept, final OnLoadLevels onLoadLevels) {
        level_refrence.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (/*dataSnapshot.hasChild(fact) && */dataSnapshot.hasChild(dept)) {
                    String level = dataSnapshot.getKey();
                    Log.i(OPERATION_TAG, level + " level added");
                    onLoadLevels.onLevelAdded(level);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(dept)) {
                    String level = dataSnapshot.getKey();
                    Log.i(OPERATION_TAG, level + " level removed");
                    onLoadLevels.onLevelRemoved(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadDeptFilters(final String fact, final OnLoadDepartments onLoadDepartments) {
        Log.i(OPERATION_TAG, fact + " is clicked and about to load");
        dept_refrence.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(OPERATION_TAG, fact + " faculty to check");
                if (dataSnapshot.hasChild(fact)) {
                    String dept = dataSnapshot.getKey();
                    Log.i(OPERATION_TAG, dept + " department added");
                    onLoadDepartments.onDeptAdded(dept);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(fact)) {
                    String dept = dataSnapshot.getKey();
                    Log.i(OPERATION_TAG, dept + " department removed");
                    onLoadDepartments.onDeptRemoved(dept);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LoadFacultyFilter(final OnLoadFaculties onLoadFaculties) {
        fact_refrence.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String fact = dataSnapshot.getValue(String.class);
                Log.i(OPERATION_TAG, fact + " faculty added");
                onLoadFaculties.onFacultyAdded(fact);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String fact = dataSnapshot.getValue(String.class);
                onLoadFaculties.onFacultyRemoved(fact);
                Log.i(OPERATION_TAG, fact + " facuty removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface OnLoadFaculties {
        void onFacultyAdded(String fact);

        void onFacultyRemoved(String fact);
    }

    public interface OnLoadCourseCode {
        void onCodeAdded(String code);

        void onCodeRemoved(String code);
    }


    public interface OnLoadDepartments {
        void onDeptAdded(String dept);

        void onDeptRemoved(String dept);
    }

    public interface OnLoadLevels {
        void onLevelAdded(String level);

        void onLevelRemoved(String level);
    }

    public interface OnHandoutUpload {
        void onHandoutUploaded(Handout handout);

        void onHandoutUploadFailed(Handout handout, Exception exception);
    }

    public interface onLoadingHandoutInformation {
        void onHandoutAdded(Handout handout);

        void onHandoutChanged(Handout handout);

        void onHandoutRemoved(Handout handout);

        void onAllHandoutAdded(DataSnapshot handout);
    }

    public interface OnCompleteHandoutDownload {
        void onHandoutDownloaded(Handout handout, File dest_file);

        void onHandoutDownloadFailed(Object exception);

        void onHandoutDownloadStarted(Handout handout);

        void onHandoutDownloadProgress(Handout handout, double progress_byte, double total_byte);
    }

    public interface OnHandoutDeleteListener {
        void onHandoutDeleted(Handout handout, String deleted_file_location);

        void onHandoutDeletionFailed(Object exception);
    }

    public interface OnHandoutLikeListener {
        void onHandoutLiked(String handout_uid, String liker, boolean is_like);

        void onHandoutLikeFailed(Object exception);
    }

    public interface OnGetUserHandouts {
        void onGetUserHandouts(Handout handout);
    }

    public interface OnUserLikeListener {
        void onUserLiked(String handout_uid, boolean is_like);
    }

    public interface OnServerKeyRetrieved {
        void onServerKeyRetrieved(String key);
    }

    public interface OnUserTokenRetrieved {
        void onUserTokenRetrieved(String key);
    }

    public interface OnGetOnlineHandout {
        void getOnlineHandout(Handout handout);
    }

    public interface OnGetLikers {
        void onGetLiker(String handout_uid, Student liker);

        void onLikerRemoved(String handout_uid, Student liker);

        void onGetLikerFailed(Object exception);
    }

    public interface OnRequestRegistrationInfo {
        void onStudentInfoRetrieved(RegisteredStudent registeredStudent);

        void onStudentInfoRetrivalFailed(DatabaseError databaseError);

    }

    public interface OnStudentImageLoaded {
        void StudentImageLoaded(String student_image);

        void StudentImageLoadFailed(Object error);

    }

    public interface OnReadDisscussionMessage {
        void onDiscussionAdded(Message message, int message_position);

        void onDiscussionSenderImageAdded(User user, int message_position);
    }

    public interface OnDiscussHandoutList {
        void onDiscussHandoutAdded(Dialog dialog);

        void onDiscussHandoutUpdated(Dialog dialog);
    }

    public interface OnStudentNameLoaded {
        void StudentNameLoaded(String student_image);

        void StudentNameLoadFailed(Object error);

    }

    public interface OnHandoutFilterLoaded {
        void filterLoaded(String filter, String node);

        void filterLoadFailed(String filter, String node);
    }
}
