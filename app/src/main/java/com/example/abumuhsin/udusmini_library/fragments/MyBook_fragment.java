package com.example.abumuhsin.udusmini_library.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.abumuhsin.udusmini_library.CoverSuplier;
import com.example.abumuhsin.udusmini_library.Enums.Cover_type;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.FirebaseHandoutOperation;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Handout;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Tables.BOOK_Table;
import com.example.abumuhsin.udusmini_library.Tables.HandoutFilterTable;
import com.example.abumuhsin.udusmini_library.Tables.PAGES_Table;
import com.example.abumuhsin.udusmini_library.Tables.PICTURES_Table;
import com.example.abumuhsin.udusmini_library.Tables.Picture_BucketTable;
import com.example.abumuhsin.udusmini_library.Views.FilterBarView;
import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.activities.OnlineBookDetailsActivity;
import com.example.abumuhsin.udusmini_library.activities.OnlineLocalHandoutListener;
import com.example.abumuhsin.udusmini_library.adapters.Grid_adapter;
import com.example.abumuhsin.udusmini_library.models.HandoutFilter;
import com.example.abumuhsin.udusmini_library.models.LocalHandout;
import com.example.abumuhsin.udusmini_library.models.top_filter_model;
import com.example.abumuhsin.udusmini_library.tasks.Convert_to_pdfTask;
import com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask;
import com.example.abumuhsin.udusmini_library.tasks.ZippingDireectoryTask;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.Fragment_Utils;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.amazing_picker.utilities.View_Utils;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Book_table_model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.CoverSuplier.getAbrvFromCourseCode;
import static com.example.abumuhsin.udusmini_library.CoverSuplier.getCourseCodeNumber;
import static com.example.abumuhsin.udusmini_library.CoverSuplier.getCoverOfType;
import static com.example.abumuhsin.udusmini_library.CoverSuplier.getLevelFromCode;
import static com.example.abumuhsin.udusmini_library.FirebaseStuff.FirebaseHandoutOperation.OPERATION_TAG;
import static com.example.abumuhsin.udusmini_library.utils.Constants.COURSE_CODE;
import static com.example.abumuhsin.udusmini_library.utils.Constants.DEPARTMENT;
import static com.example.abumuhsin.udusmini_library.utils.Constants.LEVEL;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getBookFilesPath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getBooksFilesPath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getPdfsFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getUploadFilePath;
import static com.example.abumuhsin.udusmini_library.utils.View_Utils.Select_View;


/**
 * Created by Abu Muhsin on 31/05/2018.
 */

public class MyBook_fragment extends androidx.fragment.app.Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.MultiChoiceModeListener, Grid_adapter.OnBook_OptionsClickListener, View.OnClickListener
        , OnlineLocalHandoutListener, FilterBarView.OnFilterItemClick {
    public static final String COVER_DEBUG = "cover_debug";
    public static final String BOOK_NAME = "book_name";
    public static final String BOOK_COVER = "book_cover";
    private ArrayList<LocalHandout> pics_title_list;
    private ArrayList<LocalHandout> update_list;
    private ArrayList<String> titles;
    private GridView book_gridView;
    private FloatingActionButton add_btn;
    private Grid_adapter grid_adapter;
    private ActionMode mActionMode;
    private FilterBarView filterBarView;


    private static DatabaseCreator udus_handout_database;
    public static BOOK_Table book_table;
    public static PAGES_Table page_table;
    public static PICTURES_Table pic_table;
    public static Picture_BucketTable picture_bucketTable;
    private View view;
    public static final String BOOKSDEBUG = "BooksDebug";
    private boolean is_sharing_withPdf;
    private boolean is_sharing_withBook;
    private String book_name;
    private String zip_path;
    private Uri zip_uri;
    private Student student;
    private HandoutFilterTable handoutFilterTable;

    private LinkedList<top_filter_model> first_level = new LinkedList<>();
    private ArrayList<top_filter_model> second_level_list = new ArrayList<>();

    public ArrayList<LocalHandout> getPics_title_list() {
        return pics_title_list;
    }

    public Grid_adapter getGrid_adapter() {
        return grid_adapter;
    }

    public MyBook_fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_book_layout, container, false);
        init();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    public void LoadRecentBooks() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        FirebaseLoginOperation.get(this.requireActivity());
        if (arguments != null) {
            zip_uri = arguments.getParcelable("zip_uri");
            if (zip_uri != null) {
                Log.i("active", "uri is not null");
                AddZipBook(zip_uri);
            }
        }
    }

    public void onSerchQuery(String search_txt){
        if (grid_adapter!=null) {
            Filter search_filter = grid_adapter.getFilter();
            search_filter.filter(search_txt);
        }
    }

    @Override
    public void onHandoutDownloadFromOnline(Handout handout, File dest_file) {
        AddZipBook(Uri.fromFile(dest_file));
    }

    public void AddZipBook(Uri zip_uri) {
        try {
            File dest_directory = new File(getBooksFilesPath());
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(zip_uri);
            Log.i("active", "InputStream was generated from uri of path " + zip_uri.getPath());
//            FileUtils.unzipStream(inputStream,dest_directory);
//            String book_name = zip_uri.getPath().split("/")[zip_uri.getPath().split("/").length-1];
            new UnZippingToBookTask(this, dest_directory, inputStream, false).execute();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void add_A_Book() {
        final Activity activity_context = getActivity();
        if (activity_context != null) {
            View view = LayoutInflater.from(this.requireContext()).inflate(R.layout.add_book_dialog, null);
            title_edit = view.findViewById(R.id.edt_title);
            course_code_edit = view.findViewById(R.id.edit_course_code);
            final View cancel_btn = view.findViewById(R.id.cancle_btn);
            final View save_btn = view.findViewById(R.id.save_btn);
            save_btn.setOnClickListener(this);
            cancel_btn.setOnClickListener(this);
            dialog = new AlertDialog.Builder(activity_context/*,R.style.Theme_AppCompat_Light_Dialog_Alert*/).setTitle("New Book")
                    .setView(view).show();
        } else {
            Log.i(BOOKSDEBUG, "getActivity() is null");
        }
    }

    EditText title_edit;
    EditText course_code_edit;
    String new_book_title;
    String new_course_code;
    Dialog dialog;

    void getNewBookDialogTexts() {
        new_book_title = title_edit.getText().toString().trim();
        new_course_code = course_code_edit.getText().toString().trim().toUpperCase();
    }

    boolean isDialogFormFilled() {
        return !TextUtils.isEmpty(new_book_title) && !TextUtils.isEmpty(new_course_code);
    }

    String course_code;

    public String getCourse_code() {
        return course_code;
    }

    public boolean isCourseCodeValid(String course_code) {
        if (course_code.length() <= 8) {
            String code = "";
            String abrv = "";
            if (course_code.length() == 6) {
                abrv = course_code.substring(0, 3);
                code = course_code.substring(3, 6);
            } else if (course_code.length() == 7) {
                if (course_code.contains(" ")) {
                    abrv = course_code.split(" ")[0];
                    code = course_code.split(" ")[1];
                } else {
                    abrv = course_code.substring(0, 3);
                    code = course_code.substring(3, 7);
                }
            } else if (course_code.length() == 8 && course_code.contains(" ")) {
                abrv = course_code.split(" ")[0];
                code = course_code.split(" ")[1];
            }
            this.course_code = abrv + " " + code;
            boolean is_just_six = (abrv + code).length() == 6;
            boolean is_just_seven = (abrv + code).length() == 7;
            boolean is_code_digit = Character.isDigit(code.charAt(0));
            return (is_just_six || is_just_seven) && is_code_digit;
        }
        return false;
    }

    Cover_type current_added_cover_type;

    @Override
    public void onClick(View view) {
        getNewBookDialogTexts();
        switch (view.getId()) {
            case R.id.save_btn:
                if (isDialogFormFilled()) {
                    if (isCourseCodeValid(new_course_code)) {
                        new_course_code = course_code;
                        if (!book_table.is_book_exist(new_book_title)) {
                            saveCoverPage(new_book_title, new_course_code, new OnCoverConverted() {
                                @Override
                                public void coverConverted(File cover_file) {
                                    current_added_cover_type = CoverSuplier.getType();
                                    add_A_new_BookThread(new_book_title, new_course_code, cover_file.getPath()).start();
                                }
                            });
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(this.requireActivity(), "A Book of this name is already_exit", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this.requireActivity(), "You have entered an invalid course code it should be something like (Cmp 303)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "one of the fields are empty,please fill them and try again", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cancle_btn:
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
        }
    }

    private void saveCoverPage(final String book_name, String new_course_code, final OnCoverConverted onCoverConverted) {
        int dest_width = View_Utils.getScreenResolution(requireContext()).width;
        int dest_height = View_Utils.getScreenResolution(requireContext()).height;
        final View v = getCoverOfType(requireActivity(), book_name, new_course_code);
        if (v != null) {
            v.setLayoutParams(new ViewGroup.LayoutParams(dest_width, dest_height));
            v.layout(0, 0, dest_width, dest_height);
        }
        final Thread screenshot_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (v != null) {
                    bitmap = FileUtils.takeScreenshot(v, MyBook_fragment.this.requireContext());
                }
                if (bitmap != null) {
                    final File dest_file = new File(FileUtils.getBookCoverPath(book_name));
                    FileUtils.saveImage(bitmap, dest_file, 80);
                    MyBook_fragment.this.requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCoverConverted.coverConverted(dest_file);
                            Log.i(COVER_DEBUG, "cover path is " + dest_file.getPath());
                        }
                    });
                } else {
                    Log.i(COVER_DEBUG, "bitmap is null");
                }

            }
        });
        screenshot_thread.start();
    }


    public FirebaseHandoutOperation getHandoutOperation() {
        return handoutOperation;
    }

    private FirebaseHandoutOperation handoutOperation;

    private void init() {
        initViews();
        initHandoutDatabase();
        initAdapters();
        handoutOperation = new FirebaseHandoutOperation(this.getContext());

    }

    private void initHandoutDatabase() {
        udus_handout_database = new DatabaseCreator(getContext(), FileUtils.getDatabasePath("Handoutdb.Sqlite"), null, 1);
        book_table = new BOOK_Table(udus_handout_database);
        page_table = new PAGES_Table(udus_handout_database);
        pic_table = new PICTURES_Table(udus_handout_database);
        picture_bucketTable = new Picture_BucketTable(udus_handout_database);
        handoutFilterTable = new HandoutFilterTable(udus_handout_database);

        Log.i(BOOKSDEBUG, "initHandoutDatabase: ends");
    }

    public PICTURES_Table getPic_table() {
        return pic_table;
    }

    private void initViews() {
        filterBarView = view.findViewById(R.id.filter_bar);
        book_gridView = view.findViewById(R.id.books_grid_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            book_gridView.setNestedScrollingEnabled(true);
        }
        book_gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        book_gridView.setMultiChoiceModeListener(this);
    }

    private void initAdapters() {
        update_list = new ArrayList<>();
        pics_title_list = new ArrayList<>();
        grid_adapter = new Grid_adapter(this, pics_title_list, this);
        filterBarView.setOnFilterItemClick(this);
//        filterBarView.setUpFilterAdapters();
        book_gridView.setAdapter(grid_adapter);
        book_gridView.setOnItemClickListener(this);
        book_gridView.setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        book_table.LoadBookInfo(this);

    }

    private boolean is_click_forActionMode = false;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this.requireContext(), "clicked", Toast.LENGTH_SHORT).show();
        if (is_click_forActionMode) {
            if (!book_gridView.isItemChecked(position)) {
                book_gridView.setItemChecked(position, true);
            } else {
                book_gridView.setItemChecked(position, false);
            }
        } else {
            Intent intent = new Intent(getActivity(), FlipBooKActivity.class);
            Log.i(BOOKSDEBUG, "b4 bundles");
            intent.putExtra(BOOK_NAME, grid_adapter.getItem(position).getTitle());
            intent.putExtra(BOOK_COVER, grid_adapter.getItem(position).getCover());
            startActivity(intent);
            Log.i(BOOKSDEBUG, "activity started");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this.requireContext(), "clicked", Toast.LENGTH_SHORT).show();
        if (mActionMode != null) {
            return false;
        }
        mActionMode = requireActivity().startActionMode(this);
        is_click_forActionMode = true;
        book_gridView.setItemChecked(position, true);
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            checkTheView(position);
            update_list.add(grid_adapter.getItem(position));
        } else {
            UncheckTheView(position);
            update_list.remove(grid_adapter.getItem(position));
        }
        if (update_list.size() > 1) {
            mode.getMenu().findItem(R.id.open_with_pdf).setVisible(false);
        } else {
            mode.getMenu().findItem(R.id.open_with_pdf).setVisible(true);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        requireActivity().getMenuInflater().inflate(R.menu.option_menus, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                DeleteSelectedItems();
                mode.finish();
                break;
            case R.id.share:

                break;
            case R.id.upload:
                Toast.makeText(this.getContext(), "testing", Toast.LENGTH_SHORT).show();
                mode.finish();
                break;
            case R.id.open_with_pdf:
                int position = pics_title_list.indexOf(update_list.get(0));
                String book_name = update_list.get(0).getTitle();
                grid_adapter.getOnBook_optionsClickListener().onOpenWith(position, book_name);
                mode.finish();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        DeselectCheckedViews(update_list);
        mActionMode = null;
        is_click_forActionMode = false;
    }

    public Thread add_A_new_BookThread(final String book_name, final String new_course_code, final String book_cover_path) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(OPERATION_TAG, "course code is " + new_course_code);
                String book_type = String.valueOf(CoverSuplier.getIntTypeFrom_Enum(current_added_cover_type));
                String department = getAbrvFromCourseCode(new_course_code);
                Log.i(OPERATION_TAG, "saved department is " + department);
                String course_code = getCourseCodeNumber(new_course_code);
                Log.i(OPERATION_TAG, "saved course code number is " + course_code);
                //                String course_code = new_course_code.split(" ")[1];
                String level = getLevelFromCode(course_code);
                Log.i(OPERATION_TAG, "saved level number is " + level);
                HandoutFilter handoutFilter = new HandoutFilter(department, level, course_code, book_name);
                book_table.Create_a_new_Book(new Book_table_model(book_name, new_course_code
                        , book_type, 0, book_cover_path), handoutFilter);
                final LocalHandout new_book_model = book_table.getLastBookInfo();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pics_title_list.add(new_book_model);
                        grid_adapter.notifyDataSetChanged();
                        book_gridView.setSelection(pics_title_list.indexOf(new_book_model));
                    }
                });
            }
        });
    }

    private void DeleteSelectedItems() {
        for (int i = 0; i < update_list.size(); i++) {
            String book_title = update_list.get(i).getTitle();
            new App_Preferences(this.requireContext()).clearBookSettings(book_title);
            book_table.DeleteBook(this, book_title);
            pics_title_list.remove(update_list.get(i));
            onBookDeleted(book_title);
        }
        update_list.clear();
        grid_adapter.notifyDataSetChanged();
    }

    public void Delete_a_Book(int index) {
        String book_title = pics_title_list.get(index).getTitle();
        new App_Preferences(this.requireContext()).clearBookSettings(book_title);
        book_table.DeleteBook(this, book_title);
        pics_title_list.remove(pics_title_list.get(index));
        grid_adapter.notifyDataSetChanged();
        onBookDeleted(book_title);
    }

    public void checkTheView(int position) {
        Select_View(position, true, pics_title_list);
        grid_adapter.notifyDataSetChanged();
    }

    public void UncheckTheView(int position) {
        Select_View(position, false, pics_title_list);
        grid_adapter.notifyDataSetChanged();

    }

    public void DeselectCheckedViews(ArrayList<LocalHandout> update_list) {
        for (int i = 0; i < update_list.size(); i++) {
            Select_View(i, false, update_list);
        }
        update_list.clear();
        grid_adapter.notifyDataSetChanged();
    }

    private void saveState() {
        new Fragment_Utils().saveState(this, "yes");
    }

    private void restoreState() {
        new Fragment_Utils().RestoreState(getFragmentManager(), this, "yes");
    }

    @Override
    public void onDeleteClick(final int position, String book_name) {
        new AlertDialog.Builder(this.requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage("Are you sure you want to delete this book and all of it pages?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Delete_a_Book(position);
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private int no_of_pages;

    private String book_clicked;

    public int getNo_of_pages() {
        return no_of_pages;
    }

    public String getBook_clicked() {
        return book_clicked;
    }


    //        book_clicked = book_name;
    @Override
    public void onOpenWith(int position, String book_name) {
        is_opening_with = true;
        final File pdf_file = new File(getPdfsFilePath() + File.separator + book_name + ".pdf");
        no_of_pages = pics_title_list.get(position).getPage_no();
        book_clicked = book_name;
        if (pdf_file.exists()) {
            PdfUtils.play_pdf(this.requireActivity(), pdf_file);
        } else {
            new Convert_to_pdfTask(null, this, pdf_file).execute();
        }
    }


    @Override
    public void onUploadClick(int position, String book_name) {
        if (FirebaseLoginOperation.isUserLoggedIn()) {
            exportBook(position, book_name, false);
        } else {
            Toast.makeText(this.getContext(), "you have to login first before you can upload handout", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onShareBook(int position, String book_name) {
        exportBook(position, book_name, true);
    }

    public GridView getBook_gridView() {
        return book_gridView;
    }

    private void exportBook(int position, String book_name, boolean is_for_share) {
        book_clicked = book_name;
        no_of_pages = pics_title_list.get(position).getPage_no();
        File dir_to_upload = new File(getBookFilesPath(book_name));
        File dest_file = new File(getUploadFilePath() + File.separator + book_name + "_share.zip");
        new ZippingDireectoryTask(this, dir_to_upload, dest_file, position, is_for_share).execute();
    }

    @Override
    public void onSharePdf(int position, String book_name) {
        is_sharing_withPdf = true;
        final File pdf_file = new File(getPdfsFilePath() + File.separator + book_name + ".pdf");
        no_of_pages = pics_title_list.get(position).getPage_no();
        book_clicked = book_name;
        if (pdf_file.exists()) {
            PdfUtils.share_pdf(this.requireActivity(), pdf_file);
        } else {
            new Convert_to_pdfTask(null, this, pdf_file).execute();
        }
    }

    public void onBookDeleted(String book_name) {
        final File pdf_file = new File(getPdfsFilePath() + File.separator + book_name + ".pdf");
        if (pdf_file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pdf_file.delete();
        }
    }

    @Override
    public void onShareClick(int position, String book_name) {
        Intent intent = new Intent(MyBook_fragment.this.requireContext(), OnlineBookDetailsActivity.class);
        String book = grid_adapter.getItem(position).getTitle();
        intent.putExtra(OnlineBookDetailsActivity.ONLINE_BOOK_EXTRA, book);
        startActivity(intent);
    }




    //    @Override
    private boolean is_opening_with;

    //    }
    public boolean is_opening_with() {
        return is_opening_with;
    }

    ////        Toast.makeText(this.getContext(), "i will convert to pdf", Toast.LENGTH_SHORT).show();
    public boolean is_sharing_withPdf() {
        return is_sharing_withPdf;
    }

    //        new Convert_to_pdfTask(null, this, pdf_file).execute();
    public boolean is_sharing_withBook() {
        return is_sharing_withBook;
    }

    OnCoverConverted onCoverConverted;

    public OnCoverConverted getOnCoverConverted() {
        return onCoverConverted;
    }

    public void setOnCoverConverted(OnCoverConverted onCoverConverted) {
        this.onCoverConverted = onCoverConverted;
    }


    public void RemoveFromTopFilterList(top_filter_model top_filter_model) {
        int last_index = first_level.size();
        int data_raw_index = first_level.indexOf(top_filter_model) + 1;
        int data_index = data_raw_index < last_index ? data_raw_index : last_index;
        Log.i(OPERATION_TAG, "the size of data list is" + last_index);
        first_level.subList(data_index, last_index).clear();
        Log.i(OPERATION_TAG, "the range  of cleared data is " + data_index + "-" + last_index);
    }

    public void LoadSecondLevelOfFilter(String selected_text, String column, LinkedList<top_filter_model> topList) {
        second_level_list.clear();
        if (selected_text.equals("Dept")) {
            LoadBooksWithDepartment();
            return;
        }
        book_table.LoadHandoutWithFilter(MyBook_fragment.this, selected_text, column, topList);
        String next_filter_column = "";
        switch (column) {
            case DEPARTMENT:
                next_filter_column = LEVEL;
                break;
            case LEVEL:
                next_filter_column = COURSE_CODE;
                break;
            case COURSE_CODE:
//                    book_table.LoadHandoutWithFilter(MyBook_fragment.this, clicked_text, fetched_column);
//                    FillUpTopFilter(clicked_text, fetched_column);
//                    second_level_list.clear();
//                    filterBarView.setUpSecondFilterAdapter(second_level_list, next_filter_column);
                return;
        }
//        String whereStatement = Table.get_Equal_Sign_Statement(column, selected_text);
        String whereStatement = book_table.getWhereFromFilterList(topList);
        Log.i(OPERATION_TAG, "the where statement afor the click  is " + whereStatement);
        Log.i(OPERATION_TAG, "the fetched column at the top is " + next_filter_column);
        final String finalNext_filter_column = next_filter_column;
        handoutFilterTable.FetchFilterWhere(next_filter_column, whereStatement, new HandoutFilterTable.OnFilterFetch() {
            @Override
            public void onEachFilterRead(String filter) {
                top_filter_model top_filter_model = new top_filter_model(filter, finalNext_filter_column);
                if (!second_level_list.contains(top_filter_model)) {
                    second_level_list.add(top_filter_model);
                    Log.i(OPERATION_TAG, filter);
                }
            }
        });
        filterBarView.setUpSecondFilterAdapter(second_level_list);
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

    public void LoadBooksWithDepartment() {
//        WHERE column LIKE '%XXXX%'
        handoutFilterTable.FetchFilter(DEPARTMENT, new HandoutFilterTable.OnFilterFetch() {
            @Override
            public void onEachFilterRead(String filter) {
                top_filter_model second_filter_obj = new top_filter_model(filter, DEPARTMENT);
                if (!second_level_list.contains(second_filter_obj)) {
                    second_level_list.add(second_filter_obj);
                    Log.i(OPERATION_TAG, filter);
                }
            }
        });
        FillUpTopFilter("Dept", DEPARTMENT);
        filterBarView.setUpSecondFilterAdapter(second_level_list);
    }

    @Override
    public void onTopFilterItemClicked(int position, String fetched_column, String clicked_text, LinkedList<top_filter_model> topList) {
        Log.i(OPERATION_TAG, "The clicked column is " + fetched_column);
        Log.i(OPERATION_TAG, "The clicked filter is " + clicked_text);
        int last_index = first_level.size() - 1;
        int data_raw_index = first_level.indexOf(new top_filter_model(clicked_text, fetched_column));
        if (data_raw_index < last_index) {
            RemoveFromTopFilterList(new top_filter_model(clicked_text, fetched_column));
            filterBarView.ReloadTopFilters(first_level);
            LoadSecondLevelOfFilter(clicked_text, fetched_column, first_level);
        }
    }

    @Override
    public void onSecondFilterItemClicked(int position, String fetched_column, String clicked_text, ArrayList<top_filter_model> secondList) {
        String next_filter_column = "";
        switch (fetched_column) {
            case DEPARTMENT:
                next_filter_column = LEVEL;
                break;
            case LEVEL:
                next_filter_column = COURSE_CODE;
                break;
            case COURSE_CODE:
                FillUpTopFilter(clicked_text, fetched_column);
                book_table.LoadHandoutWithFilter(MyBook_fragment.this, clicked_text, fetched_column, first_level);
                second_level_list.clear();
                filterBarView.setUpSecondFilterAdapter(second_level_list);
                return;
        }
        FillUpTopFilter(clicked_text, fetched_column);
        book_table.LoadHandoutWithFilter(MyBook_fragment.this, clicked_text, fetched_column, first_level);
        second_level_list.clear();
        String whereStatement = book_table.getWhereFromFilterList(first_level);
        Log.i(OPERATION_TAG, "where statement onClick is " + whereStatement);
        final String finalNext_filter_column = next_filter_column;
        handoutFilterTable.FetchFilterWhere(next_filter_column, whereStatement, new HandoutFilterTable.OnFilterFetch() {
            @Override
            public void onEachFilterRead(String filter) {
                top_filter_model second_filer_obj = new top_filter_model(filter, finalNext_filter_column);
                if (!second_level_list.contains(second_filer_obj)) {
                    second_level_list.add(second_filer_obj);
                    Log.i(OPERATION_TAG, filter);
                }
            }
        });
        filterBarView.setUpSecondFilterAdapter(second_level_list);
    }


    public interface OnCoverConverted {
        void coverConverted(File cover_path);
    }
}
