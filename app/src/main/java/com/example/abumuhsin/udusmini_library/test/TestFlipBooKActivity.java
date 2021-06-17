
package com.example.abumuhsin.udusmini_library.test;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.burhanrashid52.imageeditor.EditImageActivity;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.Tables.BOOK_Table;
import com.example.abumuhsin.udusmini_library.Tables.PAGES_Table;
import com.example.abumuhsin.udusmini_library.Tables.PICTURES_Table;
import com.example.abumuhsin.udusmini_library.Tables.Picture_BucketTable;
import com.example.abumuhsin.udusmini_library.Views.FlipBooKView;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.Book;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.utils.App_Preferences;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.abumuhsin.udusmini_library.utils.View_Utils;
import com.example.adaptablecurlpage.flipping.AdapterPageFlipView;
import com.example.adaptablecurlpage.flipping.utils.DrawingState;
import com.example.amazing_picker.activities.Picker_Activity;
import com.example.amazing_picker.utilities.GalleryType;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Pages_table_model;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.example.abumuhsin.udusmini_library.utils.FileUtils.CAMERA;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getDownSizedBitmapFromPath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getImagePagesFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getPdfsFilePath;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.getRealDateAndTime;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.saveImage;
import static com.example.amazing_picker.activities.Picker_Activity.GALLERY_STATUS;
import static com.example.amazing_picker.activities.Picker_Activity.IMAGES_SELECTED;
import static com.example.amazing_picker.activities.Picker_Activity.NO_IMAGES_SELECTED;
import static com.example.amazing_picker.activities.Picker_Activity.PDF_SELECTED;
import static com.example.mysqlitedbconnection.csv.Constants.COVER_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.LAST_PAGE_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;


public class TestFlipBooKActivity extends AppCompatActivity implements PAGES_Table.OnBookChanged {

    public static final String TAG = "SampleActivityDebug";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    public static final String BOOK_NAME = "book_name";
    private File image_path;
//    android.app.ProgressDialog progressDialog =
    public AdapterPageFlipView mAdapterFlipView;
    private LinkedList<Flip_model> flip_list;
    public View current_page;
    public int current_page_index;
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
    //    public boolean is_seeking;
    public String book_cover;
    public int prev_index;
    Handler seek_select_handler;
    Runnable seek_select_run;
    //    public boolean is_image_seeking = false;
    public boolean is_view_fully_loaded = true;
    boolean has_entered_onCreate = false;
    public String pdf_path;
    int tmp_page_no;
    boolean is_from_pcker_result;
    boolean is_from_camera_result;
    public String pdf_name;
    //    public static final String PDF_PATH_DEBUG = "pdf_path_debug";
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
        if (View_Utils.isSoftNavAvailable(TestFlipBooKActivity.this)) {
            setUINavigationVisibilityListeneners();
        }
        setUpAdapterFlipView();
        has_entered_onCreate = true;
    }
    public FlipBooKView getFlipBooKView() {
        return flipBooKView;
    }
    @Override
    protected void onResume() {
        super.onResume();
        flipBooKView.onResume();
        if (!is_from_pcker_result && !is_from_camera_result) {
            page_table.LoadAllPageTest(this, current_book).start();
            is_from_pcker_result = false;
            is_from_camera_result = false;
        }
    }

    //Initializations...
    private void init() {
        dont_hide = false;
        initHandoutDatabase();
        initViews();
    }

    private void initHandoutDatabase() {
        Log.i(TAG, "initHandoutDatabase: begins");
        current_book = getIntent().getStringExtra(MyBook_fragment.BOOK_NAME);
        flipBooKView.setBook_title(current_book);
        book_cover = getIntent().getStringExtra(MyBook_fragment.BOOK_COVER);
        book_table = MyBook_fragment.book_table;
        page_table = MyBook_fragment.page_table;
         page_table.setOnBookChanged(this);
        pic_table = MyBook_fragment.pic_table;
        picture_bucketTable = MyBook_fragment.picture_bucketTable;
        Log.i(TAG, "initHandoutDatabase: ends");
    }

    private void initViews() {
        Log.i(TAG, "initViews: begins");
        mAdapterFlipView = flipBooKView.findViewById(R.id.flip);
        flipBooKView.setFlipBookImportCallBacks(flipBookImportCallBacks);
        flipBooKView.setFlipBookCallBacks(flipBookCallBacks);
    }

    public void setUpAdapterFlipView() {
        Log.i(TAG, "setUpAdapterFlipView: starts");
        flip_list = new LinkedList<>();
        flip_list.add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
        flip_list.add(new Flip_model(LAST_PAGE_TYPE, "back_cover", EMPTY_PAGE));
        flipBooKView.setBookList(flip_list,new Book( current_book, book_cover));
        Log.i(TAG, "setUpAdapterFlipView: ends");
    }

    //Other Activity Callbacks...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog = new ProgressDialog(this);
//            dialog.setProgressStyle(R.style.Dial);
        dialog.setTitle("Saving pages...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);

        PAGES_Table.AddingPageListener addingPageListener = new PAGES_Table.AddingPageListener() {
            @Override
            public void onAddingProgress(int i, boolean is_from_pdf) {
                dialog.setProgress(i);
            }

            @Override
            public void onPageAdded() {
                dialog.dismiss();
            }

            @Override
            public void onSavePdfFinished(final Pages_table_model[] mpages_table_model, boolean is_from_pdf) {
                if (is_from_pdf) {
                    TestFlipBooKActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            dialog.setTitle("Setting up pages to load...");
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            dialog.setMax(mpages_table_model.length);
                            dialog.show();
                        }
                    });
                }
            }

            @Override
            public void onListReadyToUpdate(int tmp_pg_no, Flip_model flip_model, boolean is_from_pdf) {
                //                    page_no = pages_table_model_data.getPage_no();
                try {
                    flip_list.add(tmp_pg_no, flip_model);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && image_path.exists()) {
            is_from_camera_result = true;
            File saved_file = saveAPathFromCameraToPage(image_path.getPath());
            Pages_table_model pages_table_model = new Pages_table_model(current_book, current_page_index
                    , SINGLE_PAGE_TYPE, SINGLE_PAGE, saved_file.getPath());
            dialog.setMax(1);
            dialog.show();
            page_table.adding_pageInBackgroundTest(this, pdf_name, current_page_index, current_book, false
                    , addingPageListener, pages_table_model).start();
        } else if (requestCode == START_PICKER_REQUEST_CODE) {
            if (resultCode == NO_IMAGES_SELECTED) {
                Log.i(TAG, "no image");
                Toast.makeText(this, "no image was selected", Toast.LENGTH_SHORT).show();
                return;
            }
            if (resultCode == IMAGES_SELECTED || resultCode == PDF_SELECTED) {
                is_from_pcker_result = true;
                Log.i(TAG, " onActivityResult: some book_images are selected");
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
                    if (pages_table_models[0].getPicture_count() == SINGLE_PAGE) {
                        dialog.setMax(pages_table_models.length);
                        dialog.show();
                    }
                    if (resultCode == IMAGES_SELECTED) {
                        page_table.adding_pageInBackgroundTest(this, pdf_name, current_page_index, current_book, false
                                , addingPageListener, pages_table_models).start();
//                        page_table.adding_pageInBackgroundTest(false, this, pages_table_models).start();
                    } else {
                        pdf_name = data.getStringExtra("pdf_name");
                        Log.i(TAG, image_path + " pdf_name is " + pdf_name);
                        page_table.adding_pageInBackgroundTest(this, pdf_name, current_page_index, current_book, true
                                , addingPageListener, pages_table_models).start();
//                        page_table.adding_pageInBackgroundTest(false, this, pages_table_models).start();
                    }
                } else {
                    Toast.makeText(this, "you can't add any page b4 the first page", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    //Handling camera

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
        flipBooKView.RefreshBookData();
        final File pdf_file = new File(getPdfsFilePath() + File.separator + current_book + ".pdf");
        final LinkedList<Flip_model> flip_list = getFlip_list();
        if (pdf_file.exists() && flip_list.size() > 2) {
            if (pdf_file.delete()) {
                ChangeBookPdf(pdf_file);
            }
        } else {
            Log.i(TAG, "pdf file does not exist or it's empty");
        }
        flipBooKView.mAdapterFlipView.RefreshFlip();
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
                    PdfUtils.add_image_to_pdfItext(TestFlipBooKActivity.this, pdf_file.getPath(), images, new PdfUtils.OnPdfListener() {
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

    FlipBooKView.FlipBookImportCallBacks flipBookImportCallBacks = new FlipBooKView.FlipBookImportCallBacks() {
        @Override
        public void onGalleryClick(View view) {
            Intent intent = new Intent(TestFlipBooKActivity.this, Picker_Activity.class);
            intent.putExtra(BOOK_NAME, current_book);
            intent.putExtra(GALLERY_STATUS, GalleryType.IMAGE);
            startActivityForResult(intent, START_PICKER_REQUEST_CODE);
        }

        @Override
        public void onCameraClick(View view) {
            try {
                StartDeviceCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPdfClick(View view) {
            Intent intent1 = new Intent(TestFlipBooKActivity.this, Picker_Activity.class);
            intent1.putExtra("book_name", current_book);
            intent1.putExtra(GALLERY_STATUS, GalleryType.PDF);
            startActivityForResult(intent1, START_PICKER_REQUEST_CODE);
        }

        @Override
        public void onAddBtnClicked(View v) {
            if (current_page_index == 0) {
                flipBooKView.mAdapterFlipView.flip_toNextPage(300);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flipBooKView.SwitchImportBtnDisplay();
                    }
                }, 400);
            } else {
                flipBooKView.SwitchImportBtnDisplay();
            }
        }
    };

    //Click handlers...
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (flipBooKView.EnableVolumeFlip(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    FlipBooKView.FlipBookCallBacks flipBookCallBacks = new FlipBooKView.FlipBookCallBacks() {
        @Override
        public void onPageDeleteConfirmed(View v) {
            if (current_page_index < 1) {
                Toast.makeText(TestFlipBooKActivity.this, "you can't remove the first page", Toast.LENGTH_SHORT).show();
                Log.e(TAG, " you can't remove the first page here");
                return;
            } else if (current_page_index == flip_list.size() - 1) {
                Log.e(TAG, " you can't remove the last page here");
                return;
            }
            page_table.RemoveAPageTest(TestFlipBooKActivity.this, flip_list, current_book, current_page_index);
        }

        @Override
        public void onEditBtnClick(View v) {
            if (current_page_index > 0 && current_page_index < flip_list.size() - 1) {
                startEditing();
            } else if (current_page_index == 0) {
                EditCover();
            }
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
            int end_no = page_size > 1 ? page_size - 2 : page_size;
            String prog_txt = null;
            if (page_no>0) {
                prog_txt = page_no<page_size-1?(page_no + "/" + end_no):"end page";
            }else {
                prog_txt = "cover page";
            }
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
            dialog = new ProgressDialog(TestFlipBooKActivity.this);
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
                        PdfUtils.add_image_to_pdfItext(TestFlipBooKActivity.this,path, images, new PdfUtils.OnPdfListener() {
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
                    Toast.makeText(TestFlipBooKActivity.this, error_msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPdfConversionSucceeded(File file, String success_msg) {
                    dialog.dismiss();
                    Toast.makeText(TestFlipBooKActivity.this, success_msg, Toast.LENGTH_SHORT).show();
                    PdfUtils.play_pdf(TestFlipBooKActivity.this, file);
                }
            };
            new TConvert_to_pdfTask(true,getPicsFromPages(flip_list),convertPdfCallbacks).execute(pdf_file);
        }

        @Override
        public void onMoreBtnClick(View v) {
            //Todo
        }
    };
    private ArrayList<String> getPicsFromPages(LinkedList<Flip_model> flip_list) {
        ArrayList<String> images = new ArrayList<>();
        for (int i = 1; i < flip_list.size() - 1; i++) images.add(flip_list.get(i).getImage_path());
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

    private void startEditing() {
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra("pic", flip_list.get(current_page_index).getImage_path());
        startActivity(intent);
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

    public boolean is_click_not_toFlip(MotionEvent e) {
        float max_X = e.getDevice().getMotionRange(MotionEvent.AXIS_X).getMax();
        return ((max_X / e.getX()) >= 1.087613F && (max_X / e.getX()) <= 12.416F);
    }

}

