
package com.example.abumuhsin.udusmini_library.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.burhanrashid52.imageeditor.EditImageActivity;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Tables.BOOK_Table;
import com.example.abumuhsin.udusmini_library.Tables.PAGES_Table;
import com.example.abumuhsin.udusmini_library.Tables.PICTURES_Table;
import com.example.abumuhsin.udusmini_library.Tables.Picture_BucketTable;
import com.example.abumuhsin.udusmini_library.adapters.Different_layout_Adapter;
import com.example.abumuhsin.udusmini_library.adapters.ExternalPdfAdapter;
import com.example.abumuhsin.udusmini_library.adapters.Recyler_adapter;
import com.example.abumuhsin.udusmini_library.adapters.Zoom_adapter;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.flip_model;
import com.example.abumuhsin.udusmini_library.tasks.Convert_to_pdfTask;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.PdfBackLoaderTask;
import com.example.abumuhsin.udusmini_library.utils.PdfForwardLoader;
import com.example.abumuhsin.udusmini_library.utils.PdfPagesNoGenerator;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.example.adaptablecurlpage.flipping.utils.DrawingState;
import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.utilities.GalleryType;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Pages_table_model;
import com.github.clans.fab.FloatingActionButton;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.BOOK_NAME_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.IMAGES_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.MSG_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment.PDF_BOOK_NAME_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment.PDF_MSG_EXTRA;
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
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_File_for_ActivePdf;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_ZoomableFile;
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


public class FlipBooKActivity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener
        , AdapterPageFlipView.OnPageFlippedListener, OnSeekChangeListener, PAGES_Table.OnBookChanged {

    public static final String TAG = "SampleActivityDebug";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    public static final String BOOK_NAME = "book_name";
    private File image_path;
    private FrameLayout header;
    public AdapterPageFlipView mAdapterFlipView;
    private float scale = 1f;
    public Zoom_adapter zoom_adapter;
    public ExternalPdfAdapter externalPdfAdapter;
    //    private Sample_adapter sample_adapter;
    private Different_layout_Adapter differentLayoutAdapter;
    private ArrayList<String> images = new ArrayList<>();
    //    private ArrayList<String> flip_list;
    private LinkedList<flip_model> flip_list;
    private ArrayList<String> bucket_images;
    private Recyler_adapter recyler_adapter;

    private ImageButton float_delete, float_crop;
    View import_dialog;
    private com.github.clans.fab.FloatingActionButton lock_btn, play_pdf_btn, add_btn, more_btn, remove_page_btn,
            camera_btn, gallery_btn, exit_book_btn, float_edit_btn, pdf_btn;

    public View current_page;
    public int current_page_index;
    private LinearLayout tool_panel;
    private FrameLayout frameLayout;
    private boolean is_revealed;
    private boolean visible;
    private Handler handler;
    private Runnable runnable;
    private App_Preferences app_preferences;
    boolean is_switched;
    private float previous_zoomValue;
    public boolean dont_hide;
    private AlertDialog cropping_dialog;
    public static final int DEFAULT_ANIMATION_DURATION = 1500;

    /**
     * Database Instances
     */
    private static BOOK_Table book_table;
    private static PAGES_Table page_table;
    private static PICTURES_Table pic_table;
    private static Picture_BucketTable picture_bucketTable;

    public int LIGTH_BLUE;
    public int LIGTH_GREEN;
    public int LIGTH_YELLOW;
    public int LIGTH_RED;

    private View dragged_view;
    public String current_book;
    private IndicatorSeekBar indicatorSeekBar;
    //    private IndicatorStayLayout indicator_stay;
    public boolean is_seeking;
    public String book_cover;

    public int getBucketPosition() {
//        recyclerView.scrollToPosition();
        return recyler_adapter.adapter_position;
    }

    public ArrayList<String> getBucket_images() {
        return bucket_images;
    }

    public Recyler_adapter getRecyler_adapter() {
        return recyler_adapter;
    }

    public Intent other_app_intent;
    PdfPagesNoGenerator pdfPagesNoGenerator;

    //Initializations...
    private void init() {
        pdfPagesNoGenerator = new PdfPagesNoGenerator();
        dont_hide = false;
        is_pdf_from_otherApp = false;
        is_pics_from_device = false;
        other_app_intent = getIntent();
        if (other_app_intent.getBooleanExtra(MSG_EXTRA, false)) {
            is_pics_from_device = true;
            Log.i(TAG, "int()-> is_pics_from_device: is_from gallery");
        } else if (other_app_intent.getBooleanExtra(PDF_MSG_EXTRA, false)) {
            is_pdf_from_device = true;
            Log.i(TAG, "int()-> is_pdf_from_device: is_from Device pdf");
        }
        String action = other_app_intent.getAction();
        String type = other_app_intent.getType();
        Log.i("pdf_bitmap", " b4 testing");
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            Log.i("pdf_bitmap", " from another app");
            if ("application/pdf".equals(type)) {
                is_pdf_from_otherApp = true;
//                ReceiveShared_pdfData(intent); // Handle text being sent
            }
        }
        initViews();
        initHandoutDatabase();
        initColors();
    }

    public void showImportDialog() {
        View_Utils.ShowView_with_ZoomOut((ViewGroup) import_dialog);
        ShowView(gallery_btn);
        ShowView(camera_btn);
        ShowView(pdf_btn);
    }

    public FloatingActionButton getGallery_btn() {
        return gallery_btn;
    }

    public void HideImportDialog() {
        HideView(gallery_btn);
        HideView(camera_btn);
        HideView(pdf_btn);
        View_Utils.hideView_with_ZoomIn((ViewGroup) import_dialog);
    }

    private void initColors() {
//new IndicatorSeekBar().setMax();
        LIGTH_BLUE = getResources().getColor(R.color.light_blue);
        LIGTH_GREEN = getResources().getColor(R.color.light_green);
        LIGTH_YELLOW = getResources().getColor(R.color.light_yellow);
        LIGTH_RED = getResources().getColor(R.color.light_red);
    }

    private void initHandoutDatabase() {
        Log.i(TAG, "initHandoutDatabase: begins");
        if (is_pdf_from_otherApp) {
            current_book = "other_app_pdf";
            Log.i(TAG, "initHandoutDatabase: pdf is from other app");
        } else if (is_pics_from_device) {
            current_book = other_app_intent.getStringExtra(BOOK_NAME_EXTRA);
            Log.i(TAG, "initHandoutDatabase()-> is_pics_from_device: Book name is " + current_book);
        } else if (is_pdf_from_device) {
            File pdf_file = new File(other_app_intent.getStringExtra(PDF_BOOK_NAME_EXTRA));
            current_book = pdf_file.getName();
            Log.i(TAG, "initHandoutDatabase()-> is_pdf_from_device: Book name is " + current_book);
        } else {
            current_book = getIntent().getStringExtra(MyBook_fragment.BOOK_NAME);
            book_cover = getIntent().getStringExtra(MyBook_fragment.BOOK_COVER);
            ((TextView) findViewById(R.id.txt)).setText(current_book);
            book_table = MyBook_fragment.book_table;
            page_table = MyBook_fragment.page_table;
            page_table.setOnBookChanged(this);
            pic_table = MyBook_fragment.pic_table;
            picture_bucketTable = MyBook_fragment.picture_bucketTable;
//            Log.i(TAG, picture_bucketTable.test());
            Log.i(TAG, "initHandoutDatabase: ends");
        }
    }

    private void initViews() {
        Log.i(TAG, "initViews: begins");
        frameLayout = findViewById(R.id.lay);
        float_crop = findViewById(R.id.float_crop);
        float_delete = findViewById(R.id.float_delete);
        tool_panel = findViewById(R.id.bottom_lay);
        header = findViewById(R.id.head);
        adjustToolMarginsIfSoftNav(tool_panel);
        adjustToolMarginsIfSoftNav(header);
        indicatorSeekBar = findViewById(R.id.indicator_seekbar);
        exit_book_btn = initFab(R.id.exit_book_btn);
        float_edit_btn = initFab(R.id.float_edit_btn);
        lock_btn = initFab(R.id.lock_btn);
        play_pdf_btn = initFab(R.id.play_pdf);
        add_btn = initFab(R.id.add_btn);
        more_btn = initFab(R.id.more_btn);
        remove_page_btn = initFab(R.id.remove_a_page);
        camera_btn = initFab(R.id.camera);
        gallery_btn = initFab(R.id.gallery_btn);
        pdf_btn = initFab(R.id.pdf_btn);
        import_dialog = findViewById(R.id.import_nav);
        //Listen for click...
        lock_btn.setOnClickListener(this);
        play_pdf_btn.setOnClickListener(this);
        float_crop.setOnClickListener(this);
        float_delete.setOnClickListener(this);
        //Listen for drag...
        float_crop.setOnDragListener(this);
        float_delete.setOnDragListener(this);
        indicatorSeekBar.setOnSeekChangeListener(this);
        Log.i(TAG, "initViews: ends");
    }

    public LinkedList<flip_model> getFlip_list() {
        return flip_list;
    }

    public Zoom_adapter getZoom_adapter() {
        return zoom_adapter;
    }

    public int prev_index;

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

    Handler handing_auto_refresh;
    Runnable auto_refresh_runnable;

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
        mAdapterFlipView = findViewById(R.id.flip);
        app_preferences = new App_Preferences(this.getApplicationContext());
        setFlipSpeeds();
        flip_list = new LinkedList<>();
        if (is_from_pdf()) {
            if (other_app_intent != null) {
                ReceiveShared_pdfData(other_app_intent);
            }
            page_count = PdfUtils.getPdfPage_count(FlipBooKActivity.this, pdf_path);
            for (int i = 0; i < page_count; i++) {
                String page_path = null;
//                if (other_app_intent != null) {
//                    page_path = PdfUtils.getActivePdf_ImagePath(tmp_book + "_dir", i);
//                } else {
                page_path = PdfUtils.getActivePdf_ImagePath(tmp_book, i);
//                }
                Log.i(PDF_PATH_DEBUG, "Path test -> the path is " + page_path);
                flip_list.add(new flip_model(SINGLE_PAGE_TYPE, page_path, SINGLE_PAGE));
            }
        } else {
            if (is_pics_from_device) {
                prev_index = app_preferences.getPageNo(current_book);
                flip_list.add(new flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
                Log.i(TAG, "setUpAdapterFlipView-> is_pics_from_device: first_page added for device gallery Flip");
            } else {
                prev_index = app_preferences.getPageNo(current_book);
                flip_list.add(new flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
                flip_list.add(new flip_model(LAST_PAGE_TYPE, "back_cover", EMPTY_PAGE));
            }
        }
        mAdapterFlipView.setOnPageFlippedListener(this);

        if (is_from_pdf()) {
            Log.i("pdf_bitmap", "setUpAdapterFlipView: pageCount is" + page_count);
            externalPdfAdapter = new ExternalPdfAdapter(this, flip_list);
            Log.i(TAG, "setUpAdapterFlipView: adapter is settled");
            mAdapterFlipView.SetAdapter(externalPdfAdapter, prev_index);
//            mAdapterFlipView.setAdapter(externalPdfAdapter);
            if (page_count <= 1) {
                View_Utils.showSystemUI(this);
                mAdapterFlipView.MakeAdapterVisible();
            }
            Log.i(TAG, "setUpAdapterFlipView: ends");
        } else {
            zoom_adapter = new Zoom_adapter(this, flip_list);
            Log.i(TAG, "setUpAdapterFlipView: adapter is settled");
            mAdapterFlipView.setAdapter(zoom_adapter);
            Log.i(TAG, "setUpAdapterFlipView: ends");
        }
    }

    Handler seek_select_handler;
    Runnable seek_select_run;

    public void Post_display_for_seeking(final int seek_position) {
        cancel_display_seek();
        seek_select_handler = new Handler();

        seek_select_run = new Runnable() {
            @Override
            public void run() {
                GoToPage(seek_position, true, false);
            }
        };
        seekHandler.postDelayed(seek_select_run, 100);

    }

    public void cancel_display_seek() {
        if (seekHandler != null && seek_select_run != null) {
            seekHandler.removeCallbacks(seek_select_run);
        }
    }

    // Progress bar callbacks
    @Override
    public void onSeeking(SeekParams seekParams) {
        if (seekParams.fromUser) {
            is_seeking = true;
            if (seekParams.progress > 0) {
                final int seek_position = seekParams.progress - 1;
                if (is_pdf_from_otherApp || is_pdf_from_device) {
                    is_for_jump = true;
//                    CancelAutoRefresh();
//                    mAdapterFlipView.is_selected_view = false;
                } else {
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
    }

    public boolean is_image_seeking = false;
    public boolean page_flipped = false;
    boolean is_first_time;

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        StopHidingNavigations();
        StopHidingSeekBar();
        CancelAutoRefresh();
        is_seeking = true;
        if (!is_pdf_from_otherApp && !is_pdf_from_device) {
            is_image_seeking = true;
        }
        mAdapterFlipView.MakeAdapterVisible();
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        is_seeking = false;
        if (is_pdf_from_otherApp || is_pdf_from_device) {
            is_for_jump = true;
            mAdapterFlipView.is_selected_view = true;
            GoToPage(seekBar.getProgress() - 1, true, false);
        } else {
            is_image_seeking = false;
            zoom_adapter.notifyDataSetChanged();
        }
//        mAdapterFlipView.MakeFlipVisible();
    }

    public boolean is_from_pdf() {
        return is_pdf_from_otherApp || is_pdf_from_device;
    }

    // PageFlipView CallBacks
    @Override
    public void onPageFlipped(View page, int page_no, long id) {
        if (is_from_pdf() && !is_seeking && !is_go_to) {
            if (page_no == current_page_index + 1 && !is_fast_flip) {
                Log.i(TAG, "is forward");
                SaveNext(current_page_index, false).start();
            } else if (page_no == current_page_index - 1 && !is_fast_flip) {
                Log.i(TAG, "is backward");
                SavePrevious(current_page_index, false).start();
            }
            if (!is_fast_flip) {
                SaveZoomablePage(page_no).start();
                Log.i("pdf_bitmap", "zoomable page of index " + page_no + "is saved");
            }
        }
        is_go_to = false;
        page_flipped = true;
        current_page = page;
        current_page_index = page_no;
        prev_index = page_no;
        if (!is_seeking) {
            indicatorSeekBar.setMax(flip_list.size() - 2);
            indicatorSeekBar.setMin(1);
            indicatorSeekBar.setProgress(page_no + 1);
            PostHideSeekBar(4000);
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
    }

    public boolean is_fast_flip = false;
    public boolean is_view_fully_loaded = true;

    @Override
    public void onFastFlippingEnded(View page, int page_no, boolean is_forward) {
        is_fast_flip = false;
        is_for_jump = true;
        mAdapterFlipView.is_selected_view = true;
        if (is_from_pdf()) {
            GoToPage(page_no, true, true);
        }
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


    public void CancelAutoRefresh() {
        if (handing_auto_refresh != null && auto_refresh_runnable != null) {
            handing_auto_refresh.removeCallbacks(auto_refresh_runnable);
        }
    }

    Uri pdf_uri;
    public static final String PDF_PATH_DEBUG = "pdf_path_debug";

    private void ReceiveShared_pdfData(Intent intent) {
//        Uri pdf_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM); //for actionSEND
        if (!is_pdf_from_device) {
            pdf_uri = intent.getData();
            if (pdf_uri != null) {
//                pdf_path = pdf_uri.getPath();
                pdf_path = copy_tmp_pdf(pdf_uri);
                Log.i(PDF_PATH_DEBUG, "pdf_path is " + pdf_path);
            } else {
                Log.i(PDF_PATH_DEBUG, "pdf_uri is empty ");
            }
        } else {
            pdf_path = intent.getStringExtra(PDF_BOOK_NAME_EXTRA);
        }
        page_count = PdfUtils.getPdfPage_count(FlipBooKActivity.this, pdf_path);
        Log.i(PDF_PATH_DEBUG, "pdf path is " + pdf_path);
        tmp_book = new File(pdf_path).getName();
        current_book = tmp_book;
        ((TextView) findViewById(R.id.txt)).setText(current_book);
        Log.i(PDF_PATH_DEBUG, "pdf name is " + tmp_book);
        prev_index = app_preferences.getPageNo(current_book);
    }

    private String copy_tmp_pdf(Uri pdf_uri) {
        String[] split = pdf_uri.getPath().split("/");
        String pdf_name = split[split.length - 1];
        if (pdf_name.contains(".pdf"))
            pdf_name.replace(".pdf", "");
        File dest_file = new File(FileUtils.getBooksFilesPath(), pdf_name + "_tmp_pdf.pdf");
        File new_PDFile = null;
        try {
            InputStream pdf_stream = this.getContentResolver().openInputStream(pdf_uri);
            if (pdf_stream != null) {
                new_PDFile = PdfUtils.getPdfFileWith_Stream(this, pdf_stream, dest_file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (new_PDFile != null) {
            return new_PDFile.getPath();
        } else {
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        has_entered_onCreate = true;
        if (is_from_pdf()) {
            //TODO all the way from the other_app pdfs, how can i help you???
            if (other_app_intent != null) {
                prev_index = app_preferences.getPageNo(current_book);
                //On second trial. i realise that the problem is from glide, the activity context passed to it return null on 2nd resume
                GoToPage(prev_index, false, false);
            }
        } else if (is_pics_from_device) {
            //TODO all the way from the device gallery, how can i help you???
            ArrayList<String> gallery_images = other_app_intent.getStringArrayListExtra(IMAGES_EXTRA);
            if (gallery_images != null) {
                flip_list.clear();
                for (String page_path : gallery_images) {
                    flip_list.add(new flip_model(SINGLE_PAGE_TYPE, page_path, SINGLE_PAGE));
                }
            } else {
                flip_list.add(new flip_model(SINGLE_PAGE_TYPE, "no image", SINGLE_PAGE));
            }
            Log.i(TAG, "onResume()-> is_pics_from_device: adding " + flip_list.get(flip_list.size() - 1).getImage_path());
            prev_index = app_preferences.getPageNo(current_book);
            //Realise that i have to post a new selection to avoid any problem, because the render is not able to draw an empty bitmap.
            //On second trial. i realise that there is also problem is from glide, the activity context passed to it return null on 2nd resume
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        zoom_adapter.notifyDataSetChanged();
                        Log.i(TAG, "onResume()-> is_pics_from_device: can draw");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onResume()-> is_pics_from_device: can't draw");
                    }
                }
            });
            Log.i(TAG, "onResume()-> is_pics_from_device: Adapter notified");
//            OpenBook(1000);
            Log.i(TAG, "onResume()-> is_pics_from_device: Adapter book opened");
        } else {
            prev_index = app_preferences.getPageNo(current_book);
            if (!is_from_pcker_result && !is_from_camera_result) {
                page_table.LoadAllPage(this, current_book).start();
                is_from_pcker_result = false;
                is_from_camera_result = false;
            }
        }
        PostDisplayNavigations(2000);
    }

    @Override
    protected void onPause() {
        if (is_from_pdf()) {
            if (PdfForwardLoader.get(FlipBooKActivity.this).is_pdf_thread_objectRunning()) {
                PdfForwardLoader.get(FlipBooKActivity.this).JustStop_pdfThread();
            }
            if (PdfBackLoaderTask.get(FlipBooKActivity.this).is_pdf_thread_objectRunning()) {
                PdfBackLoaderTask.get(FlipBooKActivity.this).JustStop_pdfThread();
            }
        }
        has_entered_onCreate = false;
        app_preferences = new App_Preferences(this);
//        app_preferences.savePageNo(current_book, current_page_index > 0 ? (is_pdf_from_otherApp ? current_page_index - 1 : current_page_index - 1) : 0);
        app_preferences.savePageNo(current_book, current_page_index);
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (is_from_pdf()) {
            if (PdfForwardLoader.get(this).is_pdf_thread_notNull() && PdfBackLoaderTask.get(this).is_pdf_thread_notNull()) {
                PdfForwardLoader.get(this).stop_pdfThread_and_delete_tmp_pages();
                PdfBackLoaderTask.get(this).JustStop_pdfThread();
            }
            for (int i = 0; i < page_count; i++) {
                File page_file = create_new_ZoomableFile(tmp_book + "_dir", i);
                if (page_file.exists()) {
                    if (page_file.delete()) {
                        Log.i("pdf_bitmap", "current zoomable page is deleted");
                    }
                }
            }
        }
        Log.i("pdf_bitmap", "onDestroy");
        super.onDestroy();
    }

    public int page_count;

    public static final String PDF_LOAD_DEBUG = "pdf_debug";

    public SparseArray<Bitmap> page_bimaps = new SparseArray<>();
    SparseArray<String> deletable_files = new SparseArray<>();
    public boolean can_load_bitmap = false;
    Runnable reload_run;
    Handler reload_handler = new Handler();
    boolean is_go_to = false;

    public void GoToPage(final int page_no, final boolean is_tojump, final boolean is_forward) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                is_go_to = true;
                is_first_time = true;
                new Pdf_loaderTask(FlipBooKActivity.this, page_no, is_tojump, is_forward).execute();
                if (PdfForwardLoader.get(FlipBooKActivity.this).is_pdf_thread_objectRunning()) {
                    PdfForwardLoader.get(FlipBooKActivity.this).JustStop_pdfThread();
                }
                if (PdfBackLoaderTask.get(FlipBooKActivity.this).is_pdf_thread_objectRunning()) {
                    PdfBackLoaderTask.get(FlipBooKActivity.this).JustStop_pdfThread();
                }
//                pdfPagesNoGenerator.flippedToView(page_no);
//                Integer[] pages = new Integer[pdfPagesNoGenerator.getBufferedPages().size()];
//                pdfPagesNoGenerator.getBufferedPages().toArray(pages);
//                new PdfPageSaverForwardTask(pdf_path, page_no).execute(pages);
                PdfBackLoaderTask.get(FlipBooKActivity.this).LoadPdfToStorage(pdf_path, PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book + "_dir", page_no));
                PdfForwardLoader.get(FlipBooKActivity.this).LoadPdfToStorage(pdf_path, PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book + "_dir", page_no), true);
                PdfBackLoaderTask.get(FlipBooKActivity.this).setOnPdfPageDownloadListener(new PdfBackLoaderTask.OnPdfPageDownloadListener() {
                    @Override
                    public void onPdfPageDownloaded(int page_no, String page_path) {
                        int selectedItemPosition = mAdapterFlipView.getSelectedItemPosition();
                        int prev_position = selectedItemPosition - 1 > 0 ? selectedItemPosition - 1 : selectedItemPosition;
                        if (page_no == selectedItemPosition || page_no == prev_position || page_no == prev_position - 1) {
                            mAdapterFlipView.setSelection(current_page_index);
//                            ManuallyRefresh_Page(100);
                        }
                    }
                });
                PdfForwardLoader.get(FlipBooKActivity.this).setOnPdfPageDownloadListener(new PdfBackLoaderTask.OnPdfPageDownloadListener() {
                    @Override
                    public void onPdfPageDownloaded(int page_no, String page_path) {
                        int selectedItemPosition = mAdapterFlipView.getSelectedItemPosition();
                        int count = mAdapterFlipView.getCount();
                        int next_position = selectedItemPosition + 1 < count ? selectedItemPosition + 1 : count - 1;
                        if (page_no == selectedItemPosition || page_no == next_position || page_no == next_position + 1) {
                            mAdapterFlipView.setSelection(current_page_index);
//                            ManuallyRefresh_Page(100);
                        }
                    }
                });
                stop_running = true;
                stop_running = false;
                SaveZoomablePage(page_no).start();
                mAdapterFlipView.MakeAdapterVisible();
//                mAdapterFlipView.RefreshBack_Forward();
                Log.i("pdf_bitmap", "zoomable page of index " + page_no + "is saved");
            }
        });
        Log.i("pdf_bitmap", "After Loading pages start from index " + page_no);
    }


    public static class Pdf_loaderTask extends AsyncTask<Void, Void, File> {
        int requested_index;
        int page_count;
        String tmp_book;
        String pdf_path;
        boolean is_to_jump;
        boolean is_forward;
        @SuppressLint("StaticFieldLeak")
        FlipBooKActivity context;

        Pdf_loaderTask(FlipBooKActivity context, int requested_index, boolean is_tojump, boolean is_forward) {
            this.requested_index = requested_index;
            this.context = context;
            is_to_jump = is_tojump;
            tmp_book = context.tmp_book;
            pdf_path = context.pdf_path;
            this.is_forward = is_forward;
        }

        @Override
        protected File doInBackground(Void... voids) {
            int start_index = requested_index - 1 >= 0 ? requested_index - 1 : 0;
            int end_index = page_count >= requested_index + 2 ? requested_index + 2 : page_count;
            for (int i = start_index; i < end_index; i++) {

                String page_path = null;
//                if (context.other_app_intent != null) {
//                    page_path = PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book + "_dir", i);
//                } else {
                page_path = PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book, i);
//                }
                File saved_file = PdfUtils.SavePdfPage_to(context, pdf_path, page_path, i);
                Log.i(PDF_PATH_DEBUG, "Pdf_loaderTask-> " + (saved_file != null ? "pdf_page of path " + saved_file.getPath()
                        + " is saved" : "unable save pdf page"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(File page_file) {
            super.onPostExecute(page_file);
            context.mAdapterFlipView.setSelection(requested_index);
        }
    }

    public Thread SaveNext(int requested_index, final boolean is_for_jump) {
        final int index;
        index = page_count - 1 >= requested_index + 2 ? requested_index + 2 : page_count - 1;
        return new Thread(new Runnable() {
            @Override
            public void run() {
                File page_file = null;
//                if (other_app_intent != null) {
//                    page_file = create_new_File_for_ActivePdf(tmp_book + "_dir", index);
//                } else {
                page_file = create_new_File_for_ActivePdf(tmp_book, index);
//                }
                PdfUtils.SavePdfPage_to(FlipBooKActivity.this, pdf_path, page_file.getPath(), index);
                Log.i(PDF_LOAD_DEBUG, "SaveNext-> Next page is ready");
            }
        });
    }

    public Thread SavePrevious(int requested_index, final boolean is_for_jump) {
        final int index;
        index = requested_index - 2 >= 0 ? requested_index - 2 : 0;
        return new Thread(new Runnable() {
            @Override
            public void run() {

                File page_file = null;
//                if (other_app_intent != null) {
//                    page_file = create_new_File_for_ActivePdf(tmp_book + "_dir", index);
//                } else {
                page_file = create_new_File_for_ActivePdf(tmp_book, index);
//                }
                PdfUtils.SavePdfPage_to(FlipBooKActivity.this, pdf_path, page_file.getPath(), index);
                Log.i(PDF_LOAD_DEBUG, "SavePrevious-> Previous page is ready");
            }
        });
    }

    boolean stop_running = false;

    public Thread SaveZoomablePage(final int requested_index) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
//                if (stop_running) {
//                    return;
//                }
                int prev_no = requested_index - 1 >= 0 ? requested_index - 1 : 0;
                File page_file = create_new_ZoomableFile(tmp_book + "_dir", requested_index);
                File prev_page_file = create_new_ZoomableFile(tmp_book + "_dir", prev_no);
                if (prev_page_file.exists()) {
                    Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file exists");
                    if (prev_page_file.delete()) {
                        Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file exist and it has been deleted");
                    }
                }
//                externalPdfAdapter.is_waiting_for_image=true;
                Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file is not null");
                File file = PdfUtils.SaveZoomablePdfPage_to(FlipBooKActivity.this, pdf_path, page_file.getPath(), requested_index);
                Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + (file != null ? "pdf_page of path " + file.getPath() + " is saved" : "unable save pdf page"));
                Runnable runnable1 = new Runnable() {
                    public void run() {
                        if (externalPdfAdapter.is_waiting_for_image) {
                            File page_file = create_new_ZoomableFile(tmp_book, requested_index);
                            ((ImageView) current_page.findViewById(R.id.z_image)).setImageBitmap(BitmapFactory.decodeFile(page_file.getPath()));
                            externalPdfAdapter.is_waiting_for_image = false;
                            mAdapterFlipView.hideFlipView();
                        }
                    }
                };
                runOnUiThread(runnable1);
            }
        });
    }

    public boolean is_for_jump;

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

    public boolean is_pdf_from_otherApp;
    public boolean is_pdf_from_device;
    public boolean is_pics_from_device;
    public String tmp_book;
    public String pdf_path;

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

    //Activity CallBacks...

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        init();
//        PostDisplayNavigations(2000);
        Log.i(TAG, "onCreate: after PostDisplayNavigations");
        if (View_Utils.isSoftNavAvailable(FlipBooKActivity.this)) {
            setUINavigationVisibilityListeneners();
        }
        Log.i(TAG, "onCreate: after setUINavigationVisibilityListeners");
        Log.i(TAG, "onCreate: after setUpBottomSheet");
        setUpAdapterFlipView();
        has_entered_onCreate = true;
    }

    int tmp_page_no;
    boolean is_from_pcker_result;
    boolean is_from_camera_result;
    public String pdf_name;

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

    private void addPagesOnActivityResult(@Nullable Intent data, int resultCode, int request_code) {
        assert data != null;
        if (request_code == REQUEST_CODE_TAKE_PHOTO) {
        } else if (request_code == START_PICKER_REQUEST_CODE) {

        }
    }

    public static final int START_PICKER_REQUEST_CODE = 8787;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        if (is_from_pdf()) {
            if (view.getId() == R.id.exit_book_btn) {
                onBackPressed();
            } else {
                Toast.makeText(this, "This features are not available for device books yet", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        switch (view.getId()) {
            case R.id.more_btn:
                //Todo
                // more operation
                break;
            case R.id.exit_book_btn:
                onBackPressed();
                break;
            case R.id.float_edit_btn:
                int current_page = mAdapterFlipView.getSelectedItemPosition();
                if (current_page > 0 && current_page < flip_list.size() - 1) {
                    startEditing();
                } else if (current_page == 0) {
                    EditCover();
                }
                break;
            case R.id.float_crop:
                // crop_ page image
                break;
            case R.id.float_delete:
                //delete page_image
                break;
            case R.id.lock_btn:
                StopHidingNavigations();
                PopupMenu menu = new PopupMenu(this, view);
                menu.inflate(R.menu.speed_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.fast:
                                app_preferences.saveFlipSpeed(current_book, FAST);
                                break;
                            case R.id.v_fast:
                                app_preferences.saveFlipSpeed(current_book, VERY_FAST);
                                break;
                            case R.id.normal:
                                app_preferences.saveFlipSpeed(current_book, NORMAL);
                                break;
                            case R.id.slow:
                                app_preferences.saveFlipSpeed(current_book, SLOW);
                                break;
                        }
                        setFlipSpeeds();
                        return false;
                    }
                });
                menu.show();
                break;
            case R.id.play_pdf:
                StopHidingNavigations();
                try {
                    if (!is_pdf_from_otherApp) {
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
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "pdf creation failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.remove_a_page:
                if (is_pics_from_device) {
                    Toast.makeText(this, "can't remove any pages for gallery", Toast.LENGTH_SHORT).show();
                    return;
                }
                StopHidingNavigations();
                page_table.RemoveAPage(this, current_page_index);
                break;
            case R.id.add_btn:
                if (is_pics_from_device) {
                    Toast.makeText(this, "can't add any pages for gallery", Toast.LENGTH_SHORT).show();
                    return;
                }
                StopHidingNavigations();
                header.setVisibility(View.VISIBLE);
                ShowDefault_navs(View.VISIBLE);
                SwitchImportBtnDisplay();
                break;
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

    private void EditCover() {

    }

    public void SwitchImportBtnDisplay() {
        if (gallery_btn.isShown()) {
            HideImportDialog();
        } else {
            showImportDialog();
        }
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        mAdapterFlipView.MakeFlipVisible();
//        mAdapterFlipView.ShowFlipViewForRefresh();
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
//                        RefreshPage();
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
//                        RefreshPage();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
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
    // Handling UI Visibilities.....

    public void ShowOnlyOriginalNavs() {
        dont_hide = false;
        ShowHeader_nav();
        ShowDefault_navs(View.VISIBLE);
    }

    public void ShowDefault_navs(int visibility) {
        View_Utils.ShowView_with_ZoomOut(tool_panel);
        play_pdf_btn.setVisibility(visibility);
        add_btn.setVisibility(visibility);
        lock_btn.setVisibility(visibility);
        more_btn.setVisibility(visibility);
    }

    boolean has_entered_onCreate = false;

    public void adjustToolMarginsIfSoftNav(ViewGroup tool_view) {
        if (View_Utils.isSoftNavAvailable(this)) {
            LinearLayout.MarginLayoutParams LlayoutParams = null;
            FrameLayout.LayoutParams FlayoutParams = null;
            if (tool_view instanceof LinearLayout) {
                LlayoutParams = (LinearLayout.LayoutParams) tool_view.getLayoutParams();
            } else if (tool_view instanceof FrameLayout) {
                FlayoutParams = (FrameLayout.LayoutParams) tool_view.getLayoutParams();
            }
            Context context = tool_view.getContext();
            float nav_height = View_Utils.getNavHight(context);
            if (!View_Utils.isLandScape(this)) {
                if (tool_view.equals(tool_panel)) {
                    assert LlayoutParams != null;
                    LlayoutParams.bottomMargin = (int) nav_height;
                } else if (tool_view.equals(header)) {
                    assert FlayoutParams != null;
                    FlayoutParams.bottomMargin = (int) nav_height;
                }
            } else {
                if (tool_view.equals(tool_panel)) {
                    assert LlayoutParams != null;
                    LlayoutParams.rightMargin = (int) nav_height;
                } else if (tool_view.equals(header)) {
                    assert FlayoutParams != null;
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


    public void HideNavigations() {
        View_Utils.hideSystemUI(this);
        visible = false;
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

    public void StopHidingSeekBar() {
        if (seekHandler != null && seekRunnable != null) {
            seekHandler.removeCallbacks(seekRunnable);
        }
    }

    void ShowView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    void HideView(View v) {
        v.setVisibility(GONE);
    }

    public void DisplayNavigation() {
//        View_Utils.hideSystemUI(this);
        View_Utils.showSystemUI(this);
        if (!header.isShown() || !tool_panel.isShown()) {
            ShowHeader_nav();
            ShowView(indicatorSeekBar);
            ShowView(tool_panel);
        }
    }

    public void HideHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        HideView(header);
    }

    public void HideFooter_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        tool_panel.setVisibility(GONE);
    }

    public void ShowHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        header.setVisibility(View.VISIBLE);
    }

    public boolean is_nav_visible() {
        return header.getVisibility() == View.VISIBLE;
    }

    Handler seekHandler;
    Runnable seekRunnable;

    public void PostHideSeekBar(int duration) {
        if (seekHandler != null && seekRunnable != null) {
            seekHandler.removeCallbacks(seekRunnable);
        }
        seekHandler = new Handler();
        View_Utils.ShowView_with_ZoomOut((ViewGroup) indicatorSeekBar.getParent());
        seekRunnable = new Runnable() {
            @Override
            public void run() {
                if (!is_seeking) {
                    if (mAdapterFlipView.is_to_flip()) {
                        View_Utils.hideView_with_ZoomIn((ViewGroup) indicatorSeekBar.getParent());
                    } else {
                        indicatorSeekBar.setVisibility(GONE);
                    }
                }
            }
        };
        seekHandler.postDelayed(seekRunnable, duration);

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
        is_revealed = false;
    }

    //Getters...
    public Handler getHandler() {
        return handler;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }

    private com.github.clans.fab.FloatingActionButton initFab(int id) {
        com.github.clans.fab.FloatingActionButton btn = findViewById(id);
        btn.setOnClickListener(this);
        return btn;
    }

    private String getLargeTextSample(int index) {
        return index + " There are 3 reasons why i should never quit:\n" +
                "1. My Parents.\n" +
                "2.Un-married wife and unborn kids.\n" +
                "3.Those who said i can't do it\n" +
                "My Parents:\n" +
                "Waking up everyday seen the struggle in my parent's face, knowing they have nothing and trying to give us everything.\n" +
                "-> I always promise myself that i will never let them down.\n" +
                "-> I promised them a better life they deserved.\n" +
                "My Family:\n" +
                "Although i am not married and i am not even close, but i always see(Imagine) the smiles on my bride, and  my kids's faces.\n" +
                "->I will make sure that smile will never turn to sadness, disappointment and regret.\n" +
                "-> I will make sure they are all proud of whoever i was.\n" +
                "Those who said i can't :\n" +
                "-> I want to make sure i am the last person they will said that to. The reason why they said i can't is because they have never seen someone doing that.\n" +
                "-> I will make sure they witness me doing it and doing it really well, So they won't have any gut to tell someone else that it's impossible.";
    }


    public void showFlipImageDragBonds(ViewGroup viewGroup) {
        //5,red
        viewGroup.findViewById(R.id.v_top).setVisibility(View.VISIBLE);
        viewGroup.findViewById(R.id.v_start).setVisibility(View.VISIBLE);
        viewGroup.findViewById(R.id.v_end).setVisibility(View.VISIBLE);
        viewGroup.findViewById(R.id.v_bottom).setVisibility(View.VISIBLE);
    }

    public void setZoomBondWidthAndColor(ViewGroup viewGroup) {
        int top_bottom_height = (int) View_Utils.density_to_px(FlipBooKActivity.this, 2f);
        int left_right_width = (int) View_Utils.density_to_px(FlipBooKActivity.this, 2f);
        //size
        viewGroup.findViewById(R.id.v_top).getLayoutParams().height = top_bottom_height;
        viewGroup.findViewById(R.id.v_bottom).getLayoutParams().height = top_bottom_height;
        viewGroup.findViewById(R.id.v_start).getLayoutParams().width = left_right_width;
        viewGroup.findViewById(R.id.v_end).getLayoutParams().width = left_right_width;
        //color
        viewGroup.findViewById(R.id.v_top).setBackgroundResource(R.color.accentLight);
        viewGroup.findViewById(R.id.v_bottom).setBackgroundResource(R.color.accentLight);
        viewGroup.findViewById(R.id.v_start).setBackgroundResource(R.color.accentLight);
        viewGroup.findViewById(R.id.v_end).setBackgroundResource(R.color.accentLight);
    }

    public void setDragBondWidthAndColor(ViewGroup viewGroup) {
        int top_bottom_height = (int) View_Utils.density_to_px(FlipBooKActivity.this, 5f);
        int left_right_width = (int) View_Utils.density_to_px(FlipBooKActivity.this, 5f);
        //size
        viewGroup.findViewById(R.id.v_top).getLayoutParams().height = top_bottom_height;
        viewGroup.findViewById(R.id.v_bottom).getLayoutParams().height = top_bottom_height;
        viewGroup.findViewById(R.id.v_start).getLayoutParams().width = left_right_width;
        viewGroup.findViewById(R.id.v_end).getLayoutParams().width = left_right_width;
        //color
        viewGroup.findViewById(R.id.v_top).setBackgroundResource(R.color.red);
        viewGroup.findViewById(R.id.v_bottom).setBackgroundResource(R.color.red);
        viewGroup.findViewById(R.id.v_start).setBackgroundResource(R.color.red);
        viewGroup.findViewById(R.id.v_end).setBackgroundResource(R.color.red);
    }

    public void HideFlipImageDragBonds(ViewGroup viewGroup) {
        viewGroup.findViewById(R.id.v_top).setVisibility(GONE);
        viewGroup.findViewById(R.id.v_start).setVisibility(GONE);
        viewGroup.findViewById(R.id.v_end).setVisibility(GONE);
        viewGroup.findViewById(R.id.v_bottom).setVisibility(GONE);
    }

    public void showRecyclerzoomIndicatorBonds(ViewGroup viewGroup) {
        viewGroup.findViewById(R.id.recycler_zoom_indicator).setVisibility(View.VISIBLE);
    }

    public void HideRecyclerzoomIndicatorBonds(ViewGroup viewGroup) {
        viewGroup.findViewById(R.id.recycler_zoom_indicator).setVisibility(GONE);
    }

    final String msg = "dragDebug";

    @Override
    public boolean onDrag(View highlighted_view, DragEvent event) {
        int action = event.getAction();
        CharSequence dragData;
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    if (highlighted_view instanceof ImageButton) {
                        highlighted_view.getBackground().setColorFilter(LIGTH_YELLOW, PorterDuff.Mode.SRC_IN);
                        highlighted_view.invalidate();
                        Log.i("drag_imageDebug", "started for image button");
                    }
                    return true;
                } else {
                    Log.d(msg, "Can't accept");
                    return false;
                }
            case DragEvent.ACTION_DRAG_ENTERED:
//                Log.d(msg, "ACTION_DRAG_ENTERED");

                if (highlighted_view == float_crop) {
                    highlighted_view.getBackground().setColorFilter(LIGTH_GREEN, PorterDuff.Mode.SRC_IN);
                    highlighted_view.invalidate();
                } else if (highlighted_view == float_delete) {
                    highlighted_view.getBackground().setColorFilter(LIGTH_RED, PorterDuff.Mode.SRC_IN);
                    highlighted_view.invalidate();
                }
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                if (highlighted_view instanceof ImageButton) {
                    highlighted_view.getBackground().setColorFilter(LIGTH_YELLOW, PorterDuff.Mode.SRC_IN);
                    highlighted_view.invalidate();
                }
                return true;
            case DragEvent.ACTION_DROP:
                final ClipData.Item item = event.getClipData().getItemAt(0);
                dragged_view = (View) event.getLocalState();
                if (!(highlighted_view instanceof ImageButton)) {
                    Toast.makeText(this, "you just drop data to bucket", Toast.LENGTH_SHORT).show();
                } else {
                    if (highlighted_view == float_delete) {
                        new AlertDialog.Builder(this).setTitle("Warning!!!").setMessage("Are you you want to Empty this page?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String path = item.getText().toString();
                                        if (path.equals(flip_list.get(current_page_index).getImage_path())) {
                                            MyBook_fragment.page_table.deletePictureFromThisPage(FlipBooKActivity.this,
                                                    flip_list.get(current_page_index));
                                            if (new File(path).delete()) {
                                                Toast.makeText(FlipBooKActivity.this, path + " deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(FlipBooKActivity.this, path + " delete failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else if (highlighted_view == float_crop) {
                        startEditing();
                    }
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
//                Log.d(msg, "ACTION_DRAG_ENDED");
                if (highlighted_view instanceof ImageButton) {
                    highlighted_view.getBackground().clearColorFilter();
                    highlighted_view.invalidate();
                }
                float_crop.setVisibility(GONE);
                float_delete.setVisibility(GONE);
                if (event.getResult()) {
                    Toast.makeText(this, "The drop was handled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_SHORT).show();
                }
                return true;
            // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

    private static final int RESULT_FINE = 44444;

    private void startEditing() {
        File tmp_file = new File(getCacheDir(), "editing_img.jpg");
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra("pic", flip_list.get(current_page_index).getImage_path());
        startActivity(intent);
    }

    @Override
    public void onBookChanged(String book_name, List<PAGES_Table.PageInfo> PageInfos, boolean is_removed) {
        final File pdf_file = new File(getPdfsFilePath() + File.separator + current_book + ".pdf");
        final LinkedList<flip_model> flip_list = getFlip_list();
        if (pdf_file.exists() && flip_list.size() > 2) {
            //noinspection ResultOfMethodCallIgnored
            pdf_file.delete();
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


    }
}

