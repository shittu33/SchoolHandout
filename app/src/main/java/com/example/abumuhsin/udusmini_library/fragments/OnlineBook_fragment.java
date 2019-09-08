package com.example.abumuhsin.udusmini_library.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.activities.OnlineBookDetailsActivity;
import com.example.abumuhsin.udusmini_library.activities.OnlineLocalHandoutListener;
import com.example.abumuhsin.udusmini_library.adapters.OnlineHandout_adapter;
import com.example.abumuhsin.udusmini_library.models.OnlineHandout;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class OnlineBook_fragment extends Fragment implements OnlineHandout_adapter.OnBook_OptionsClickListener {

    public static final String HANDOUT_EXTRA = "Handout_extra";
    private View view;
    private Student student;
    private GridView handout_grid_view;
    ProgressDialog progressDialog;
    OnlineHandout_adapter onlineHandout_adapter;
//    ArrayAdapter<String> onlineHandout_adapter;
private ArrayList<OnlineHandout> handouts = new ArrayList<>();


    public OnlineBook_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.online_book_layout, container, false);
        initView(view);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ListenForAddedHandout();
    }
    private void initView(View view) {
        handout_grid_view = view.findViewById(R.id.online_grid_view);
        onlineHandout_adapter = new OnlineHandout_adapter(this,handouts, this);
        handout_grid_view.setAdapter(onlineHandout_adapter);
        handout_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(OnlineBook_fragment.this.requireContext(), "this is cool inside", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OnlineBook_fragment.this.requireContext(), OnlineBookDetailsActivity.class);
                intent.putExtra(HANDOUT_EXTRA,onlineHandout_adapter.getItem(i));
                startActivity(intent);
            }
        });
    }

    private void DownloadHandout(int i) {
        Handout handout = onlineHandout_adapter.getItem(i);
        progressDialog = new ProgressDialog(OnlineBook_fragment.this.requireContext());
        progressDialog.setTitle("Handout Download");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        firebaseHandoutOperation.DownloadHandout(handout, new FirebaseHandoutOperation.OnCompleteHandoutDownload() {
            @Override
            public void onHandoutDownloaded(Handout handout, File dest_file) {
                progressDialog.dismiss();
                onlineLocalHandoutListener.onHandoutDownloadFromOnline(handout, dest_file);
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
                    Toast.makeText(OnlineBook_fragment.this.requireContext(), ((DatabaseError) error_obj).getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error_obj instanceof Exception) {
                    Toast.makeText(OnlineBook_fragment.this.requireContext(), ((Exception) error_obj).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private FirebaseHandoutOperation firebaseHandoutOperation;
    private OnlineLocalHandoutListener onlineLocalHandoutListener;
    public void ListenForAddedHandout() {
        if (handouts.size()<1) {
            firebaseHandoutOperation = new FirebaseHandoutOperation(requireContext());
            firebaseHandoutOperation.LoadAllHandouts(new FirebaseHandoutOperation.onLoadingHandoutInformation() {
                @Override
                public void onHandoutAdded(final Handout handout) {
                    firebaseHandoutOperation.checkIfCurrentUserLikeHandout(handout.getHandout_id(), new FirebaseHandoutOperation.OnUserLikeListener() {
                        @Override
                        public void onUserLiked(String handout_uid, boolean is_like) {
                            if (is_like) {
                                OnlineHandout onlineHandout = new OnlineHandout(handout, true);
                                if (!handouts.contains(onlineHandout)) {
                                    handouts.add(onlineHandout);
                                }
                            } else {
                                OnlineHandout onlineHandout = new OnlineHandout(handout, false);
                                if (!handouts.contains(onlineHandout)) {
                                    handouts.add(onlineHandout);
                                }
                            }
                            onlineHandout_adapter.notifyDataSetChanged();
                        }
                    });
                    Log.i(MyBook_fragment.BOOKSDEBUG, "one handout is added");
                }

                @Override
                public void onHandoutChanged(final Handout handout) {

                    firebaseHandoutOperation.checkIfCurrentUserLikeHandout(handout.getHandout_id(), new FirebaseHandoutOperation.OnUserLikeListener() {
                        @Override
                        public void onUserLiked(String handout_uid, boolean is_like) {
                            final int index_of_changed_handout = getHandoutIndex(new OnlineHandout(handout, false), handouts);
                            if (is_like) {
                                handouts.set(index_of_changed_handout, new OnlineHandout(handout, true));
                            } else {
                                handouts.set(index_of_changed_handout, new OnlineHandout(handout, false));
                            }
                            Log.i(MyBook_fragment.BOOKSDEBUG, "Changed handout index is " + index_of_changed_handout);
                            onlineHandout_adapter.notifyDataSetChanged();
                        }
                    });
                    //adjust handout details...
                }

                @Override
                public void onHandoutRemoved(Handout handout) {
                    handouts.remove(getHandoutIndex(new OnlineHandout(handout, false), handouts));
                    onlineHandout_adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public int getHandoutIndex(OnlineHandout onlineHandout, ArrayList<OnlineHandout> onlineHandouts) {
        for (int i = 0; i < onlineHandouts.size(); i++) {
            String list_handout_id = handouts.get(i).getHandout().getHandout_id();
            String handout_id = onlineHandout.getHandout().getHandout_id();
            if (list_handout_id.equals(handout_id)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FirebaseLoginOperation.get(this.requireActivity());
        firebaseHandoutOperation = new FirebaseHandoutOperation(this.getContext());
        FirebaseLoginOperation.get(this.requireActivity());
        // if hosting Activity doesn't implement OnlineLocalHandoutListener interface then throw error
        if (context instanceof OnlineLocalHandoutListener) {
            onlineLocalHandoutListener = (OnlineLocalHandoutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onlineLocalHandoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onlineLocalHandoutListener = null;
    }

    public void Test() {
        Toast.makeText(this.getContext(), "on upload book online!!!", Toast.LENGTH_SHORT).show();
    }

    private Handout getAdapterHandout(int i) {
        return onlineHandout_adapter.getItem(i);
    }

    @Override
    public void onDeleteClick(int position, String book_name) {
        Handout handout = getAdapterHandout(position);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("deleting " + book_name);
        progressDialog.show();
        firebaseHandoutOperation.DeleteHandout(handout, new FirebaseHandoutOperation.OnHandoutDeleteListener() {
            @Override
            public void onHandoutDeleted(Handout handout, String deleted_file_location) {
                Toast.makeText(OnlineBook_fragment.this.requireContext(),"File " + deleted_file_location+ " has been deleted", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            @Override
            public void onHandoutDeletionFailed(Object exception) {
                if (exception instanceof DatabaseError) {
                    Toast.makeText(OnlineBook_fragment.this.requireContext(),((DatabaseError) exception).getMessage(), Toast.LENGTH_SHORT).show();
                }else if (exception instanceof Exception){
                    Toast.makeText(OnlineBook_fragment.this.requireContext(),((Exception) exception).getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onShareClick(int position, String book_name) {

    }

    @Override
    public void onDownloadBtnClick(int position, String book_name) {
        if (FirebaseLoginOperation.isUserLoggedIn()) {
            DownloadHandout(position);
        }else {
            Toast.makeText(OnlineBook_fragment.this.requireContext(), "user is currently offline", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOpenWith(int position, String book_name) {
    }

    @Override
    public void onSharePdf(int position, String book_name) {

    }

    @Override
    public void onShareBook(int position, String book_name) {

    }
}
