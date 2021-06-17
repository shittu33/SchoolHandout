
package com.example.abumuhsin.udusmini_library.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.FlipBooKView;
import com.example.abumuhsin.udusmini_library.Views.PageBottomControlPanel;
import com.example.abumuhsin.udusmini_library.Views.PageHeaderView;
import com.example.abumuhsin.udusmini_library.adapters.FlipPdfAdapter;
import com.example.abumuhsin.udusmini_library.models.Book;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.tasks.FlipPdfBackLoaderTask;
import com.example.abumuhsin.udusmini_library.tasks.FlipPdfForwardLoaderTask;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.PdfPagesNoGenerator;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.example.adaptablecurlpage.flipping.utils.DrawingState;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

import static android.view.View.GONE;
import static com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment.PDF_BOOK_NAME_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.PDFBook_fragment.PDF_MSG_EXTRA;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.FAST;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.NORMAL;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.SLOW;
import static com.example.abumuhsin.udusmini_library.utils.App_Preferences.VERY_FAST;
import static com.example.abumuhsin.udusmini_library.utils.FlipBookActivityHelper.ToggleScreenStatus;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_File_for_ActivePdf;
import static com.example.abumuhsin.udusmini_library.utils.PdfUtils.create_new_ZoomableFile;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;


public class TFlipPdfActivity extends AppCompatActivity implements AdapterPageFlipView.OnPageFlippedListener,
        OnSeekChangeListener, PageBottomControlPanel.OnControlButtonListener, PageHeaderView.OnControlButtonListener {

    public static final String TAG = "SampleActivityDebug";
    public static final int AUTO_FLIP_DURATION = 6000;
    public static int AUTO_FLIP_INTERVAL = 4000;
    private FrameLayout header;
    public AdapterPageFlipView mAdapterFlipView;
    public FlipPdfAdapter flipPdfAdapter;
    private LinkedList<Flip_model> flip_list;
    public View current_page;
    public int current_page_index;
    private LinearLayout tool_panel;
    private Handler handler;
    private Runnable runnable;
    private App_Preferences app_preferences;
    public boolean dont_hide;
    public String current_book;
    private IndicatorSeekBar indicatorSeekBar;
    public boolean is_seeking;
    public Intent other_app_intent;
    PdfPagesNoGenerator pdfPagesNoGenerator;
    PageBottomControlPanel pageBottomControlPanel;
    PageHeaderView pageHeaderView;
    ActionBar actionBar;
    public int prev_index;
    Handler handing_auto_refresh;
    Runnable auto_refresh_runnable;
    public boolean is_image_seeking = false;
    public boolean page_flipped = false;
    boolean is_first_time;
    public boolean is_fast_flip = false;
    public boolean is_view_fully_loaded = true;
    boolean is_holded = false;
    boolean is_long_ready = false;
    boolean stop_running = false;
    boolean has_entered_onCreate = false;
    public boolean is_for_jump;
    public boolean is_pdf_from_otherApp;
    public boolean is_pdf_from_device;
    public String tmp_book;
    public String pdf_path;
    int tmp_page_no;
    public String pdf_name;
    Handler long_press_handler = new Handler();
    Runnable long_press_run = new Runnable() {
        @Override
        public void run() {
            pageBottomControlPanel.toggleAutolay();
            is_long_ready = true;
        }
    };
    Uri pdf_uri;
    public static final String PDF_PATH_DEBUG = "pdf_path_debug";
    public int page_count;
    public static final String PDF_LOAD_DEBUG = "pdf_debug";
    boolean is_go_to = false;
    private FlipBooKView flipBooKView;

    // Main Activity Callbacks...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flipBooKView = new FlipBooKView(this);
        setContentView(flipBooKView);
        init();
        Log.i(TAG, "onCreate: after PostDisplayNavigation");
        if (View_Utils.isSoftNavAvailable(TFlipPdfActivity.this)) {
            setUINavigationVisibilityListeneners();
        }
        Log.i(TAG, "onCreate: after setUINavigationVisibilityListeners");
        setUpAdapterFlipView();
        has_entered_onCreate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        flipBooKView.onResume();
        if (other_app_intent != null) {
//            GoToPage(prev_index, false, false);
        }
    }

    //Initializations...
    private void init() {
//        app_preferences = new App_Preferences(this);
        pdfPagesNoGenerator = new PdfPagesNoGenerator();
        dont_hide = false;
        is_pdf_from_otherApp = false;
        other_app_intent = getIntent();
        if (other_app_intent.getBooleanExtra(PDF_MSG_EXTRA, false)) {
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
            }
        }
        initViews();
        DefinePdfSource();
    }

    private void initViews() {
        mAdapterFlipView = flipBooKView.findViewById(R.id.flip);
        flipBooKView.setFlipBookCallBacks(flipBookCallBacks);
        Log.i(TAG, "initViews: ends");
    }

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
//        mAdapterFlipView = flipBooKView.mAdapterFlipView;
        mAdapterFlipView = flipBooKView.findViewById(R.id.flip);
        Log.i("AdapterPageFlipView ", "mAdapterFlipView is "+ mAdapterFlipView);
        app_preferences = new App_Preferences(this.getApplicationContext());
        setFlipSpeeds();
        flip_list = new LinkedList<>();
        if (other_app_intent != null) {
            ReceiveShared_pdfData(other_app_intent);
            String title = current_book.length() >= 20 ? current_book.substring(0, 20) + "..." : current_book;
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
        page_count = PdfUtils.getPdfPage_count(TFlipPdfActivity.this, pdf_path);
        Log.i("pdf_bitmap", "setUpAdapterFlipView: pageCount is" + page_count);
        for (int i = 0; i < page_count; i++) {
            String page_path = null;
            page_path = PdfUtils.getActivePdf_ImagePath(tmp_book, i);
            Log.i(PDF_PATH_DEBUG, "Path test -> the path is " + page_path);
            flip_list.add(new Flip_model(SINGLE_PAGE_TYPE, page_path, SINGLE_PAGE));
        }
        flipBooKView.setBookList(flip_list, new Book(current_book,true,pdf_path));
        Log.i(TAG, "setUpAdapterFlipView: ends");
        if (page_count <= 1) {
            View_Utils.showSystemUI(this);
        }
    }
    private void ReceiveShared_pdfData(Intent intent) {
        if (!is_pdf_from_device) {
            pdf_uri = intent.getData();
            if (pdf_uri != null) {
                pdf_path = copy_tmp_pdf(pdf_uri);
                Log.i(PDF_PATH_DEBUG, "pdf_path is " + pdf_path);
            } else {
                Log.i(PDF_PATH_DEBUG, "pdf_uri is empty ");
            }
        } else {
            pdf_path = intent.getStringExtra(PDF_BOOK_NAME_EXTRA);
        }
        page_count = PdfUtils.getPdfPage_count(TFlipPdfActivity.this, pdf_path);
        Log.i(PDF_PATH_DEBUG, "pdf path is " + pdf_path);
        tmp_book = new File(pdf_path).getName();
        current_book = tmp_book;
        Log.i(PDF_PATH_DEBUG, "flipBooKView is " + flipBooKView);
        flipBooKView.ResumePreviousIndex();
    }

    FlipBooKView.FlipBookCallBacks flipBookCallBacks = new FlipBooKView.FlipBookCallBacks() {
        @Override
        public void onPageDeleteConfirmed(View v) {
        }

        @Override
        public void onEditBtnClick(View v) {
        }

        @Override
        public void onSeeking(final int seek_position) {
            is_for_jump = false;
        }

        @Override
        public void onSeekingStarted(IndicatorSeekBar seekBar) {
            flipBooKView.StopHidingNavigations();
            flipBooKView.CancelAutoRefresh();
            flipBooKView.is_seeking = true;
            flipBooKView.mAdapterFlipView.MakeAdapterVisible();
        }

        @Override
        public void onSeekingStopped(IndicatorSeekBar seekBar) {
            flipBooKView.is_seeking = false;
            is_for_jump = true;
            GoToPage(seekBar.getProgress() - 1, true, false);
        }

        @Override
        public FlipBooKView.Page_info onPageFlipped(View page, int page_no, long id) {
            if (!is_seeking && !is_go_to) {
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
            current_page_index = page_no;
            current_page = page;
            int page_count = flip_list.size();
            int end_no = page_count > 0 ? page_count  : page_count;
            String prog_txt = page_no + 1 + "/" + end_no;
            return new FlipBooKView.Page_info(prog_txt, end_no);
        }

        @Override
        public void onPageFastFlipStarted(View page, int page_no, boolean is_forward) {
            Log.i(TAG, "onFastFlipStarted");
        }

        @Override
        public void onPageFastFlipEnded(View page, int page_no, boolean is_forward) {

        }

        @Override
        public void onCovertToPdfClicked(File pdf_file) {

        }

        @Override
        public void onMoreBtnClick(View v) {
            //Todo
        }
    };

    private void DefinePdfSource() {
        Log.i(TAG, "DefinePdfSource: begins");
        if (is_pdf_from_otherApp) {
            current_book = "other_app_pdf";
            Log.i(TAG, "DefinePdfSource: pdf is from other app");
        } else if (is_pdf_from_device) {
            File pdf_file = new File(other_app_intent.getStringExtra(PDF_BOOK_NAME_EXTRA));
            current_book = pdf_file.getName();
            Log.i(TAG, "DefinePdfSource()-> is_pdf_from_device: Book name is " + current_book);
        }
    }

    //Other Activity Callbacks...

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).is_pdf_thread_objectRunning()) {
            FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).JustStop_pdfThread();
        }
        if (FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).is_pdf_thread_objectRunning()) {
            FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).JustStop_pdfThread();
        }
        has_entered_onCreate = false;
        app_preferences = new App_Preferences(this);
        app_preferences.savePageNo(current_book, current_page_index);
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (FlipPdfForwardLoaderTask.get(this).is_pdf_thread_notNull() && FlipPdfForwardLoaderTask.get(this).is_pdf_thread_notNull()) {
            FlipPdfForwardLoaderTask.get(this).stop_pdfThread_and_delete_tmp_pages();
            FlipPdfBackLoaderTask.get(this).JustStop_pdfThread();
        }
        for (int i = 0; i < page_count; i++) {
            File page_file = create_new_ZoomableFile(tmp_book + "_dir", i);
            if (page_file.exists()) {
                if (page_file.delete()) {
                    Log.i("pdf_bitmap", "current zoomable page is deleted");
                }
            }
        }
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
        Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPdfClick(View v) {
        Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
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
                is_for_jump = true;
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
        is_for_jump = true;
        mAdapterFlipView.is_selected_view = true;
        GoToPage(seekBar.getProgress() - 1, true, false);
    }

    // PageFlipView CallBacks
    @Override
    public void onPageFlipped(View page, int page_no, long id) {
        int end_no = flipPdfAdapter.getCount() > 2 ? flipPdfAdapter.getCount() - 2 : flipPdfAdapter.getCount();
        String no_txt = page_no + 1 + "/" + end_no;
        pageBottomControlPanel.setProgress(no_txt);
        if (!is_seeking && !is_go_to) {
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
        is_for_jump = true;
        mAdapterFlipView.is_selected_view = true;
        GoToPage(page_no, true, true);
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
        View_Utils.hideSystemUI(TFlipPdfActivity.this);
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


//................PDF HELPERS...................

    public void GoToPage(final int page_no, final boolean is_tojump, final boolean is_forward) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                is_go_to = true;
                is_first_time = true;
                new Pdf_loaderTask(TFlipPdfActivity.this, page_no, is_tojump, is_forward).execute();
                if (FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).is_pdf_thread_objectRunning()) {
                    FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).JustStop_pdfThread();
                }
                if (FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).is_pdf_thread_objectRunning()) {
                    FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).JustStop_pdfThread();
                }
//                pdfPagesNoGenerator.flippedToView(page_no);
//                Integer[] pages = new Integer[pdfPagesNoGenerator.getBufferedPages().size()];
//                pdfPagesNoGenerator.getBufferedPages().toArray(pages);
//                new PdfPageSaverForwardTask(pdf_path, page_no).execute(pages);
                FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).LoadPdfToStorage(pdf_path, PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book + "_dir", page_no));
                FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).LoadPdfToStorage(pdf_path, PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book + "_dir", page_no), true);
                FlipPdfBackLoaderTask.get(TFlipPdfActivity.this).setOnPdfPageDownloadListener(new FlipPdfBackLoaderTask.OnPdfPageDownloadListener() {
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
                FlipPdfForwardLoaderTask.get(TFlipPdfActivity.this).setOnPdfPageDownloadListener(new FlipPdfBackLoaderTask.OnPdfPageDownloadListener() {
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
        TFlipPdfActivity context;

        Pdf_loaderTask(TFlipPdfActivity context, int requested_index, boolean is_tojump, boolean is_forward) {
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
                page_path = PdfUtils.getActivePdf_ImagePath( /*new File(tmp_book).getName()*/tmp_book, i);
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
                page_file = create_new_File_for_ActivePdf(tmp_book, index);
                PdfUtils.SavePdfPage_to(TFlipPdfActivity.this, pdf_path, page_file.getPath(), index);
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
                page_file = create_new_File_for_ActivePdf(tmp_book, index);
                PdfUtils.SavePdfPage_to(TFlipPdfActivity.this, pdf_path, page_file.getPath(), index);
                Log.i(PDF_LOAD_DEBUG, "SavePrevious-> Previous page is ready");
            }
        });
    }


    public Thread SaveZoomablePage(final int requested_index) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                int prev_no = requested_index - 1 >= 0 ? requested_index - 1 : 0;
                File page_file = create_new_ZoomableFile(tmp_book + "_dir", requested_index);
                File prev_page_file = create_new_ZoomableFile(tmp_book + "_dir", prev_no);
                if (prev_page_file.exists()) {
                    Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file exists");
                    if (prev_page_file.delete()) {
                        Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file exist and it has been deleted");
                    }
                }
                Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + "prev_page file is not null");
                File file = PdfUtils.SaveZoomablePdfPage_to(TFlipPdfActivity.this, pdf_path, page_file.getPath(), requested_index);
                Log.i(PDF_LOAD_DEBUG, "SaveZoomablePage-> " + (file != null ? "pdf_page of path " + file.getPath() + " is saved" : "unable save pdf page"));
//                Runnable runnable1 = new Runnable() {
//                    public void run() {
//                        if (flipPdfAdapter.is_waiting_for_image) {
//                            File page_file = create_new_ZoomableFile(tmp_book, requested_index);
//                            ((ImageView) current_page.findViewById(R.id.z_image)).setImageBitmap(BitmapFactory.decodeFile(page_file.getPath()));
//                            flipPdfAdapter.is_waiting_for_image = false;
//                            mAdapterFlipView.hideFlipView();
//                        }
//                    }
//                };
//                runOnUiThread(runnable1);
            }
        });
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

    //Getters and setters...
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
        Log.i("App_preferences ", "App_Preferences is "+app_preferences);
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

