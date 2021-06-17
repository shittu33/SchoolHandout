
package com.example.abumuhsin.udusmini_library.test;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Views.FlipBooKView;
import com.example.abumuhsin.udusmini_library.models.Book;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.BOOK_NAME_EXTRA;
import static com.example.abumuhsin.udusmini_library.fragments.GalleryBook_fragment.IMAGES_EXTRA;
import static com.example.mysqlitedbconnection.csv.Constants.COVER_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;


public class TFlipGalleryActivity extends AppCompatActivity {
    public static final String TAG = "SampleActivityDebug";
    public AdapterPageFlipView mAdapterFlipView;
    private LinkedList<Flip_model> flip_list;
    public View current_page;
    public int current_page_index;
    public boolean dont_hide;
    public String current_book = "";
    public String book_cover;
    ActionBar actionBar;
    Handler seek_select_handler;
    Runnable seek_select_run;
    public boolean is_view_fully_loaded = true;
    boolean has_entered_onCreate = false;
    public String pdf_name;
    private FlipBooKView flipBooKView;
    private ProgressDialog dialog;

    // Main Activity Callbacks...
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flipBooKView = new FlipBooKView(this);
        setContentView(flipBooKView);
        init();
        Log.i(TAG, "onCreate: after PostDisplayNavigation");
        if (View_Utils.isSoftNavAvailable(TFlipGalleryActivity.this)) {
            setUINavigationVisibilityListeneners();
        }
        Log.i(TAG, "onCreate: after setUINavigationVisibilityListeners");
        setUpAdapterFlipView();
        has_entered_onCreate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        flipBooKView.onResume();
        ArrayList<String> gallery_images = getIntent().getStringArrayListExtra(IMAGES_EXTRA);
        if (gallery_images != null) {
            flip_list.clear();
            for (String page_path : gallery_images) {
                flip_list.add(new Flip_model(SINGLE_PAGE_TYPE, page_path, SINGLE_PAGE));
            }
        } else {
            flip_list.add(new Flip_model(SINGLE_PAGE_TYPE, "no image", SINGLE_PAGE));
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    flipBooKView.RefreshBookData();
                    Log.i(TAG, "onResume()-> is_pics_from_device: can draw");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResume()-> is_pics_from_device: can't draw");
                }
            }
        });
        Log.i(TAG, "onResume()-> is_pics_from_device: Adapter notified");
    }

    //Initializations...
    private void init() {
        dont_hide = false;
        initViews();
        current_book = getIntent().getStringExtra(BOOK_NAME_EXTRA);
        flipBooKView.setBook_title(current_book);
//        String title = null;
//        if (current_book != null && actionBar != null) {
//            title = current_book.length() >= 20 ? current_book.substring(0, 20) + "..." : current_book;
//            actionBar.setTitle(title);
//        }
    }
    private void initViews() {
        mAdapterFlipView = flipBooKView.findViewById(R.id.flip);
        flipBooKView.setFlipBookCallBacks(flipBookCallBacks);
        flipBooKView.DisableFuntions(false);
        Log.i(TAG, "initViews: ends");
    }

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
        flip_list = new LinkedList<>();
        flip_list.add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
        Log.i(TAG, "setUpAdapterFlipView-> is_pics_from_device: first_page added for device gallery Flip");
        flipBooKView.setBookList(flip_list,new Book( current_book, book_cover));
        Log.i(TAG, "setUpAdapterFlipView: ends");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        flipBooKView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        flipBooKView.onDestroy();
        super.onDestroy();
    }

    //Click handlers...

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (flipBooKView.EnableVolumeFlip(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    final FlipBooKView.FlipBookCallBacks flipBookCallBacks = new FlipBooKView.FlipBookCallBacks() {
        @Override
        public void onPageDeleteConfirmed(View v) {
        }

        @Override
        public void onEditBtnClick(View v) {
        }

        @Override
        public void onSeeking(final int seek_position) {
            seek_select_handler = new Handler();
            seek_select_run = new Runnable() {
                @Override
                public void run() {
                    flipBooKView.mAdapterFlipView.set_flexible_Selection(seek_position);
                }
            };
            if (seek_position<flip_list.size()) {
                seek_select_handler.post(seek_select_run);
            }
        }

        @Override
        public void onSeekingStarted(IndicatorSeekBar seekBar) {
            flipBooKView.StopHidingNavigations();
            flipBooKView.CancelAutoRefresh();
            flipBooKView.is_image_seeking = true;
            flipBooKView.mAdapterFlipView.MakeAdapterVisible();
        }

        @Override
        public void onSeekingStopped(IndicatorSeekBar seekBar) {
            flipBooKView.is_image_seeking = false;
            flipBooKView.RefreshBookData();
        }

        @Override
        public FlipBooKView.Page_info onPageFlipped(View page, int page_no, long id) {
            current_page_index = page_no;
            current_page = page;
            int page_size = flip_list.size();
            int end_no = page_size > 0 ? page_size  : page_size;
            String prog_txt = page_no + 1 + "/" + end_no;
            return new FlipBooKView.Page_info(prog_txt,end_no);
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
            dialog = new ProgressDialog(TFlipGalleryActivity.this);
            dialog.setTitle("Converting this book to pdf...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            TConvert_to_pdfTask.ConvertPdfCallbacks convertPdfCallbacks = new TConvert_to_pdfTask.ConvertPdfCallbacks() {
                @Override
                public void onPdfConversionStarted(int book_size) {
                    dialog.setMax(book_size);
                    dialog.show();
                }

                @Override
                public void onReadyToConvertInBackground(String path, ArrayList<String> images) {
                    try {
                        PdfUtils.add_image_to_pdfItext(TFlipGalleryActivity.this, path, images, new PdfUtils.OnPdfListener() {
                            @Override
                            public void onPdf_progress(int i) {
                                dialog.setProgress(i);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConversionError(String error_msg) {
                    dialog.dismiss();
                    Toast.makeText(TFlipGalleryActivity.this, error_msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPdfConversionSucceeded(File file, String success_msg) {
                    dialog.dismiss();
                    Toast.makeText(TFlipGalleryActivity.this, success_msg, Toast.LENGTH_SHORT).show();
                    PdfUtils.play_pdf(TFlipGalleryActivity.this, file);
                }
            };
            new TConvert_to_pdfTask(false, getPicsFromPages(flip_list), convertPdfCallbacks).execute(pdf_file);
        }

        @Override
        public void onMoreBtnClick(View v) {
            //Todo
        }
    };

    private ArrayList<String> getPicsFromPages(LinkedList<Flip_model> flip_list) {
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < flip_list.size(); i++) images.add(flip_list.get(i).getImage_path());
        return images;
    }


    //Getters...
    public LinkedList<Flip_model> getFlip_list() {
        return flip_list;
    }

    //...........................Helpers....................

    // Handling UI Visibilities.....

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


    public boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }

}

