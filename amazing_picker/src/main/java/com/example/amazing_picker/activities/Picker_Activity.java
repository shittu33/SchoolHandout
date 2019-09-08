package com.example.amazing_picker.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.amazing_picker.pdfLoaders.PdfBackwardLoaderTask;
import com.example.amazing_picker.pdfLoaders.PdfForwardLoaderTask;
import com.example.amazing_picker.pdfLoaders.PdfViewerLoaderTask;
import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.ExtendedViewPager;
import com.example.amazing_picker.Veiws.SelectableViewHolder;
import com.example.amazing_picker.adapters.Dir_recyler_adapter;
import com.example.amazing_picker.adapters.Images_recyler_adapter;
import com.example.amazing_picker.adapters.Img_pagerAdapter;
import com.example.amazing_picker.adapters.Pdf_pager_adapter;
import com.example.amazing_picker.adapters.Pdf_recyler_adapter;
import com.example.amazing_picker.models.Folder_image;
import com.example.amazing_picker.models.Model_images;
import com.example.amazing_picker.models.SelectablePdf_page;
import com.example.amazing_picker.models.Selectable_image;
import com.example.amazing_picker.utilities.GalleryType;
import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;
import com.example.amazing_picker.utilities.PdfUtils;
import com.example.amazing_picker.utilities.Pdf_Cursor_loaderUtils;
import com.example.amazing_picker.utilities.View_Utils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_ALREADY_GRANTED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_REQUEST_NEEDED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.getImagesFrom;
import static com.example.amazing_picker.utilities.Pdf_Cursor_loaderUtils.PIC_TAG;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Picker_Activity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener
        , SelectableViewHolder.OnRecylerItemSelected,
        Dir_recyler_adapter.FolderCLickListener, Pdf_recyler_adapter.PdfCLickListener, AdapterView.OnItemSelectedListener
        , OnSeekChangeListener {
    private RecyclerView dir_RecyclerView, images_RecyclerView;
    private GridLayoutManager gridLayoutManager;
    private Images_recyler_adapter images_recyler_adapter;
    private Pdf_recyler_adapter pdf_recycler_adapter;
    private Dir_recyler_adapter dir_recyler_adapter;
    private ArrayList<Selectable_image> imageList;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;
    private com.github.clans.fab.FloatingActionButton picker_nav;
    private Guideline guideline3;
    private ArrayList<Folder_image> folder_imageList;
    private ArrayList<Folder_image> folder_pdfList;
    private ArrayList<Model_images> folders_and_images = new ArrayList<>();
    private ArrayList<Model_images> folders_and_pdfs = new ArrayList<>();
    boolean folder_is_visible = false;
    private TextView num_txt;
    private ImageButton cancel_picker;
    private com.github.clans.fab.FloatingActionButton view_btn, comfirm_btn;

    //picker_preview
    private IndicatorSeekBar indicatorSeekBar;
    private ExtendedViewPager pager;
    private Img_pagerAdapter pagerAdapter;
    Spinner file_type_spin;
    ArrayAdapter<String> spinAdapter;
    private com.github.clans.fab.FloatingActionButton exit_btn;
    private Button copy_btn;
    private AppCompatRadioButton select_btn;
    private TextView title_tv;
    private Dialog imagePreview_dialog;
    public boolean is_to_check;
    private boolean is_loading_pdf_first_time = true;
    //pdf picker
    boolean is_pdf_folder = false;
    boolean do_check_pdf_page = false;
    private ExtendedViewPager pdf_pager;
    private View pdf_preview_layout;
    private ArrayList<String> pdfList;
    private ArrayList<SelectablePdf_page> bitmaps = new ArrayList<>();
    private Pdf_pager_adapter pdf_pager_adapter;
    //    private ParcelFileDescriptor parcelFileDescriptor = null;
    private TextView folder_title;

    boolean is_swipe_for_pdf = false;
    int count = 0;
    String pdf_path;
    int prev_position = 0;
    int next_position = 0;
    int current_postion = 0;
    public String book_name;
    private AppCompatRadioButton select_btn_all;

    public static final String GALLERY_STATUS = "gallery_status";
    public static final String BOOK_NAME = "book_name";
    Enum gallery_type;
    //Initializations...
    private void init() {
        book_name = getIntent().getStringExtra(BOOK_NAME);
        gallery_type = (Enum) getIntent().getSerializableExtra(GALLERY_STATUS);
        initViews();
        initLists();
        initAdapters();
//        ListWrapper listWrapper = (ListWrapper)getIntent().getSerializableExtra("images");
//        folders_and_images = listWrapper.getModel_images();
    }

    private void initAdapters() {
        pdf_recycler_adapter = new Pdf_recyler_adapter(this, pdfList, this);
        spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Images", "Pdf"});
        file_type_spin.setAdapter(spinAdapter);
        file_type_spin.setSelection(gallery_type==GalleryType.IMAGE?0:1);
        LoadFoldersAndImages();
        Log.i(PIC_TAG, "got it");
        setUpPickerImagesAdapter(imageList);
    }

    private void initViews() {
        guideline3 = findViewById(R.id.guideline3);
        picker_nav = findViewById(R.id.picker_nav);
        comfirm_btn = findViewById(R.id.confirm_btn);
        num_txt = findViewById(R.id.num_txt);
        cancel_picker = findViewById(R.id.cancel_picker);
        picker_nav.setOnClickListener(this);
        view_btn = findViewById(R.id.view_btn);
        file_type_spin = findViewById(R.id.spin);
        folder_title = findViewById(R.id.folder_title);
        constraintLayout = findViewById(R.id.picker_lay);
        pdf_preview_layout = constraintLayout.findViewById(R.id.pdf_preview_lay);
//        bucket_layout = constraintLayout.findViewById(R.id.bucket_sheet);
        copy_btn = pdf_preview_layout.findViewById(R.id.copy_btn);
        images_RecyclerView = findViewById(R.id.img_recycler);
        dir_RecyclerView = findViewById(R.id.dir_recycler);
        copy_btn.setOnClickListener(this);
        file_type_spin.setOnItemSelectedListener(this);
        view_btn.setOnClickListener(this);
        comfirm_btn.setOnClickListener(this);
        cancel_picker.setOnClickListener(this);
    }

    private void initLists() {
        folder_pdfList = new ArrayList<>();
        folder_imageList = new ArrayList<>();
        imageList = new ArrayList<>();
        pdfList = new ArrayList<>();

    }

    public void setUpDirAdapter(ArrayList<Folder_image> dir_list) {
//        getDirectoriesFromFolder_ImageList();
        dir_recyler_adapter = new Dir_recyler_adapter(this, dir_list, this);
        @SuppressLint("WrongConstant")
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, VERTICAL, false);
        dir_RecyclerView.setLayoutManager(layoutManager);
        dir_RecyclerView.setAdapter(dir_recyler_adapter);
    }

    @SuppressLint("WrongConstant")
    public void setUpPickerImagesAdapter(ArrayList<Selectable_image> imageList) {
        images_recyler_adapter = new Images_recyler_adapter(this, imageList, this, true);
        gridLayoutManager = new GridLayoutManager(this, 3, VERTICAL, false);
        images_RecyclerView.setLayoutManager(gridLayoutManager);
        images_RecyclerView.setAdapter(images_recyler_adapter);
    }
    //Activity CallBacks

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_recycler_layout);
        init();
        Log.i(PIC_TAG, "started");
        AutohidePickerNavs();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //Images
                clear_pdfsFromScreen();
                setUpDirAdapter(getDirectoriesFromFolder_ImageList());
                break;
            case 1:
                //Pdfs
//                Picker_CursorLoaderTask.get(this).StopLoading_Data();
                clear_ImagesFromScreen();
                GetPdfsAndDirectoryFromDevice();
                break;
        }
    }

    //Clear Screen...
    private void clear_pdfsFromScreen() {
        pdfList.clear();
        if (pdf_recycler_adapter != null) {
            pdf_recycler_adapter.notifyDataSetChanged();
        }
    }

    private void clear_ImagesFromScreen() {
        imageList.clear();
        if (images_recyler_adapter!=null) {
            images_recyler_adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 666: {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.i(ImageCursorLoaderUtils.PIC_TAG, "Now we can set it up for good");
                        //noinspection unchecked
                        folders_and_images = ImageCursorLoaderUtils.getImages_and_Folders(Picker_Activity.this);
//                        Picker_CursorLoaderTask.get(this).LoadData();
                        setUpDirAdapter(getDirectoriesFromFolder_ImageList());
                        is_loading_pdf_first_time = false;
                    } else {
                        Toast.makeText(Picker_Activity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            }
            case 777:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        folders_and_images = Pdf_Cursor_loaderUtils.getPdfs_and_Folders(this);
                        LoadAndSetDirectoryLsit_For_PdfRecycler();
                    } else {
                        Toast.makeText(Picker_Activity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (pdf_preview_layout.isShown()) {
            CancelPdf_preview();
        } else if (imagePreview_dialog!=null && imagePreview_dialog.isShowing()) {
            ExitImagePreview();
        } else {
            super.onBackPressed();
        }
    }

    public static final int PDF_SELECTED = 2222;
    public static final int IMAGES_SELECTED = 3333;
    public static final int NO_IMAGES_SELECTED = 4444;
    public static final int ACTION_CANCEL = 5555;

    void ExitImagePreview() {
        if (imagePreview_dialog != null) {
            if (imagePreview_dialog.isShowing()) {
                imagePreview_dialog.dismiss();
            }
            ArrayList<Selectable_image> indexed_selectable_images = images_recyler_adapter.getIndexed_selectable_images();
            indexed_selectable_images.clear();
            indexed_selectable_images.addAll(pagerAdapter.getTmp_selected_images());
            images_recyler_adapter.notifyDataSetChanged();
            selected_count = images_recyler_adapter.getIndexed_selectable_images().size();
            num_txt.setText(String.valueOf(selected_count));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.exit_btn) {
            if (pdf_preview_layout.isShown()) {
                CancelPdf_preview();
            } else {
                ExitImagePreview();
            }
        } else if (i == R.id.copy_btn) {
            if (is_swipe_for_pdf) {
                //copy pdf pages
                pdf_preview_layout.setVisibility(View.GONE);
                StopCopyingPagesAndDeleteUnselectedPages();
                List<SelectablePdf_page> selectablePdf_pages = pdf_pager_adapter.getTempSelected_PdfPages();
                savePagesFromSelectedPdfs(selectablePdf_pages);
            } else {
                //copy images
                List<Selectable_image> selectable_pics = images_recyler_adapter.getIndexed_selectable_images();
                savePagesFromSelectedList(selectable_pics);
            }
        } else if (i == R.id.picker_nav) {
            HideOrShow_DirectoryView();
        } else if (i == R.id.cancel_picker) {
            onBackPressed();
        } else if (i == R.id.confirm_btn) {
            List<Selectable_image> selectable_pics = images_recyler_adapter.getIndexed_selectable_images();
            savePagesFromSelectedList(selectable_pics);
        } else if (i == R.id.view_btn) {
            if (images_recyler_adapter.getSelectedItems().isEmpty()) {
                Toast.makeText(this, "Select at least one Image", Toast.LENGTH_SHORT).show();
            } else {
                View pager_layout = LayoutInflater.from(this).inflate(R.layout.picker_preview_pager, null);
                ShowImagePreviewDialog(pager_layout);
            }
        } else if (i == R.id.radio_btn) {
            if (is_swipe_for_pdf) {
                SelectOrDeselect_Pdf_Page();
            } else {
                SelectOrDeselect_PreviewImage();
            }
        } else if (i == R.id.radio_btn_all) {
            if (is_swipe_for_pdf) {
                SelectOrDeselect_Pdf_Page_All();
            } else {
                Toast.makeText(this, "not available yet", Toast.LENGTH_SHORT).show();
                SelectOrDeselect_All_Images();
            }
        }
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        if (pagerAdapter != null) {
            if (seekParams.fromUser) {
                pager.setCurrentItem(seekParams.progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        if (is_swipe_for_pdf) {
            PdfBackwardLoaderTask.get(this).LoadPdfToStorage(pdf_path, seekBar.getProgress() + 1);
            PdfForwardLoaderTask.get(this).LoadPdfToStorage(pdf_path, seekBar.getProgress() + 1);
        }
        if (is_swipe_for_pdf) {
            if (pdf_pager_adapter != null) {
                pdf_pager.setCurrentItem(seekBar.getProgress());
            }
        }
    }

    private void StopCopyingPagesAndDeleteUnselectedPages() {
        PdfViewerLoaderTask.get(this).JustStop_pdfThread();
        ArrayList<SelectablePdf_page> non_selected_pages = pdf_pager_adapter.getNonSelected_PdfPages();
        for (SelectablePdf_page non_selected_pdf : non_selected_pages) {
            File non_selected_page = new File(non_selected_pdf.getPdf_page_path());
            if (non_selected_page.exists()) {
                non_selected_page.delete();
                Log.i("pdf_bitmap", non_selected_page.getName() + " was not selected and has been deleted");
            }
        }
    }

    private void savePagesFromSelectedPdfs(List<SelectablePdf_page> selectablePdf_pages) {
        Intent result_intent = new Intent();
        if (!selectablePdf_pages.isEmpty()) {
            ArrayList<String> selected_images = new ArrayList<>();
            for (SelectablePdf_page selectablePdf_page : selectablePdf_pages) {
                selected_images.add(selectablePdf_page.getPdf_page_path());
            }
            result_intent.putExtra("pdf_name", pdf_path);
            Log.i("pdf_bitmap", "pdf path to copy is " + pdf_path);
            result_intent.putStringArrayListExtra("pics", selected_images);
            setResult(PDF_SELECTED, result_intent);
        } else {
            setResult(NO_IMAGES_SELECTED);
        }
        finish();
    }

    private void savePagesFromSelectedList(List<Selectable_image> selectable_pics) {
        Intent result_intent = new Intent();
        if (!selectable_pics.isEmpty()) {
            ArrayList<String> selected_images = new ArrayList<>();
            for (Selectable_image selectable_image : selectable_pics) {
                selected_images.add(selectable_image.getSelectable_path());
            }
            result_intent.putStringArrayListExtra("pics", selected_images);
            setResult(IMAGES_SELECTED, result_intent);
        } else {
            setResult(NO_IMAGES_SELECTED);
        }
        finish();
    }

    //Self created Callbacks
    @Override
    public void OnPdfClicked(final String pdf_path, int position) {
        this.pdf_path = pdf_path;
        Log.i("pdf_bitmap", pdf_path);
        is_swipe_for_pdf = true;
        pdf_pager = pdf_preview_layout.findViewById(R.id.view_pager);
        exit_btn = pdf_preview_layout.findViewById(R.id.exit_btn);
        title_tv = pdf_preview_layout.findViewById(R.id.title_tv);
        select_btn = pdf_preview_layout.findViewById(R.id.radio_btn);
        select_btn_all = pdf_preview_layout.findViewById(R.id.radio_btn_all);
        copy_btn = pdf_preview_layout.findViewById(R.id.copy_btn);
        indicatorSeekBar = pdf_preview_layout.findViewById(R.id.indicator_seekbar);
        indicatorSeekBar.setOnSeekChangeListener(this);
        exit_btn.setOnClickListener(this);
        copy_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        select_btn_all.setOnClickListener(this);
        PdfViewerLoaderTask.get(this).LoadPdfToStorage(pdf_path);
        bitmaps.clear();
        Log.i("pdf_bitmap", "b4 Handler");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count = PdfViewerLoaderTask.get(Picker_Activity.this).getPdf_page_count();
                for (int i = 0; i < count; i++) {
                    bitmaps.add(new SelectablePdf_page(PdfUtils.getActivePdfPagePath(pdf_path, i), true));
                }
                Log.i("pdf_bitmap", "b4 adapter");
                pdf_pager_adapter = new Pdf_pager_adapter(Picker_Activity.this, bitmaps);
                Log.i("pdf_bitmap", "after adapter");
                pdf_pager.setAdapter(pdf_pager_adapter);
                Log.i("pdf_bitmap", "adapter is set");
                pdf_pager.addOnPageChangeListener(Picker_Activity.this);
                Log.i("pdf_bitmap", "b4 title");
//                title_tv.setText(pdf_pager_adapter.getPageTitle(0));
                pdf_preview_layout.setVisibility(View.VISIBLE);
                if (count > 0) {
                    indicatorSeekBar.setVisibility(View.VISIBLE);
                }
                indicatorSeekBar.setMin(1);
                indicatorSeekBar.setThumbAdjustAuto(false);
                View_Utils.hideSystemUI(Picker_Activity.this);
            }
        }, 1000);
    }

    int selected_count;

    @Override
    public void onImageSelected(Selectable_image image) {
        selected_count = images_recyler_adapter.getSelectedItems().size();
        Log.i(PIC_TAG, "onImageSelected: selected_count is " + selected_count);
        num_txt.setVisibility(View.VISIBLE);
        if (selected_count == 1) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is one");
        } else if (selected_count == 0) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is zero");
            num_txt.setVisibility(View.GONE);
        }
        num_txt.setText(String.valueOf(selected_count));
        Log.i(PIC_TAG, "onImageSelected: num_text is set");
        Log.i(PIC_TAG, image.getSelectable_path() + "->\n " + (image.isSelected() ? "is selected" : "not selected"));
    }

    @Override
    public void onFolderClick(String folder_name, int position, boolean is_pdf_folder) {
        this.is_pdf_folder = is_pdf_folder;
        folder_title.setText(folder_name);
        if (is_pdf_folder) {
            Load_pdfs_from_Folder(folder_name);
        } else {
            Load_ImagesFromFolder(folder_name);
        }
    }

    //View Pager CallBacks
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        prev_position = position;
        if (is_swipe_for_pdf) {
            indicatorSeekBar.setMax(count);
            indicatorSeekBar.setProgress(position + 1);
        } else {
            indicatorSeekBar.setMax(images_recyler_adapter.getIndexed_selectable_images().size());
            indicatorSeekBar.setProgress(position + 1);
        }
    }

    @Override
    public void onPageSelected(int position) {
        current_postion = position;
        if (is_swipe_for_pdf) {
            //pdf preview
            title_tv.setText(pdf_pager_adapter.getPageTitle(position));
            boolean is_currentPage_selected = pdf_pager_adapter.getSelectablePdf_pages().get(position).is_page_selected();
            select_btn.setChecked(is_currentPage_selected);
            select_btn.setText(is_currentPage_selected ? "Deselect" : "Select");
        } else {
            //image preview
            title_tv.setText(pagerAdapter.getPageTitle(position));
            boolean is_currentPage_selected = pagerAdapter.getImages().get(position).isSelected();
            select_btn.setChecked(is_currentPage_selected);
            select_btn.setText(is_currentPage_selected ? "Deselect" : "Select");
//            is_to_check = is_currentPage_selected;
        }
    }

    //Pdf preview helper
    public void CancelPdf_preview() {
        pdf_preview_layout.setVisibility(View.GONE);
        is_swipe_for_pdf = false;
        PdfViewerLoaderTask.get(this).stop_pdfThread_and_delete_tmp_pages();
        prev_position = 0;
        next_position = 0;
        current_postion = 0;
        pdf_pager.removeOnPageChangeListener(this);
//        View_Utils.showSystemUI(this);
    }

    boolean is_all_selected = false;

    private void SelectOrDeselect_Pdf_Page_All() {
        ArrayList<SelectablePdf_page> selectablePdf_pages = pdf_pager_adapter.getSelectablePdf_pages();
        ArrayList<SelectablePdf_page> temp_selected_pages = pdf_pager_adapter.getTempSelected_PdfPages();
        is_all_selected = !select_btn_all.getText().equals(getString(R.string.select_all_txt));
        if (is_all_selected) {
            select_btn_all.setChecked(false);
            select_btn.setChecked(false);
            select_btn.setText(getString(R.string.select_page));
            for (int i = 0; i < selectablePdf_pages.size(); i++) {
                SelectablePdf_page selectablePdf_page = selectablePdf_pages.get(i);
                selectablePdf_page.set_page_selected(false);
                temp_selected_pages.remove(selectablePdf_page);

            }
            select_btn_all.setText(getString(R.string.select_all_txt));
            Toast.makeText(this, "all deselected", Toast.LENGTH_SHORT).show();
        } else {
            select_btn_all.setChecked(true);
            select_btn.setChecked(true);
            select_btn.setText(getString(R.string.deselect_page));
            for (int i = 0; i < selectablePdf_pages.size(); i++) {
                SelectablePdf_page selectablePdf_page = selectablePdf_pages.get(i);
                selectablePdf_page.set_page_selected(true);
                if (!temp_selected_pages.contains(selectablePdf_page)) {
                    temp_selected_pages.add(selectablePdf_page);
                }
            }
            select_btn_all.setText(getString(R.string.deselect_all_txt));
            Toast.makeText(this, "all selected", Toast.LENGTH_SHORT).show();
        }
        pdf_pager_adapter.updateSelectedNo(temp_selected_pages.size());
        pdf_pager_adapter.notifyDataSetChanged();
    }

    private void SelectOrDeselect_All_Images() {
        ArrayList<Selectable_image> selectable_images = pagerAdapter.getImages();
        ArrayList<Selectable_image> tmp_selectable_images = pagerAdapter.getTmp_selected_images();
        is_all_selected = !select_btn_all.getText().equals(getString(R.string.select_all_txt));
        if (is_all_selected) {
            select_btn_all.setChecked(false);
            select_btn.setChecked(false);
            select_btn.setText(getString(R.string.select_page));
            for (int i = 0; i < selectable_images.size(); i++) {
                Selectable_image selectable_image = selectable_images.get(i);
                selectable_image.set_selected(false);
                tmp_selectable_images.remove(selectable_image);
            }
            select_btn_all.setText(getString(R.string.select_all_txt));
            Toast.makeText(this, "all deselected", Toast.LENGTH_SHORT).show();
        } else {
            select_btn_all.setChecked(true);
            select_btn.setChecked(true);
            select_btn.setText(getString(R.string.deselect_page));
            for (int i = 0; i < selectable_images.size(); i++) {
                Selectable_image selectable_image = selectable_images.get(i);
                selectable_image.set_selected(true);
                if (!tmp_selectable_images.contains(selectable_image)) {
                    tmp_selectable_images.add(selectable_image);
                }
            }
            select_btn_all.setText(getString(R.string.deselect_all_txt));
            Toast.makeText(this, "all selected", Toast.LENGTH_SHORT).show();
        }
        pagerAdapter.updateSelectedNo(tmp_selectable_images.size());
        pagerAdapter.notifyDataSetChanged();
    }

    private void SelectOrDeselect_Pdf_Page() {
        int current_adapter_index = pdf_pager.getCurrentItem();
        ArrayList<SelectablePdf_page> selectablePdf_pages = pdf_pager_adapter.getSelectablePdf_pages();
        ArrayList<SelectablePdf_page> tmp_selected_pages = pdf_pager_adapter.getTempSelected_PdfPages();
        if (selectablePdf_pages.get(current_adapter_index).is_page_selected()) {
            do_check_pdf_page = false;
        } else if (!selectablePdf_pages.get(current_adapter_index).is_page_selected()) {
            do_check_pdf_page = true;
        }
        select_btn.setChecked(do_check_pdf_page);
        select_btn.setText(do_check_pdf_page ? "Deselect" : "Select");
        selectablePdf_pages.get(current_adapter_index).set_page_selected(do_check_pdf_page);
        SelectablePdf_page current_selectable_page = selectablePdf_pages.get(current_adapter_index);
        if (do_check_pdf_page && !tmp_selected_pages.contains(current_selectable_page)) {
            tmp_selected_pages.add(current_selectable_page);
        } else if (!do_check_pdf_page) {
            tmp_selected_pages.remove(current_selectable_page);
        }
        pdf_pager_adapter.updateSelectedNo(tmp_selected_pages.size());
        pdf_pager_adapter.notifyDataSetChanged();
    }

    //Image Picker preview helper methods...
    private void SelectOrDeselect_PreviewImage() {
        int current_adapter_index = pager.getCurrentItem();
        ArrayList<Selectable_image> selectable_images = pagerAdapter.getImages();
        ArrayList<Selectable_image> tmp_selectable_images = pagerAdapter.getTmp_selected_images();
        if (selectable_images.get(current_adapter_index).isSelected()) {
            is_to_check = false;
        } else if (!selectable_images.get(current_adapter_index).isSelected()) {
            is_to_check = true;
        }
        select_btn.setChecked(is_to_check);
        select_btn.setText(is_to_check ? "Deselect" : "Select");
        selectable_images.get(current_adapter_index).set_selected(is_to_check);
        Selectable_image current_selectable_image = selectable_images.get(current_adapter_index);
        if (is_to_check && !tmp_selectable_images.contains(current_selectable_image)) {
            tmp_selectable_images.add(current_selectable_image);
        } else if (!is_to_check) {
            tmp_selectable_images.remove(current_selectable_image);
        }
        pagerAdapter.updateSelectedNo(tmp_selectable_images.size());
//        images_recyler_adapter.onImageSelected(selectable_images.get(current_adapter_index));
        pagerAdapter.notifyDataSetChanged();

    }

    private void ShowImagePreviewDialog(View pager_layout) {
        pager = pager_layout.findViewById(R.id.view_pager);
        exit_btn = pager_layout.findViewById(R.id.exit_btn);
        copy_btn = pager_layout.findViewById(R.id.copy_btn);
        title_tv = pager_layout.findViewById(R.id.title_tv);
        select_btn = pager_layout.findViewById(R.id.radio_btn);
        select_btn_all = pager_layout.findViewById(R.id.radio_btn_all);
        indicatorSeekBar = pager_layout.findViewById(R.id.indicator_seekbar);
        indicatorSeekBar.setOnSeekChangeListener(this);
        copy_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        select_btn_all.setOnClickListener(this);
        Log.i(PIC_TAG, "first image is " + images_recyler_adapter.getIndexed_selectable_images().get(0).getSelectable_path());
        pagerAdapter = new Img_pagerAdapter(this,
                images_recyler_adapter.getIndexed_selectable_images());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);
        title_tv.setText(pagerAdapter.getPageTitle(0));

        if (images_recyler_adapter.getIndexed_selectable_images().size() > 0) {
            indicatorSeekBar.setVisibility(View.VISIBLE);
        }
        indicatorSeekBar.setMin(1);
        indicatorSeekBar.setThumbAdjustAuto(false);
        imagePreview_dialog = new AlertDialog.Builder(this).setView(pager_layout).create();
        imagePreview_dialog.show();
        imagePreview_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                pager.clearOnPageChangeListeners();
                ExitImagePreview();
            }
        });

    }

    //General Loading...
    private void GetPdfsAndDirectoryFromDevice() {
        if (is_loading_pdf_first_time) {
            if (ImageCursorLoaderUtils.isPermissionGranted(Picker_Activity.this) == PERMISSION_ALREADY_GRANTED) {
                Log.i(PIC_TAG, "granted");
                folders_and_pdfs = Pdf_Cursor_loaderUtils.getPdfs_and_Folders(this);
                LoadAndSetDirectoryLsit_For_PdfRecycler();
                is_loading_pdf_first_time = false;
            } else if (ImageCursorLoaderUtils.isPermissionGranted(Picker_Activity.this) == PERMISSION_REQUEST_NEEDED) {
                ImageCursorLoaderUtils.Request_for_permission(this, 777);
            }
        } else {
            LoadAndSetDirectoryLsit_For_PdfRecycler();
        }
    }

    public void LoadFoldersAndImages() {
//        Picker_CursorLoaderTask.get(this).StopLoading_Data();
        if (ImageCursorLoaderUtils.isPermissionGranted(Picker_Activity.this) == PERMISSION_ALREADY_GRANTED) {
            Log.i(PIC_TAG, "granted");
            folders_and_images = ImageCursorLoaderUtils.getImages_and_Folders(this);
//            Picker_CursorLoaderTask.get(this).LoadData();
        } else if (ImageCursorLoaderUtils.isPermissionGranted(Picker_Activity.this) == PERMISSION_REQUEST_NEEDED) {
            ImageCursorLoaderUtils.Request_for_permission(this, 666);
        }
    }

    //Directory Loading...
    private void LoadAndSetDirectoryLsit_For_PdfRecycler() {
        folder_pdfList.clear();
        for (Model_images model_pdf : folders_and_pdfs) {
            folder_pdfList.add(
                    new Folder_image(model_pdf.getStr_folder(), "pdf"));
        }
        setUpDirAdapter(folder_pdfList);
    }

    public ArrayList<Folder_image> getDirectoriesFromFolder_ImageList() {
        imageList.clear();
        folder_imageList.clear();
        for (Model_images model_image : folders_and_images) {
            folder_imageList.add(
                    new Folder_image(model_image.getStr_folder(),
                            model_image.getAl_imagepath().get(0)));
        }
        return folder_imageList;
    }

    //Image/Pdf Loading ...
    private void Load_ImagesFromFolder(String folder_name) {
        if (!imageList.isEmpty()) {
            imageList.clear();
        }
        for (String image_path :
                (getImagesFrom(folders_and_images, folder_name))) {
            imageList.add(new Selectable_image(image_path, false));
        }
        setUpPickerImagesAdapter(imageList);
    }

    private void Load_pdfs_from_Folder(String folder_name) {
//        int column_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160,
//                getResources().getDisplayMetrics());
//        gridLayoutManager = new GridAutoFitLayoutManager(this,column_width);
        pdfList.clear();
        pdfList.addAll(Pdf_Cursor_loaderUtils.getPdfsFrom(folders_and_pdfs, folder_name));
        pdf_recycler_adapter = new Pdf_recyler_adapter(this, pdfList, this);
        gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        images_RecyclerView.setLayoutManager(gridLayoutManager);
        images_RecyclerView.setAdapter(pdf_recycler_adapter);
    }

    //Navigations
    private void AutohidePickerNavs() {
        images_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !view_btn.isShown() && !comfirm_btn.isShown()) {
                    View_Utils.ShowView_with_ZoomOut((ViewGroup) comfirm_btn.getParent());
                    view_btn.setVisibility(View.VISIBLE);
                    comfirm_btn.setVisibility(View.VISIBLE);
                    num_txt.setVisibility(View.VISIBLE);
                } else if (dy > 0 && view_btn.isShown() && comfirm_btn.isShown()) {
                    View_Utils.hideView_with_ZoomIn((ViewGroup) comfirm_btn.getParent());
                    view_btn.setVisibility(View.GONE);
                    comfirm_btn.setVisibility(View.GONE);
                    num_txt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

//                if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                    view_btn.setVisibility(View.VISIBLE);
//                    comfirm_btn.setVisibility(View.VISIBLE);
//                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void hideFolderRecycler() {
        dir_RecyclerView.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline3.getLayoutParams();
        params.guidePercent = 0.01f;
        guideline3.setLayoutParams(params);

//        constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(R.id.img_recycler, ConstraintSet.LEFT, R.id.picker_lay, ConstraintSet.LEFT);
//        constraintSet.applyTo(constraintLayout);
    }

    private void ShowFolderRecycler() {

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline3.getLayoutParams();
        params.guidePercent = 0.29f;
        guideline3.setLayoutParams(params);
//        constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(R.id.img_recycler, ConstraintSet.LEFT, R.id.guideline3, ConstraintSet.LEFT);
//        constraintSet.applyTo(constraintLayout);
        dir_RecyclerView.setVisibility(View.VISIBLE);
    }

    private void HideOrShow_DirectoryView() {
        folder_is_visible = !folder_is_visible;
        if (folder_is_visible) {
            picker_nav.setImageResource(R.drawable.hamburger);
            hideFolderRecycler();
//            if (is_pdf_folder) {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            } else {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            }
            gridLayoutManager.setSpanCount(4);
            gridLayoutManager.requestLayout();
        } else {
            picker_nav.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            ShowFolderRecycler();
//            if (is_pdf_folder) {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            } else {
//                gridLayoutManager.setSpanCount(3);
//            }
                gridLayoutManager.setSpanCount(3);
            gridLayoutManager.requestLayout();
        }


    }

    //Getters
    public ArrayList<Folder_image> getFolder_imageList() {
        return folder_imageList;
    }

    public ArrayList<Selectable_image> getImageList() {
        return imageList;
    }

    public Button getCopy_btn() {
        return copy_btn;
    }

    public AppCompatRadioButton getSelect_btn() {
        return select_btn;
    }

    public TextView getTitle_tv() {
        return title_tv;
    }

    public void setFolders_and_images(ArrayList<Model_images> folders_and_images) {
        this.folders_and_images = folders_and_images;
    }

    public String getPdf_path() {
        return pdf_path;
    }

    public void setCount(int count) {
        this.count = count;
    }

    //Not used CallBacks...
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
