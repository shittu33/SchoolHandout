package com.example.abumuhsin.udusmini_library.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.firebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.firebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.FilterBarView;
import com.example.abumuhsin.udusmini_library.activities.DiscussionActivity;
import com.example.abumuhsin.udusmini_library.activities.OnlineBookDetailsActivity;
import com.example.abumuhsin.udusmini_library.activities.OnlineLocalHandoutListener;
import com.example.abumuhsin.udusmini_library.adapters.OnlineHandout_adapter;
import com.example.abumuhsin.udusmini_library.models.OnlineHandout;
import com.example.abumuhsin.udusmini_library.models.top_filter_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.COURSE_CODE_CAT_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.DEPARTMENT_CAT_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.FACULTY_CAT_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.LEVEL_CAT_NODE;
import static com.example.abumuhsin.udusmini_library.firebaseStuff.FirebaseHandoutOperation.OPERATION_TAG;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class OnlineBook_fragment extends Fragment implements OnlineHandout_adapter.OnBook_OptionsClickListener, FilterBarView.OnFilterItemClick {

    public static final String HANDOUT_EXTRA = "Handout_extra";
    private View view;
    private Student student;
    private GridView handout_grid_view;
    ProgressDialog progressDialog;
    ProgressBar h_load_prog_bar;
    OnlineHandout_adapter onlineHandout_adapter;
    //    ArrayAdapter<String> onlineHandout_adapter;
    private ArrayList<OnlineHandout> handouts = new ArrayList<>();
    private LinkedList<top_filter_model> first_level = new LinkedList<>();
    private FilterBarView filterBarView;
    private ArrayList<top_filter_model> second_level_list = new ArrayList<>();


    public OnlineBook_fragment() {
    }

    private void LoadHandoutsWithFilter(String filter, String alternative_filter, String fetched_node) {
        handouts.clear();
        firebaseHandoutOperation.LoadAllHandoutsWhere(filter, alternative_filter, fetched_node, new FirebaseHandoutOperation.onLoadingHandoutInformation() {
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
            public void onHandoutChanged(Handout handout) {

            }

            @Override
            public void onHandoutRemoved(Handout handout) {

            }

            @Override
            public void onAllHandoutAdded(DataSnapshot handout) {

            }
        });

    }

    public void onSerchQuery(String search_txt) {
        if (onlineHandout_adapter!= null) {
            Filter search_filter = onlineHandout_adapter.getFilter();
            search_filter.filter(search_txt);
        }
    }
    public void LoadSecondLevelFilters(String fetched_node, String clicked_text) {
        LoadHandoutsWithFilter(clicked_text, tmp_dept, fetched_node);
        String next_filter_column = "";
        switch (fetched_node) {
            case FACULTY_CAT_NODE:
                next_filter_column = DEPARTMENT_CAT_NODE;
                Log.i(OPERATION_TAG, clicked_text + " of department " + DEPARTMENT_CAT_NODE + " is added");
                firebaseHandoutOperation.LoadDeptFilters(clicked_text, new FirebaseHandoutOperation.OnLoadDepartments() {
                    @Override
                    public void onDeptAdded(String dept) {
                        top_filter_model next_filter = new top_filter_model(dept, DEPARTMENT_CAT_NODE);
                        if (!second_level_list.contains(next_filter)) {
                            second_level_list.add(next_filter);
                            filterBarView.ReloadSecondFilters(second_level_list);
                        }
                    }

                    @Override
                    public void onDeptRemoved(String dept) {

                    }
                });
                break;
            case DEPARTMENT_CAT_NODE:
                next_filter_column = LEVEL_CAT_NODE;
                tmp_dept = clicked_text;
                Log.i(OPERATION_TAG, clicked_text + " of Level " + LEVEL_CAT_NODE + " is added");
                firebaseHandoutOperation.LoadLevelFilters(clicked_text, new FirebaseHandoutOperation.OnLoadLevels() {
                    @Override
                    public void onLevelAdded(String level) {
                        top_filter_model next_filter = new top_filter_model(level, LEVEL_CAT_NODE);
//                        if (!second_level_list.contains(next_filter)) {
                        second_level_list.add(next_filter);
                        filterBarView.ReloadSecondFilters(second_level_list);
//                        }
                    }

                    @Override
                    public void onLevelRemoved(String level) {

                    }
                });
                break;
            case LEVEL_CAT_NODE:
                next_filter_column = COURSE_CODE_CAT_NODE;
                Log.i(OPERATION_TAG, clicked_text + " of code " + COURSE_CODE_CAT_NODE + " is added");
                firebaseHandoutOperation.LoadCourseCodeFilters(tmp_dept, clicked_text.charAt(0), new FirebaseHandoutOperation.OnLoadCourseCode() {
                    @Override
                    public void onCodeAdded(String code) {
                        top_filter_model next_filter = new top_filter_model(code, COURSE_CODE_CAT_NODE);
//                        if (!second_level_list.contains(next_filter)) {
                        second_level_list.add(next_filter);
                        filterBarView.ReloadSecondFilters(second_level_list);
//                        }
                    }

                    @Override
                    public void onCodeRemoved(String code) {

                    }
                });
                break;
            case COURSE_CODE_CAT_NODE:
        }
    }


    private void LoadSecondLevelOfFilterFromTop(String clicked_text, String fetched_node, LinkedList<top_filter_model> first_level) {
        second_level_list.clear();
        if (clicked_text.equals("Faculties")) {
            LoadHandoutWithFaculties();
            ListenForAddedHandout();
            return;
        }
        LoadSecondLevelFilters(fetched_node, clicked_text);
        filterBarView.setUpSecondFilterAdapter(second_level_list);
    }

    String tmp_dept;

    private void RemoveFromTopFilterList(top_filter_model top_filter_model) {
        int last_index = first_level.size();
        int data_raw_index = first_level.indexOf(top_filter_model) + 1;
        int data_index = data_raw_index < last_index ? data_raw_index : last_index;
        Log.i(OPERATION_TAG, "the size of data list is" + last_index);
        first_level.subList(data_index, last_index).clear();
        Log.i(OPERATION_TAG, "the range  of cleared data is " + data_index + "-" + last_index);

    }

    private void FillUpTopFilter(String clicked_text, String fetched_column) {
        top_filter_model top_filter_model = new top_filter_model(clicked_text, fetched_column);
        if (!first_level.contains(top_filter_model)) {
            first_level.add(top_filter_model);
        }
        Log.i(OPERATION_TAG, "The added column is " + fetched_column);
        Log.i(OPERATION_TAG, "The added filer is " + clicked_text);
        filterBarView.setUpTopFilterAdapter(first_level);
    }

    public void LoadHandoutWithFaculties() {
        firebaseHandoutOperation.LoadFacultyFilter(new FirebaseHandoutOperation.OnLoadFaculties() {
            @Override
            public void onFacultyAdded(String fact) {
                top_filter_model next_filter = new top_filter_model(fact, FACULTY_CAT_NODE);
                if (!second_level_list.contains(next_filter)) {
                    second_level_list.add(next_filter);
                    filterBarView.ReloadSecondFilters(second_level_list);
                }
            }

            @Override
            public void onFacultyRemoved(String fact) {

            }
        });

        FillUpTopFilter("Faculties", FACULTY_CAT_NODE);
        filterBarView.setUpSecondFilterAdapter(second_level_list);
    }


    @Override
    public void onTopFilterItemClicked(int position, String fetched_node, String clicked_text, LinkedList<top_filter_model> topList) {
        Log.i(OPERATION_TAG, "The clicked column is " + fetched_node);
        Log.i(OPERATION_TAG, "The clicked filter is " + clicked_text);
        int last_index = first_level.size() - 1;
        int data_raw_index = first_level.indexOf(new top_filter_model(clicked_text, fetched_node));
        if (data_raw_index < last_index) {
            RemoveFromTopFilterList(new top_filter_model(clicked_text, fetched_node));
            filterBarView.ReloadTopFilters(first_level);
            //Load handouts specific to this current filter
            LoadSecondLevelOfFilterFromTop(clicked_text, fetched_node, first_level);
        }
    }


    @Override
    public void onSecondFilterItemClicked(int position, String fetched_node, String clicked_text, final ArrayList<top_filter_model> secondList) {
        String next_filter_column = "";
        second_level_list.clear();
        LoadSecondLevelFilters(fetched_node, clicked_text);
        FillUpTopFilter(clicked_text, fetched_node);
        filterBarView.setUpSecondFilterAdapter(second_level_list);
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
        LoadHandoutWithFaculties();
    }

    private void initView(View view) {
        filterBarView = view.findViewById(R.id.filter_bar);
        h_load_prog_bar = view.findViewById(R.id.h_load_prog_bar);
        handout_grid_view = view.findViewById(R.id.online_grid_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handout_grid_view.setNestedScrollingEnabled(true);
        }
        onlineHandout_adapter = new OnlineHandout_adapter(this, handouts, this);
        handout_grid_view.setAdapter(onlineHandout_adapter);
        handout_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(OnlineBook_fragment.this.requireContext(), "this is cool inside", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OnlineBook_fragment.this.requireContext(), OnlineBookDetailsActivity.class);
                intent.putExtra(HANDOUT_EXTRA, onlineHandout_adapter.getItem(i));
                startActivity(intent);
            }
        });
        filterBarView.setOnFilterItemClick(this);
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
        if (handouts.size() < 1) {
            h_load_prog_bar.setVisibility(View.VISIBLE);
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

                @Override
                public void onAllHandoutAdded(DataSnapshot handout) {
                    h_load_prog_bar.setVisibility(View.GONE);
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
                Toast.makeText(OnlineBook_fragment.this.requireContext(), "File " + deleted_file_location + " has been deleted", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onHandoutDeletionFailed(Object exception) {
                if (exception instanceof DatabaseError) {
                    Toast.makeText(OnlineBook_fragment.this.requireContext(), ((DatabaseError) exception).getMessage(), Toast.LENGTH_SHORT).show();
                } else if (exception instanceof Exception) {
                    Toast.makeText(OnlineBook_fragment.this.requireContext(), ((Exception) exception).getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onCommentBook(int position, Handout handout) {
        Intent intent = new Intent(this.requireContext(), DiscussionActivity.class);
        intent.putExtra(HANDOUT_EXTRA, handout);
        startActivity(intent);
    }

    @Override
    public void onShareClick(int position, String book_name) {

    }

    @Override
    public void onDownloadBtnClick(int position, String book_name) {
        if (FirebaseLoginOperation.isUserLoggedIn()) {
            DownloadHandout(position);
        } else {
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
