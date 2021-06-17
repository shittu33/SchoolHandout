
package com.example.abumuhsin.udusmini_library.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.burhanrashid52.imageeditor.EditImageActivity;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Tables.BOOK_Table;
import com.example.abumuhsin.udusmini_library.Tables.PAGES_Table;
import com.example.abumuhsin.udusmini_library.Tables.PICTURES_Table;
import com.example.abumuhsin.udusmini_library.Tables.Picture_BucketTable;
import com.example.abumuhsin.udusmini_library.Views.PageBottomControlPanel;
import com.example.abumuhsin.udusmini_library.Views.PageHeaderView;
import com.example.abumuhsin.udusmini_library.adapters.FlipBook_adapter;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.tasks.Convert_to_pdfTask;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.example.adaptablecurlpage.flipping.utils.DrawingState;
import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.utilities.GalleryType;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Pages_table_model;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ShowSpeedDialog;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ToggleScreenStatus;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.FAST;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.NORMAL;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.SLOW;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.VERY_FAST;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.CAMERA;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getDownSizedBitmapFromPath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getImagePagesFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getPdfsFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getRealDateAndTime;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.saveImage;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.play_pdf;
import static com.example.amazing_picker.activities.Picker_Activity.GALLERY_STATUS;
import static com.example.amazing_picker.activities.Picker_Activity.IMAGES_SELECTED;
import static com.example.amazing_picker.activities.Picker_Activity.NO_IMAGES_SELECTED;
import static com.example.amazing_picker.activities.Picker_Activity.PDF_SELECTED;
import static com.example.mysqlitedbconnection.csv.Constants.COVER_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.LAST_PAGE_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;


public class FlipBooKActivity extends AppCompatActivity implements View.OnClickListener, AdapterPageFlipView.OnPageFlippedListener,
        OnSeekChangeListener, PAGES_Table.OnBookChanged, PageBottomControlPanel.OnControlButtonListener, PageHeaderView.OnControlButtonListener {

    public static final String TAG = "SampleActivityDebug";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    public static final String BOOK_NAME = "book_name";
    public static final int AUTO_FLIP_DURATION = 6000;
    public static int AUTO_FLIP_INTERVAL = 4000;
    private File image_path;
    private FrameLayout header;
    public AdapterPageFlipView mAdapterFlipView;
    public FlipBook_adapter flipBook_adapter;
    private ArrayList<String> images = new ArrayList<>();
    private LinkedList<Flip_model> flip_list;
    View import_dialog;
    private com.github.clans.fab.FloatingActionButton camera_btn, gallery_btn, pdf_btn;
    public View current_page;
    public int current_page_index;
    private LinearLayout tool_panel;
    private Handler handler;
    private Runnable runnable;
    private App_Preferences app_preferences;
    public boolean dont_hide;
    /**
     * Database Instances
     */
    private static BOOK_Table book_table;
    private static PAGES_Table page_table;
    private static PICTURES_Table pic_table;
    private static Picture_BucketTable picture_bucketTable;
    public String current_book;
    private IndicatorSeekBar indicatorSeekBar;
    public boolean is_seeking;
    public String book_cover;
    PageBottomControlPanel pageBottomControlPanel;
    PageHeaderView pageHeaderView;
    ActionBar actionBar;
    public int prev_index;
    Handler handing_auto_refresh;
    Runnable auto_refresh_runnable;
    Handler seek_select_handler;
    Runnable seek_select_run;
    public boolean is_image_seeking = false;
    public boolean page_flipped = false;
    boolean is_first_time;
    public boolean is_fast_flip = false;
    public boolean is_view_fully_loaded = true;
    boolean is_holded = false;
    boolean is_long_ready = false;
    boolean has_entered_onCreate = false;
    public String tmp_book;
    public String pdf_path;
    int tmp_page_no;
    boolean is_from_pcker_result;
    boolean is_from_camera_result;
    public String pdf_name;
    Handler long_press_handler = new Handler();
    Runnable long_press_run = new Runnable() {
        @Override
        public void run() {
            pageBottomControlPanel.toggleAutolay();
            is_long_ready = true;
        }
    };
    public static final String PDF_PATH_DEBUG = "pdf_path_debug";
    boolean is_go_to = false;

    // Main Activity Callbacks...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        init();
        Log.i(TAG, "onCreate: after PostDisplayNavigation");
        if (View_Utils.isSoftNavAvailable(FlipBooKActivity.this)) {
            setUINavigationVisibilityListeneners();
        }
        Log.i(TAG, "onCreate: after setUINavigationVisibilityListeners");
        setUpAdapterFlipView();
        has_entered_onCreate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        has_entered_onCreate = true;
        prev_index = app_preferences.getPageNo(current_book);
        if (!is_from_pcker_result && !is_from_camera_result) {
            page_table.LoadAllPage(this, current_book).start();
            is_from_pcker_result = false;
            is_from_camera_result = false;
        }
        PostDisplayNavigations(2000);
    }

    //Initializations...
    private void init() {
        dont_hide = false;
        initViews();
        initHandoutDatabase();
        String title = current_book.length() >= 20 ? current_book.substring(0, 20) + "..." : current_book;
        if (actionBar!=null) {
            actionBar.setTitle(title);
        }
    }

    private void initHandoutDatabase() {
        Log.i(TAG, "initHandoutDatabase: begins");
        current_book = getIntent().getStringExtra(MyBook_fragment.BOOK_NAME);
        book_cover = getIntent().getStringExtra(MyBook_fragment.BOOK_COVER);
        book_table = MyBook_fragment.book_table;
        page_table = MyBook_fragment.page_table;
        page_table.setOnBookChanged(this);
        pic_table = MyBook_fragment.pic_table;
        picture_bucketTable = MyBook_fragment.picture_bucketTable;
        Log.i(TAG, "initHandoutDatabase: ends");
    }

    private void initViews() {
        pageHeaderView = findViewById(R.id.p_header);
        setSupportActionBar(pageHeaderView.getToolbar());
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Log.i(TAG, "initViews: begins");
        pageBottomControlPanel = findViewById(R.id.bottom_control_panel);
        pageBottomControlPanel.setOnControlButtonListener(this);
        pageHeaderView.setOnHeaderButtonListener(this);
        tool_panel = findViewById(R.id.bottom_linear);
        header = findViewById(R.id.head);
        adjustToolMarginsIfSoftNav(tool_panel);
        adjustToolMarginsIfSoftNav(header);
        indicatorSeekBar = pageBottomControlPanel.getP_seeSeekBar();
        camera_btn = initFab(R.id.camera);
        gallery_btn = initFab(R.id.gallery_btn);
        pdf_btn = initFab(R.id.pdf_btn);
        import_dialog = findViewById(R.id.import_nav);
        //Listen for drag...
        indicatorSeekBar.setOnSeekChangeListener(this);
        Log.i(TAG, "initViews: ends");
    }

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
        mAdapterFlipView = findViewById(R.id.flip);
        app_preferences = new App_Preferences(this.getApplicationContext());
        ToggleScreenStatus(this, app_preferences.getScreenState(current_book));
        pageBottomControlPanel.setB_screenColor(app_preferences.getScreenState(current_book));
        setFlipSpeeds();
        flip_list = new LinkedList<>();
        prev_index = app_preferences.getPageNo(current_book);
        flip_list.add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
        flip_list.add(new Flip_model(LAST_PAGE_TYPE, "back_cover", EMPTY_PAGE));
        mAdapterFlipView.setOnPageFlippedListener(this);
        flipBook_adapter = new FlipBook_adapter(this, flip_list);
        Log.i(TAG, "setUpAdapterFlipView: adapter is settled");
        mAdapterFlipView.setAdapter(flipBook_adapter);
        Log.i(TAG, "setUpAdapterFlipView: ends");
    }

    //Other Activity Callbacks...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && image_path.exists()) {
            is_from_camera_result = true;
            File saved_file = saveAPathFromCameraToPage(image_path.getPath());
            Pages_table_model pages_table_model = new Pages_table_model(current_book, current_page_index
                    , SINGLE_PAGE_TYPE, SINGLE_PAGE, saved_file.getPath());
            page_table.adding_pageInBackground(false, this, pages_table_model).start();
        } else if (requestCode == START_PICKER_REQUEST_CODE) {
            if (resultCode == NO_IMAGES_SELECTED) {
                Log.i(TAG, "no image");
                Toast.makeText(this, "no image was selected", Toast.LENGTH_SHORT).show();
                return;
            }
            if (resultCode == IMAGES_SELECTED || resultCode == PDF_SELECTED) {
                is_from_pcker_result = true;
                Log.i(TAG, " onActivityResult: some images are selected");
                ArrayList<String> images = data.getStringArrayListExtra("pics");
                assert images != null;
                Collections.reverse(images);
                if (current_page_index > 0) {
                    Pages_table_model[] pages_table_models = new Pages_table_model[images.size()];
                    tmp_page_no = 0;
                    for (String image_path : images) {
                        Log.i(TAG, image_path + " inside gallery loop");
                        int page_no = current_page_index;
                        Pages_table_model pages_table_model = new Pages_table_model(current_book, page_no, SINGLE_PAGE_TYPE, SINGLE_PAGE, image_path);
                        pages_table_models[tmp_page_no] = pages_table_model;
                        Log.i(TAG, image_path + " is added to array");
                        tmp_page_no++;
                    }
                    if (resultCode == IMAGES_SELECTED) {
                        page_table.adding_pageInBackground(false, this, pages_table_models).start();
                    } else {
                        pdf_name = data.getStringExtra("pdf_name");
                        Log.i(TAG, image_path + " pdf_name is " + pdf_name);
                        page_table.adding_pageInBackground(true, this, pages_table_models).start();
                    }
                } else {
                    Toast.makeText(this, "you can't add a book b4 the first page", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        has_entered_onCreate = false;
        app_preferences = new App_Preferences(this);
        app_preferences.savePageNo(current_book, current_page_index);
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i("pdf_bitmap", "onDestroy");
        super.onDestroy();
    }

    //Handling camera
    public File saveAPathAsPages(final String path, int from) {
        File dest = new File(getImagePagesFilePath(current_book),
                getRealDateAndTime(from) + "_" + new File(path).getName().replace(".", "") + "." + from);

        int dest_width = View_Utils.getScreenResolution(this).width;
        int dest_height = View_Utils.getScreenResolution(this).height;
        Bitmap bitmap = getDownSizedBitmapFromPath(path, dest_width, dest_height);
        saveImage(bitmap, dest, 70);
        Log.i(TAG, dest.getPath() + " wa saved!!!");
        return dest;
    }

    public File saveAPathFromCameraToPage(final String path) {
        int dest_width = View_Utils.getScreenResolution(this).width;
        int dest_height = View_Utils.getScreenResolution(this).height;
        Bitmap bitmap = getDownSizedBitmapFromPath(path, dest_width, dest_height);
        return saveImage(bitmap, new File(path), 100);

    }

    public void StartDeviceCamera() throws IOException {
        image_path = new File(getImagePagesFilePath(current_book), getRealDateAndTime(CAMERA) + ".jpg");
        Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image_path));
        if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    @Override
    public void onBookChanged(String book_name, List<PAGES_Table.PageInfo> PageInfos, boolean is_removed) {
        final File pdf_file = new File(getPdfsFilePath() + File.separator + current_book + ".pdf");
        final LinkedList<Flip_model> flip_list = getFlip_list();
        if (pdf_file.exists() && flip_list.size() > 2) {
            if (pdf_file.delete()) {
                ChangeBookPdf(pdf_file);
            }
        } else {
            Log.i(TAG, "pdf file does not exist or it's empty");
        }
    }

    private void ChangeBookPdf(final File pdf_file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> images = new ArrayList<>();
                for (int i = 1; i < flip_list.size() - 1; i++) {
                    Log.i("pdf_bitmap", "pdf page  " + i + " is about to add");
                    images.add(flip_list.get(i).getImage_path());
                    Log.i("pdf_bitmap", "pdf page of image " + flip_list.get(i).getImage_path() + " is added");
                }

                try {
                    PdfUtils.add_image_to_pdfItext(FlipBooKActivity.this, pdf_file.getPath(), images, new PdfUtils.OnPdfListener() {
                        @Override
                        public void onPdf_progress(int i) {
                            Log.i("pdf_bitmap", "pdf index " + i + "was added");
                        }
                    });
                    Log.i("pdf_bitmap", "pdf creation Succeed");
                } catch (Exception e) {
                    Log.i("pdf_bitmap", "pdf creation failed");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //  OnClick listener
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery_btn:
                StopHidingNavigations();
                HideImportDialog();
                Intent intent = new Intent(FlipBooKActivity.this, Picker_Activity.class);
                intent.putExtra(BOOK_NAME, current_book);
                intent.putExtra(GALLERY_STATUS, GalleryType.IMAGE);
                startActivityForResult(intent, START_PICKER_REQUEST_CODE);
                break;
            case R.id.camera:
                HideImportDialog();
                try {
                    StartDeviceCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pdf_btn:
                HideImportDialog();
                StopHidingNavigations();
                Intent intent1 = new Intent(FlipBooKActivity.this, Picker_Activity.class);
                intent1.putExtra("book_name", current_book);
                intent1.putExtra(GALLERY_STATUS, GalleryType.PDF);
                startActivityForResult(intent1, START_PICKER_REQUEST_CODE);
                break;
        }
    }

    //Click handlers...

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mAdapterFlipView.is_to_flip()) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (mAdapterFlipView.getmPageFlipView().getmDuration() < 800) {//500 b4
                            mAdapterFlipView.getmPageFlipView().setFlipDuration(800);
                        }
                        mAdapterFlipView.flip_toNextPage(100);//400 b4
                        Log.i(TAG, "jumped to " + (current_page_index + 1));
                    } else if (action == KeyEvent.ACTION_UP) {
                        mAdapterFlipView.getmPageFlipView().setFlipDuration(getNormalSpeed());// 500 b4
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mAdapterFlipView.is_to_flip()) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (mAdapterFlipView.getmPageFlipView().getmDuration() > 300) { //200 b4
                            mAdapterFlipView.getmPageFlipView().setFlipDuration(300);
                        }
                        mAdapterFlipView.flip_toPreviousPage(100); //400 b4
                        Log.i(TAG, "jumped to " + (current_page_index - 1));
                    } else if (action == KeyEvent.ACTION_UP) {
                        mAdapterFlipView.getmPageFlipView().setFlipDuration(getNormalSpeed());// 500 b4
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onfwdClick(View v) {
        mAdapterFlipView.flip_toNextPage(200);
    }

    @Override
    public void onbkwdClick(View v) {
        mAdapterFlipView.flip_toPreviousPage(200);
    }

    @Override
    public void onFwdHold(View v) {
        mAdapterFlipView.setInfiniteFlipForwardinterval(mAdapterFlipView.getInfiniteFlipForwardinterval());
        mAdapterFlipView.flip_Infinitely_Forward(mAdapterFlipView.getInfiniteFlipForwardDuration());
    }

    @Override
    public void onBkwdHold(View v) {
        mAdapterFlipView.setFlipBackwardDuration(mAdapterFlipView.getInfiniteFlipBackwardinterval());
        mAdapterFlipView.flip_Infinitely_Backward(mAdapterFlipView.getFlipBackwardDuration());
    }

    @Override
    public void onFwdReleased(View v) {
        mAdapterFlipView.StopInfiniteFlip();
    }

    @Override
    public void onBkwdReleased(View v) {
        mAdapterFlipView.StopInfiniteFlip();
    }

    @Override
    public void onLockClick(View v) {
        //Todo
    }

    @Override
    public void onSpeedClick(View v) {
        StopHidingNavigations();
        int selected_speed = app_preferences.getFlipSpeed(current_book);
        ShowSpeedDialog(this, selected_speed, new FlipBookActivityHelper.SpeedDialogListener() {
            @Override
            public void onSpeedDialogShown(int selected_speed, String selected_text) {
                app_preferences.saveFlipSpeed(current_book, selected_speed);
                setFlipSpeeds();
            }
        });
    }

    @Override
    public void onPdfClick(View v) {
        StopHidingNavigations();
        try {
            final File pdf_file = new File(getPdfsFilePath() + File.separator + current_book + ".pdf");
            if (pdf_file.exists()) {
                play_pdf(this, pdf_file);
            } else {
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setMessage("For the first time, we will convert this book to pdf. " +
                                "After the successful conversion you can find the pdf in " + pdf_file.getPath())
                        .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Convert_to_pdfTask(FlipBooKActivity.this, null, pdf_file).execute();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "pdf creation failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onAutoPlayClick(View v, boolean is_play) {
        if (is_play) {
            BeginAutoFlip();
        } else {
            StopAutoFlip();
        }
    }

    @Override
    public void onScreenOnToggle(View v, boolean is_screen_Always) {
        app_preferences.saveScreenState(current_book, is_screen_Always);
        ToggleScreenStatus(this, is_screen_Always);
    }


    @Override
    public void onAddClick(View v) {
        StopHidingNavigations();
        header.setVisibility(View.VISIBLE);
        SwitchImportBtnDisplay();
    }

    @Override
    public void onRemoveClick(View v) {
        StopHidingNavigations();
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage("Are you sure you want to delete this file???")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        page_table.RemoveAPage(FlipBooKActivity.this, current_page_index);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onEditClick(View v) {
        int current_page = mAdapterFlipView.getSelectedItemPosition();
        if (current_page > 0 && current_page < flip_list.size() - 1) {
            startEditing();
        } else if (current_page == 0) {
            EditCover();
        }
    }

    @Override
    public void onMoreClick(View v) {
        //Todo
        // more operation
    }

    @Override
    public void onAnyControlClick(View v) {
        StopHidingNavigations();
    }

    // Progress bar callbacks
    @Override
    public void onSeeking(SeekParams seekParams) {
        if (seekParams.fromUser) {
            is_seeking = true;
            if (seekParams.progress > 0) {
                final int seek_position = seekParams.progress - 1;
                seek_select_handler = new Handler();
                seek_select_run = new Runnable() {
                    @Override
                    public void run() {
                        mAdapterFlipView.set_flexible_Selection(seek_position);
                    }
                };
                seek_select_handler.post(seek_select_run);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        StopHidingNavigations();
        CancelAutoRefresh();
        is_seeking = true;
        is_image_seeking = true;
        mAdapterFlipView.MakeAdapterVisible();
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        is_seeking = false;
        is_image_seeking = false;
        flipBook_adapter.notifyDataSetChanged();
    }

    // PageFlipView CallBacks
    @Override
    public void onPageFlipped(View page, int page_no, long id) {
        int end_no = flipBook_adapter.getCount() > 2 ? flipBook_adapter.getCount() - 2 : flipBook_adapter.getCount();
        String no_txt = page_no + 1 + "/" + end_no;
        pageBottomControlPanel.setProgress(no_txt);
        is_go_to = false;
        page_flipped = true;
        current_page = page;
        current_page_index = page_no;
        prev_index = page_no;
        if (!is_seeking) {
            indicatorSeekBar.setMax(flip_list.size() - 2);
            indicatorSeekBar.setMin(1);
            indicatorSeekBar.setProgress(page_no + 1);
        }
    }

    @Override
    public void onPageFlippedForward(View page, int page_no, long id) {

    }

    @Override
    public void onPageFlippedBackward(View page, int page_no, long id) {

    }

    @Override
    public void onFastFlippingStarted(View page, int page_no, boolean is_forward) {
        is_fast_flip = true;
        is_first_time = true;
        Log.i(TAG, "onUserFinger_isDown general");

    }


    @Override
    public void onFastFlippingEnded(View page, int page_no, boolean is_forward) {
        is_fast_flip = false;
        mAdapterFlipView.is_selected_view = true;
    }

    @Override
    public void onRestorePage(int page_no, boolean is_forward) {
        page_flipped = false;
        if (is_forward) {
            Log.i("pdf_bitmap", " onRestorePageForward");
            ManuallyRefresh_Page(50);
        } else {
            Log.i("pdf_bitmap", " is back restore");
        }
    }

    @Override
    public void onResetFlipSpeed() {
        Log.i("pdf_bitmap", " onResetFlipSpeed");

    }

    @Override
    public void onRefreshForwardBackwardFinished(int time_taken) {
        View_Utils.hideSystemUI(FlipBooKActivity.this);
    }

    @Override
    public void onHold(View v, int pos) {
        //if the autoPlay bottom is switched
        Log.i(TAG, "onHold");
        if (pageBottomControlPanel.is_play()) {
            is_holded = true;
            long_press_handler.postDelayed(long_press_run, ViewConfiguration.getLongPressTimeout());
//            StopAutoFlip();
            Log.i(TAG, "onHold is_play");
        }
    }

    @Override
    public void onRelease(View v, int pos) {
        Log.i(TAG, "onRelease");
        if (is_holded) {
            long_press_handler.removeCallbacks(long_press_run);
            if (is_long_ready) {
                pageBottomControlPanel.toggleAutolay();
                is_long_ready = false;
            }
            Log.i(TAG, "onRelease is_play");
            is_holded = false;
            //            BeginAutoFlip();
        }
    }

    @Override
    public void onEndPageReached(View v, int pos, boolean flip_forward) {
        Log.i(TAG, "onEndPageReached");
        if (!flip_forward) {
            return;
        }
        if (pageBottomControlPanel.is_play()) {
            pageBottomControlPanel.toggleAutolay();
            Log.i(TAG, "onEndPageReached is_play()");
        }
        Toast.makeText(this, "You have reached the end of this handout @ pos " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserFinger_isDown(View page, int page_no) {
        CancelAutoRefresh();
    }

    @Override
    public void onUserFinger_isUp(View page, final int page_no) {
        if (page_flipped) {
            mAdapterFlipView.is_selected_view = true;

            Log.i("pdf_bitmap", "the up action does flip the page");
            page_flipped = false;
        } else {
            Log.i("pdf_bitmap", "the up action does not flip the page");
            page_flipped = true;
        }
    }

    @Override
    public void onAfterViewLoadedToFlip(int page_no) {
    }

    @Override
    public void onImageLoadingStatus(View page, int page_no, boolean is_ViewFullyLoaded) {
        is_view_fully_loaded = is_ViewFullyLoaded;
        Log.i("pdf_bitmap", is_ViewFullyLoaded ? "View is fully loaded: " : "not fully loaded");
    }

    @Override
    public int onChangeActiveIndex(int active_index) {
        if (prev_index != active_index) {
            return prev_index;
        }
        return active_index;
    }

    //Getters...
    public LinkedList<Flip_model> getFlip_list() {
        return flip_list;
    }

    public FlipBook_adapter getFlipBook_adapter() {
        return flipBook_adapter;
    }

    public int getNormalSpeed() {
        int speed = app_preferences.getFlipSpeed(current_book);
        switch (speed) {
            case NORMAL:
                return 1500;
            case FAST:
                return 600;
            case VERY_FAST:
                return 400;
            case SLOW:
                return 2000;
        }
        return 1500;
    }

    public void setFlipSpeeds() {
        int speed = app_preferences.getFlipSpeed(current_book);
        switch (speed) {
            case NORMAL:
                mAdapterFlipView.getmPageFlipView().setFlipDuration(1500);
                mAdapterFlipView.setInfiniteFlipForwardDuration(900);
                mAdapterFlipView.setInfiniteFlipForwardinterval(280);
                mAdapterFlipView.setInfiniteFlipBackwardDuration(350);
                mAdapterFlipView.setInfiniteFlipBackwardinterval(300);
                mAdapterFlipView.setFlipBackwardDuration(1000);
                break;
            case FAST:
                mAdapterFlipView.getmPageFlipView().setFlipDuration(800);
                mAdapterFlipView.setInfiniteFlipForwardDuration(700);
                mAdapterFlipView.setInfiniteFlipForwardinterval(200);
                mAdapterFlipView.setInfiniteFlipBackwardDuration(300);
                mAdapterFlipView.setInfiniteFlipBackwardinterval(220);
                mAdapterFlipView.setFlipBackwardDuration(300);
                break;
            case VERY_FAST:
                mAdapterFlipView.getmPageFlipView().setFlipDuration(400);
                mAdapterFlipView.setInfiniteFlipForwardDuration(400);
                mAdapterFlipView.setInfiniteFlipForwardinterval(200);
                mAdapterFlipView.setInfiniteFlipBackwardDuration(240);
                mAdapterFlipView.setInfiniteFlipBackwardinterval(180);
                mAdapterFlipView.setFlipBackwardDuration(230);
                break;
            case SLOW:
                mAdapterFlipView.getmPageFlipView().setFlipDuration(2000);
                mAdapterFlipView.setInfiniteFlipForwardDuration(1100);
                mAdapterFlipView.setInfiniteFlipForwardinterval(300);
                mAdapterFlipView.setInfiniteFlipBackwardDuration(400);
                mAdapterFlipView.setInfiniteFlipBackwardinterval(320);
                mAdapterFlipView.setFlipBackwardDuration(1200);
                break;
        }

    }

    public Handler getHandler() {
        return handler;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    //...........................Helpers....................

    // Handling UI Visibilities.....

    public void adjustToolMarginsIfSoftNav(ViewGroup tool_view) {
        if (View_Utils.isSoftNavAvailable(this)) {
            LinearLayout.MarginLayoutParams LlayoutParams = null;
            FrameLayout.LayoutParams FlayoutParams = null;
            if (tool_view instanceof LinearLayout) {
                LlayoutParams = (LinearLayout.MarginLayoutParams) tool_view.getLayoutParams();
            } else if (tool_view instanceof FrameLayout) {
                FlayoutParams = (FrameLayout.LayoutParams) tool_view.getLayoutParams();
            }
            Context context = tool_view.getContext();
            float nav_height = View_Utils.getNavHight(context);
            float sBar_height = View_Utils.getNavHight(context) / 2;
            if (!View_Utils.isLandScape(this)) {
                if (tool_view.equals(tool_panel)) {
                    assert LlayoutParams != null;
                    LlayoutParams.bottomMargin = (int) nav_height;
                } else if (tool_view.equals(header)) {
                    assert FlayoutParams != null;
                    FlayoutParams.topMargin = (int) sBar_height;
                }
            } else {
                if (tool_view.equals(tool_panel)) {
                    assert LlayoutParams != null;
                    LlayoutParams.rightMargin = (int) nav_height;
                } else if (tool_view.equals(header)) {
                    assert FlayoutParams != null;
                    FlayoutParams.topMargin = (int) sBar_height;
                    FlayoutParams.rightMargin = (int) nav_height;
                }
            }
        }
    }

    private void setUINavigationVisibilityListeneners() {
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // TODO: The system bars are visible. Make any desired
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
//                            layoutParams.bottomMargin = 0;
                        }
                    }
                });
    }

    public void showImportDialog() {
        View_Utils.ShowView_with_ZoomOut((ViewGroup) import_dialog);
        ShowView(gallery_btn);
        ShowView(camera_btn);
        ShowView(pdf_btn);
    }

    public void HideImportDialog() {
        HideView(gallery_btn);
        HideView(camera_btn);
        HideView(pdf_btn);
        View_Utils.hideView_with_ZoomIn((ViewGroup) import_dialog);
    }

    public void SwitchImportBtnDisplay() {
        if (gallery_btn.isShown()) {
            HideImportDialog();
        } else {
            showImportDialog();
        }
    }

    public void HideNavigations() {
        View_Utils.hideSystemUI(this);
        boolean visible = false;
        HideHeader_nav();
        HideFooter_nav();
        if (gallery_btn.isShown()) {
            HideImportDialog();
        }
    }

    public void StopHidingNavigations() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    void ShowView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    void HideView(View v) {
        v.setVisibility(GONE);
    }

    public void DisplayNavigation() {
        View_Utils.showSystemUI(this);
        if (!header.isShown() || !tool_panel.isShown()) {
            ShowHeader_nav();
            pageBottomControlPanel.show();
//            ShowView(tool_panel);
        }
    }

    public void HideHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        HideView(header);
    }

    public void HideFooter_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
//        tool_panel.setVisibility(GONE);
        pageBottomControlPanel.hide();
    }

    public void ShowHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        header.setVisibility(View.VISIBLE);
    }

    public boolean is_nav_visible() {
        return header.getVisibility() == View.VISIBLE;
    }

    public void PostDisplayNavigations(int duration) {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        handler = new Handler();
        DisplayNavigation();
        runnable = new Runnable() {
            @Override
            public void run() {
                HideNavigations();
            }
        };
        handler.postDelayed(runnable, duration);
        boolean is_revealed = false;
    }

    public void CancelAutoRefresh() {
        if (handing_auto_refresh != null && auto_refresh_runnable != null) {
            handing_auto_refresh.removeCallbacks(auto_refresh_runnable);
        }
    }

    private void startEditing() {
//        File tmp_file = new File(getCacheDir(), "editing_img.jpg");
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra("pic", flip_list.get(current_page_index).getImage_path());
        startActivity(intent);
    }

    //  Refresh...
    public void AutoRefreshAfter(int duration) {
        CancelAutoRefresh();
        handing_auto_refresh = new Handler();
        auto_refresh_runnable = new Runnable() {
            @Override
            public void run() {
                mAdapterFlipView.RefreshFlip();
//                mAdapterFlipView.getmPageFlipView().startAnimation(AnimationUtils.loadAnimation(FlipBooKActivity.this, R.anim.flip_fade_in));
                mAdapterFlipView.MakeFlipVisible();
            }
        };

        handing_auto_refresh.postDelayed(auto_refresh_runnable, duration);
    }

    public void ManuallyRefresh_Page(int duration) {
        CancelAutoRefresh();
        handing_auto_refresh = new Handler();
        auto_refresh_runnable = new Runnable() {
            @Override
            public void run() {
                mAdapterFlipView.setSelection(current_page_index);
            }
        };

        handing_auto_refresh.postDelayed(auto_refresh_runnable, duration);
    }

    public void OpenBook(final int duration) {
        if (prev_index >= 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapterFlipView.getmDrawingState() != DrawingState.REFRESH_DRAW) {
                        mAdapterFlipView.getmPageFlipView().setFlipDuration(duration);
                        mAdapterFlipView.flip_toNextPage(200);
                    }
                }
            }, 100);
            mAdapterFlipView.ResetFlipDuration(duration /*+ 200*/);
        }
    }

    public static final int START_PICKER_REQUEST_CODE = 8787;

    private void EditCover() {
    }

    public void addAnEmptyPage() {
        if (current_page_index > 0) {
            int page_no = current_page_index;
            Pages_table_model pages_table_model = new Pages_table_model(current_book, page_no, SINGLE_PAGE_TYPE, EMPTY_PAGE);
            page_table.adding_pageInBackground(false, this, pages_table_model).start();
        } else {
            Toast.makeText(this, "you can't add a book b4 the first page", Toast.LENGTH_SHORT).show();
        }
    }

    // bottom control helpers...
    public void BeginAutoFlip() {
        if (is_holded) {
            PostDisplayNavigations(1000);
        } else {
            PostDisplayNavigations(10);
        }
        mAdapterFlipView.setInfiniteFlipForwardinterval(AUTO_FLIP_INTERVAL);
        mAdapterFlipView.flip_Infinitely_Forward(AUTO_FLIP_DURATION);
        Log.i(TAG, "autoflip started");
    }

    private void StopAutoFlip() {
        mAdapterFlipView.StopInfiniteFlip();
        setFlipSpeeds();
        StopHidingNavigations();
        Log.i(TAG, "autoflip stopped");
    }

    private com.github.clans.fab.FloatingActionButton initFab(int id) {
        com.github.clans.fab.FloatingActionButton btn = findViewById(id);
        btn.setOnClickListener(this);
        return btn;
    }

    public boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }

}

