package com.example.abumuhsin.udusmini_library.FirebaseStuff;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.RegisteredStudent;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Student;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class FirebaseHandoutOperation {
    public static final String OPERATION_TAG = "handout_opera_tag";
    private Context context;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    public FirebaseHandoutOperation(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
    }

    public void LoadCurrentStudentImage(final OnStudentImageLoaded onStudentImageLoaded) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            LoadStudentImage(currentUser.getUid(), onStudentImageLoaded);
        }
    }
    public void LoadCurrentStudentName(final OnStudentNameLoaded onStudentNameLoaded) {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            LoadStudentName(currentUser.getUid(), onStudentNameLoaded);
        }
    }

    public void LoadStudentImage(final String uid, final OnStudentImageLoaded onStudentImageLoaded) {
        databaseReference.child("Students Info").child(uid).child("student_image_path").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                StorageReference student_image_bucket_file = null;
                if (url != null) {
                    student_image_bucket_file = firebaseStorage.getReferenceFromUrl(url);
                    final File dest_file = new File(FileUtils.getOnlineProfileFile(uid));
                    if (!dest_file.exists()) {
                        student_image_bucket_file.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                onStudentImageLoaded.StudentImageLoaded(dest_file.getPath());
                            }
                        });
                    }else {
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
                                                        final DatabaseReference handouts = databaseReference.child("Handouts").push();
                                                        //Finally save handout information
                                                        handouts.setValue(handout)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        handouts.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void LoadAllHandouts(final onLoadingHandoutInformation onLoadingHandoutInformation) {
        DatabaseReference handouts_reference = firebaseDatabase.getReference().child("Handouts");
        handouts_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Handout handout = dataSnapshot.getValue(Handout.class);
                if (handout != null) {
                    handout.setHandout_id(dataSnapshot.getKey());
                    final StorageReference cover_bucket_reference = firebaseStorage.getReferenceFromUrl(handout.getCover_url());
                    final File dest_file;
//                    try {
//                        dest_file = File.createTempFile("cover", "png");
                    dest_file = new File(FileUtils.getOnlineHandoutFile(handout.getHandout_id()));
                    if (!dest_file.exists()) {
                        cover_bucket_reference.getFile(dest_file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                handout.setCover_url(dest_file.getPath());
                                String poster_adm_no = handout.getPoster();
                                databaseReference.child("registered student").child(poster_adm_no).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        handout.setCover_url(dest_file.getPath());
                        String poster_adm_no = handout.getPoster();
                        databaseReference.child("registered student").child(poster_adm_no).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                    }
//                    } catch (IOException e) {
//                        Log.i(OPERATION_TAG, "something happen to the file");
//                        e.printStackTrace();
//                    }
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
                if (uid != null) {
                    firebaseDatabase.getReference().child("Students Info").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Student liker = dataSnapshot.getValue(Student.class);
                            if (liker != null) {
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

    public interface OnHandoutUpload {
        void onHandoutUploaded(Handout handout);

        void onHandoutUploadFailed(Handout handout, Exception exception);
    }

    public interface onLoadingHandoutInformation {
        void onHandoutAdded(Handout handout);

        void onHandoutChanged(Handout handout);

        void onHandoutRemoved(Handout handout);
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

    public interface OnGetNumberOfLikers {
        void ongetNumberOfLikers(Handout handout, long no_of_likers);
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
    public interface OnStudentNameLoaded {
        void StudentNameLoaded(String student_image);

        void StudentNameLoadFailed(Object error);

    }
}
