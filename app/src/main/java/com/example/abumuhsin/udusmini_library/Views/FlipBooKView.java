package com.example.abumuhsin.udusmini_library.Views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.adapters.FlipBookView_adapter;
import com.example.abumuhsin.udusmini_library.adapters.FlipPdfAdapter2;
import com.example.abumuhsin.udusmini_library.models.Book;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.example.adaptablecurlpage.flipping.utils.DrawingState;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.FAST;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.NORMAL;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.SLOW;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.VERY_FAST;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getPdfsFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ShowSpeedDialog;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ToggleScreenStatus;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.play_pdf;


public class FlipBooKView extends FrameLayout implements View.OnClickListener, AdapterPageFlipView.OnPageFlippedListener,
        OnSeekChangeListener, PageBottomControlPanel.OnControlButtonListener, PageHeaderView.OnControlButtonListener, FlipBookView_adapter.FlipBookAdapterCallbacks {

    public static final String TAG = "SampleActivityDebug";
    public static final int AUTO_FLIP_DURATION = 6000;
    public static int AUTO_FLIP_INTERVAL = 4000;
    private FrameLayout header;
    public AdapterPageFlipView mAdapterFlipView;
    public BaseAdapter flipAdapter;
    View import_dialog;
    private com.github.clans.fab.FloatingActionButton camera_btn, gallery_btn, pdf_btn;
    public View current_page;
    public int current_page_index;
    private LinearLayout tool_panel;
    private Handler handler;
    private Runnable runnable;
    private App_Preferences app_preferences;
    public boolean dont_hide;
    public String book_title = "";
    private IndicatorSeekBar indicatorSeekBar;
    public boolean is_seeking;
    PageBottomControlPanel pageBottomControlPanel;
    PageHeaderView pageHeaderView;
    ActionBar actionBar;
    public int prev_index;
    Handler handing_auto_refresh;
    Runnable auto_refresh_runnable;
    public boolean is_image_seeking = false;
    //    public boolean page_flipped = false;
//    public boolean is_first_time;
//    public boolean is_fast_flip = false;
    public boolean is_view_fully_loaded = true;
    boolean is_holded = false;
    boolean is_long_ready = false;
    boolean has_entered_onCreate = false;
    public String pdf_path;
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
    //    boolean is_go_to = false;
    AppCompatActivity activity;
    private LinkedList<Flip_model> flip_list;

//    public FlipBooKView(@NonNull AppCompatActivity activity) {
//        super(activity.getApplicationContext());
//        this.activity = activity;
//        init();
//    }

    public FlipBooKView(@NonNull Context context) {
        super(context);
        if (!(context instanceof AppCompatActivity))
            return;
        this.activity = (AppCompatActivity) context;
        init();
    }

    public FlipBooKView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof AppCompatActivity))
            return;
        this.activity = (AppCompatActivity) context;
        init();
    }

    public FlipBooKView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof AppCompatActivity))
            return;
        this.activity = (AppCompatActivity) context;
        init();
    }


    //Initializations...
    private void init() {
        inflate(activity, R.layout.sample_main, this);
        dont_hide = false;
        app_preferences = new App_Preferences(activity.getApplicationContext());
        initViews();
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    private void initViews() {
        pageHeaderView = findViewById(R.id.p_header);
        Log.e(TAG, "the pageHeaderView is " + pageHeaderView);
        activity.setSupportActionBar(pageHeaderView.getToolbar());
        actionBar = activity.getSupportActionBar();
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

    // Main Activity Callbacks...

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public void setBookList(LinkedList<Flip_model> flip_list, Book book) {
        String title = book_title.length() >= 20 ? book_title.substring(0, 20) + "..." : book_title;
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        this.flip_list = flip_list;
        this.book_title = book.getBook_title();
        Log.i(TAG, "setUpAdapterFlipView: starts");
        mAdapterFlipView = findViewById(R.id.flip);
        app_preferences = new App_Preferences(activity.getApplicationContext());
        ToggleScreenStatus(activity, app_preferences.getScreenState(book_title));
        pageBottomControlPanel.setB_screenColor(app_preferences.getScreenState(book_title));
        setFlipSpeeds();
        prev_index = app_preferences.getPageNo(book_title);
        mAdapterFlipView.setOnPageFlippedListener(this);
        if (book.isPdf())
            flipAdapter = new FlipPdfAdapter2(getContext(), flip_list, this,book.getPdf_path());
        else
            flipAdapter = new FlipBookView_adapter(this, flip_list, this);
        if (flipAdapter instanceof FlipBookView_adapter)
            ((FlipBookView_adapter) flipAdapter).setBook_cover(book.getBook_cover());
        mAdapterFlipView.setAdapter(flipAdapter);
        Log.i(TAG, "setUpAdapterFlipView: ends");
    }

    public void RefreshBookData() {
        flipAdapter.notifyDataSetChanged();
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        has_entered_onCreate = true;
        PostDisplayNavigations(2000);
        ResumePreviousIndex();
    }

    public void ResumePreviousIndex() {
//        app_preferences = new App_Preferences(activity);
        prev_index = app_preferences.getPageNo(book_title);
    }

    public void onPause() {
        has_entered_onCreate = false;
        app_preferences = new App_Preferences(activity);
        app_preferences.savePageNo(book_title, current_page_index);
        Log.i(TAG, "onPause");
    }

    public void onDestroy() {
        Log.i("pdf_bitmap", "onDestroy");
    }

    public void setFlipBookImportCallBacks(FlipBookImportCallBacks flipBookImportCallBacks) {
        this.flipBookImportCallBacks = flipBookImportCallBacks;
    }

    //  OnClick listener
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery_btn:
                StopHidingNavigations();
                HideImportDialog();
                flipBookImportCallBacks.onGalleryClick(view);
                break;
            case R.id.camera:
                HideImportDialog();
                flipBookImportCallBacks.onCameraClick(view);
                break;
            case R.id.pdf_btn:
                HideImportDialog();
                StopHidingNavigations();
                flipBookImportCallBacks.onPdfClick(view);
                break;
        }
    }

    //Click handlers...


    public boolean EnableVolumeFlip(KeyEvent event) {
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
                return false;
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
        int selected_speed = app_preferences.getFlipSpeed(book_title);
        ShowSpeedDialog(activity, selected_speed, new FlipBookActivityHelper.SpeedDialogListener() {
            @Override
            public void onSpeedDialogShown(int selected_speed, String selected_text) {
                app_preferences.saveFlipSpeed(book_title, selected_speed);
                setFlipSpeeds();
            }
        });
    }

    @Override
    public void onPdfClick(View v) {
        StopHidingNavigations();
        try {
            final File pdf_file = new File(getPdfsFilePath() + File.separator + book_title + ".pdf");
            if (pdf_file.exists()) {
                play_pdf(activity, pdf_file);
            } else {
                new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setMessage("For the first time, we will convert this book to pdf. " +
                                "After the successful conversion you can find the pdf in " + pdf_file.getPath())
                        .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                flipBookCallBacks.onCovertToPdfClicked(pdf_file);
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
            Toast.makeText(activity, "pdf creation failed", Toast.LENGTH_SHORT).show();
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
        app_preferences.saveScreenState(book_title, is_screen_Always);
        ToggleScreenStatus(activity, is_screen_Always);
    }


    @Override
    public void onAddClick(View v) {
        StopHidingNavigations();
        header.setVisibility(View.VISIBLE);
        flipBookImportCallBacks.onAddBtnClicked(v);
    }

    @Override
    public void onRemoveClick(final View v) {
        final View view = v;
        StopHidingNavigations();
        new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage("Are you sure you want to delete this file???")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flipBookCallBacks.onPageDeleteConfirmed(view);
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
        flipBookCallBacks.onEditBtnClick(v);
    }

    @Override
    public void onMoreClick(View v) {
        //Todo
        // more operation
        flipBookCallBacks.onMoreBtnClick(v);
    }

    @Override
    public void onAnyControlClick(View v) {
        StopHidingNavigations();
    }

    @Override
    public void onPageClicked(int pos) {

    }

    @Override
    public void onPageZoomed(int current_page_index) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPageUnZoomed(int current_page_index) {
        boolean is_screen_always = app_preferences.getScreenState(book_title);
        ToggleScreenStatus(activity, is_screen_always);
    }

    @Override
    public void onPageFullyLoaded(Bitmap resource, int current_page_index) {

    }

    @Override
    public void onLastPageBtnClicked(View v) {
        flipBookImportCallBacks.onAddBtnClicked(v);
    }

    // Progress bar callbacks
    @Override
    public void onSeeking(SeekParams seekParams) {
        if (seekParams.fromUser) {
            is_seeking = true;
            if (seekParams.progress > 0) {
                final int seek_position = seekParams.progress - 1;
                flipBookCallBacks.onSeeking(seek_position);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        is_seeking = true;
        flipBookCallBacks.onSeekingStarted(seekBar);
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        is_seeking = false;
        flipBookCallBacks.onSeekingStopped(seekBar);
    }

    public static class Page_info {
        String page_progress;
        int page_size;

        public Page_info(String page_progress, int page_size) {
            this.page_progress = page_progress;
            this.page_size = page_size;
        }
    }

    // PageFlipView CallBacks
    @Override
    public void onPageFlipped(View page, int page_no, long id) {
        Page_info page_info = flipBookCallBacks.onPageFlipped(page, page_no, id);
        pageBottomControlPanel.setProgress(page_info.page_progress);
//        is_go_to = false;
//        page_flipped = true;
        current_page = page;
        current_page_index = page_no;
        prev_index = page_no;
        if (!is_seeking) {
            indicatorSeekBar.setMax(page_info.page_size);
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
        flipBookCallBacks.onPageFastFlipStarted(page, page_no, is_forward);
    }


    @Override
    public void onFastFlippingEnded(View page, int page_no, boolean is_forward) {
        flipBookCallBacks.onPageFastFlipEnded(page, page_no, is_forward);
    }

    @Override
    public void onRestorePage(int page_no, boolean is_forward) {
//        page_flipped = false;
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
        View_Utils.hideSystemUI(activity);
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

    public void DisableFuntions(boolean is_pdf) {
        if (is_pdf) {
            pageBottomControlPanel.DisableControls(is_pdf);
        }
        pageHeaderView.DisableFuntions();
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
        Toast.makeText(activity, "You have reached the end of this handout @ pos " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserFinger_isDown(View page, int page_no) {
        CancelAutoRefresh();
    }

    @Override
    public void onUserFinger_isUp(View page, final int page_no) {
//        if (page_flipped) {
//            mAdapterFlipView.is_selected_view = true;
//
//            Log.i("pdf_bitmap", "the up action does flip the page");
//            page_flipped = false;
//        } else {
//            Log.i("pdf_bitmap", "the up action does not flip the page");
//            page_flipped = true;
//        }
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

    //    public FlipBookView_adapter getFlipBookView_adapter() {
//        return flipBookView_adapter;
//    }
    public BaseAdapter getFlipBookView_adapter() {
        return flipAdapter;
    }

    public int getNormalSpeed() {
        int speed = app_preferences.getFlipSpeed(book_title);
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
        int speed = app_preferences.getFlipSpeed(book_title);
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

    public Handler getHandlerC() {
        return handler;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    //...........................Helpers....................

    // Handling UI Visibilities.....

    public void adjustToolMarginsIfSoftNav(ViewGroup tool_view) {
        if (View_Utils.isSoftNavAvailable(activity)) {
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
            if (!View_Utils.isLandScape(activity)) {
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
        View decorView = activity.getWindow().getDecorView();
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
        View_Utils.hideSystemUI(activity);
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
        View_Utils.showSystemUI(activity);
        if (!header.isShown() || !tool_panel.isShown()) {
            ShowHeader_nav();
            pageBottomControlPanel.show();
//            ShowView(tool_panel);
        }
    }

    public void HideHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out));
        HideView(header);
    }

    public void HideFooter_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out));
//        tool_panel.setVisibility(GONE);
        pageBottomControlPanel.hide();
    }

    public void ShowHeader_nav() {
        header.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
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

    //  Refresh...
    public void AutoRefreshAfter(int duration) {
        CancelAutoRefresh();
        handing_auto_refresh = new Handler();
        auto_refresh_runnable = new Runnable() {
            @Override
            public void run() {
                mAdapterFlipView.RefreshFlip();
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

    FlipBookCallBacks flipBookCallBacks;
    FlipBookImportCallBacks flipBookImportCallBacks;

    public void setFlipBookCallBacks(FlipBookCallBacks flipBookCallBacks) {
        this.flipBookCallBacks = flipBookCallBacks;
    }

    public interface FlipBookImportCallBacks {

        void onGalleryClick(View view);

        void onCameraClick(View view);

        void onPdfClick(View view);

        void onAddBtnClicked(View v);
    }

    public interface FlipBookCallBacks {
        void onPageDeleteConfirmed(View v);

        void onEditBtnClick(View v);

        void onSeeking(int seek_position);

        void onSeekingStarted(IndicatorSeekBar seekBar);

        void onSeekingStopped(IndicatorSeekBar seekBar);

        //Pdf must implement
        Page_info onPageFlipped(View page, int page_no, long id);

        void onPageFastFlipStarted(View page, int page_no, boolean is_forward);

        void onPageFastFlipEnded(View page, int page_no, boolean is_forward);

        void onCovertToPdfClicked(File pdf_file);

        void onMoreBtnClick(View v);
    }

}

