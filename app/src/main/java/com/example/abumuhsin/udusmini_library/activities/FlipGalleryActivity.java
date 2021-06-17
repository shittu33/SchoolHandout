
package com.example.abumuhsin.udusmini_library.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.PageBottomControlPanel;
import com.example.abumuhsin.udusmini_library.Views.PageHeaderView;
import com.example.abumuhsin.udusmini_library.adapters.FlipGalleryBook_adapter;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.tasks.Convert_to_pdfTask;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import static android.view.View.GONE;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.BOOK_NAME_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.IMAGES_EXTRA;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.FAST;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.NORMAL;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.SLOW;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.VERY_FAST;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getPdfsFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ShowSpeedDialog;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ToggleScreenStatus;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.play_pdf;
import static com.example.mysqlitedbconnection.csv.Constants.COVER_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;


public class FlipGalleryActivity extends AppCompatActivity implements AdapterPageFlipView.OnPageFlippedListener,
        OnSeekChangeListener, PageBottomControlPanel.OnControlButtonListener, PageHeaderView.OnControlButtonListener {
    public static final String TAG = "SampleActivityDebug";
    public static final int AUTO_FLIP_DURATION = 6000;
    public static int AUTO_FLIP_INTERVAL = 4000;
    private FrameLayout header;
    public AdapterPageFlipView mAdapterFlipView;
    public FlipGalleryBook_adapter flipGalleryBookAdapter;
    private LinkedList<Flip_model> flip_list;
    public View current_page;
    public int current_page_index;
    private LinearLayout tool_panel;
    private Handler handler;
    private Runnable runnable;
    private App_Preferences app_preferences;
    public boolean dont_hide;
    public String current_book="";
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
    public String pdf_name;
    Handler long_press_handler = new Handler();
    Runnable long_press_run = new Runnable() {
        @Override
        public void run() {
            pageBottomControlPanel.toggleAutolay();
            is_long_ready = true;
        }
    };
    boolean is_go_to = false;

    // Main Activity Callbacks...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        init();
        Log.i(TAG, "onCreate: after PostDisplayNavigation");
        if (View_Utils.isSoftNavAvailable(FlipGalleryActivity.this)) {
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
        //TODO all the way from the device gallery, how can i help you???
        ArrayList<String> gallery_images = getIntent().getStringArrayListExtra(IMAGES_EXTRA);
        if (gallery_images != null) {
            flip_list.clear();
            for (String page_path : gallery_images) {
                flip_list.add(new Flip_model(SINGLE_PAGE_TYPE, page_path, SINGLE_PAGE));
            }
        } else {
            flip_list.add(new Flip_model(SINGLE_PAGE_TYPE, "no image", SINGLE_PAGE));
        }
        Log.i(TAG, "onResume()-> is_pics_from_device: adding " + flip_list.get(flip_list.size() - 1).getImage_path());
        prev_index = app_preferences.getPageNo(current_book);
        //Realise that i have to post a new selection to avoid any problem, because the render is not able to draw an empty bitmap.
        //On second trial. i realise that there is also problem is from glide, the activity context passed to it return null on 2nd resume
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    flipGalleryBookAdapter.notifyDataSetChanged();
                    Log.i(TAG, "onResume()-> is_pics_from_device: can draw");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResume()-> is_pics_from_device: can't draw");
                }
            }
        });
        Log.i(TAG, "onResume()-> is_pics_from_device: Adapter notified");
        PostDisplayNavigations(2000);
    }

    //Initializations...
    private void init() {
        dont_hide = false;
        initViews();
        current_book = getIntent().getStringExtra(BOOK_NAME_EXTRA);
        String title = current_book.length() >= 20 ? current_book.substring(0, 20) + "..." : current_book;
        if (actionBar!=null) {
            actionBar.setTitle(title);
        }
    }

    private void initViews() {
        pageHeaderView = findViewById(R.id.p_header);
        pageHeaderView.DisableFuntions();
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
        indicatorSeekBar =pageBottomControlPanel.getP_seeSeekBar() ;
        //Listen for drag...
        indicatorSeekBar.setOnSeekChangeListener(this);
        Log.i(TAG, "initViews: ends");
    }

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
        mAdapterFlipView = findViewById(R.id.flip);
        app_preferences = new App_Preferences(this.getApplicationContext());
        ToggleScreenStatus(this,app_preferences.getScreenState(current_book));
        pageBottomControlPanel.setB_screenColor(app_preferences.getScreenState(current_book));
        setFlipSpeeds();
        flip_list = new LinkedList<>();
        prev_index = app_preferences.getPageNo(current_book);
        flip_list.add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
        Log.i(TAG, "setUpAdapterFlipView-> is_pics_from_device: first_page added for device gallery Flip");
        mAdapterFlipView.setOnPageFlippedListener(this);
        flipGalleryBookAdapter = new FlipGalleryBook_adapter(this, flip_list);
        Log.i(TAG, "setUpAdapterFlipView: adapter is settled");
        mAdapterFlipView.setAdapter(flipGalleryBookAdapter);
        Log.i(TAG, "setUpAdapterFlipView: ends");
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
                                new Convert_to_pdfTask(FlipGalleryActivity.this, null, pdf_file).execute();
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
        ToggleScreenStatus(this,is_screen_Always);
    }

    @Override
    public void onAddClick(View v) {
        Toast.makeText(this, "can't add any pages for gallery", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveClick(View v) {
        Toast.makeText(this, "can't remove any pages for gallery", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(View v) {

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
        mAdapterFlipView.MakeAdapterVisible();
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        is_seeking = false;
        is_image_seeking = false;
        flipGalleryBookAdapter.notifyDataSetChanged();
    }

    // PageFlipView CallBacks
    @Override
    public void onPageFlipped(View page, int page_no, long id) {
        int end_no = flipGalleryBookAdapter.getCount()>2? flipGalleryBookAdapter.getCount()-2: flipGalleryBookAdapter.getCount();
        String no_txt = page_no+1 + "/" + end_no;
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
        View_Utils.hideSystemUI(FlipGalleryActivity.this);
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


    public void HideNavigations() {
        View_Utils.hideSystemUI(this);
        boolean visible = false;
        HideHeader_nav();
        HideFooter_nav();
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

    public boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }

}

