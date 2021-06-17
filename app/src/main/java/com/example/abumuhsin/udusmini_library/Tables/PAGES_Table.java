package com.example.abumuhsin.udusmini_library.Tables;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.abumuhsin.udusmini_library.activities.FlipBooKActivity;
import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.Flip_model;
import com.example.abumuhsin.udusmini_library.test.TestFlipBooKActivity;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.abumuhsin.udusmini_library.utils.PdfUtils;
import com.example.mysqlitedbconnection.csv.CSV_reader;
import com.example.mysqlitedbconnection.csv.CSV_writer;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Pages_table_model;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Picture_table_model;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Where;
import com.example.mysqlitedbconnection.csv.sqlite.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask.SHARED_TIME;
import static com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask.SHARE_ID;
import static com.example.abumuhsin.udusmini_library.utils.FileUtils.GALLERY;
import static com.example.mysqlitedbconnection.csv.Constants.AND;
import static com.example.mysqlitedbconnection.csv.Constants.ASC;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK_TITLE;
import static com.example.mysqlitedbconnection.csv.Constants.COVER_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.DEBUG_TABLES;
import static com.example.mysqlitedbconnection.csv.Constants.EMPTY_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.ID;
import static com.example.mysqlitedbconnection.csv.Constants.LAST_PAGE_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.LAYOUT;
import static com.example.mysqlitedbconnection.csv.Constants.PAGES;
import static com.example.mysqlitedbconnection.csv.Constants.PAGE_NUMBER;
import static com.example.mysqlitedbconnection.csv.Constants.PAGE_PICTURE_COUNT;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.SINGLE_PAGE_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.TITLE;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class PAGES_Table extends Table implements Serializable {
    DatabaseCreator databaseCreator;

    public PAGES_Table(DatabaseCreator database) {
        super(database, PAGES,
                getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(ID),
                getInteger_ColumnStatement(PAGE_NUMBER),
                getString_ColumnStatement(BOOK_TITLE),
                getInteger_ColumnStatement(LAYOUT),
                getInteger_ColumnStatement(PAGE_PICTURE_COUNT),
                getForeignKeyStatement(BOOK_TITLE, BOOK, TITLE));
        databaseCreator = database;
    }

    public void add_a_page(Pages_table_model pages_table_model) {
        ContentValues contentValues = new ContentValues();
        Where where_book = new Where(BOOK_TITLE, pages_table_model.getBook_name());
        Where where_page = new Where(PAGE_NUMBER, pages_table_model.getPage_no());
        String where = get_AND_Where_Equalstatement(where_book) + AND + get_Greater_Equal_Statement(where_page);
//        Log.i(DEBUG_TABLES,"previous page number is " +  getLastPageInfo(pages_table_model.getBook_name()).getPage_no());
//            Log.i(DEBUG_TABLES,"New page number is " +  getLastPageInfo(pages_table_model.getBook_name()).getPage_no());
        if (is_valueExistInColumn(PAGE_NUMBER, pages_table_model.getPage_no(), get_AND_Where_Equalstatement(where_book))) {
            IncreaseIntegerColumnValues(PAGE_NUMBER, 1, where);
            Log.i(DEBUG_TABLES, " add_a_page: page increased");
            new PICTURES_Table(databaseCreator).updatePicture_PageIndex(1, pages_table_model.getBook_name(), pages_table_model.getPage_no());
        } else {
            Log.i(DEBUG_TABLES, "page number does not exist");
        }
        contentValues.put(PAGE_NUMBER, pages_table_model.getPage_no());
        contentValues.put(BOOK_TITLE, pages_table_model.getBook_name());
        contentValues.put(LAYOUT, pages_table_model.getLayout_type());
        contentValues.put(PAGE_PICTURE_COUNT, pages_table_model.getPicture_count());
        insert_values_to_columns(contentValues);
        new BOOK_Table(databaseCreator).update_book_pageCount(pages_table_model.getBook_name(), 1);
    }

    public Pages_table_model getLastPageInfo(String book_name) {
//        Cursor cursor = fetch_All_Where_OrderedBy(get_Equal_Sign_Statement(BOOK_TITLE, book_name), PAGE_NUMBER, ASC);
        Cursor cursor = fetch_All_Where(get_Equal_Sign_Statement(BOOK_TITLE, book_name));
        if (cursor.moveToLast()) {
            Log.i(DEBUG_TABLES, "inside data loop");
        }
        int page_no = 1;
        int layout_type = SINGLE_PAGE_TYPE;
        int pic_count = EMPTY_PAGE;
        try {
            layout_type = cursor.getInt(cursor.getColumnIndex(LAYOUT));
            Log.i(DEBUG_TABLES, " getLastPageInfo: after layout_type");
            page_no = cursor.getInt(cursor.getColumnIndex(PAGE_NUMBER));
            Log.i(DEBUG_TABLES, "getLastPageInfo: after page_no");
            pic_count = cursor.getInt(cursor.getColumnIndex(PAGE_PICTURE_COUNT));
            Log.i(DEBUG_TABLES, "getLastPageInfo: after page_count");
            cursor.close();
        } catch (Exception e) {
            Log.i(DEBUG_TABLES, "getLastPageInfo: something is wrong with these columns");
        }
        Log.i(DEBUG_TABLES, "page_no is " + page_no + " layout_type is " + layout_type + " pic_count is " + pic_count);
        return new Pages_table_model(book_name, page_no, layout_type, pic_count);
    }

    public Thread LoadAllPage(final FlipBooKActivity flipBooKActivity, final String book_name) {
        final PICTURES_Table pictures_table = new PICTURES_Table(databaseCreator);
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = fetch_All_Where_OrderedBy(get_Equal_Sign_Statement(BOOK_TITLE, book_name), PAGE_NUMBER, ASC);
                flipBooKActivity.getFlip_list().clear();
                flipBooKActivity.getFlip_list().add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Log.i(DEBUG_TABLES, "LoadAllPage: inside data loop");
                        int page_no;
                        int layout_type;
                        int pic_count;
                        String image_path = "";
                        try {
                            layout_type = cursor.getInt(cursor.getColumnIndex(LAYOUT));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after layout_type");
                            page_no = cursor.getInt(cursor.getColumnIndex(PAGE_NUMBER));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after page_no");
                            pic_count = cursor.getInt(cursor.getColumnIndex(PAGE_PICTURE_COUNT));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after page_count");
                            Log.i(DEBUG_TABLES, "index is " + page_no + " pic_count is " + pic_count);
                            if (pic_count == SINGLE_PAGE) {
                                Log.i(DEBUG_TABLES, "LoadAllPage: has picture");
                                image_path = pictures_table.getPage_PicturePaths(book_name, page_no).get(0);
                                Log.i(DEBUG_TABLES, "LoadAllPage: got the picture");
                            }

                            try {
                                Log.i(DEBUG_TABLES, "index " + page_no + " is to be added");
                                flipBooKActivity.getFlip_list().add(page_no, new Flip_model(layout_type, image_path, pic_count));
                                Log.i(DEBUG_TABLES, "index " + page_no + " is not out of bound");
                            } catch (IndexOutOfBoundsException e) {
                                Log.i(DEBUG_TABLES, "index " + page_no + " is out of bound");
                            }
                        } catch (Exception e) {
                            Log.i(DEBUG_TABLES, "LoadAllPage: something is wrong with these columns");
                        }
                    }
                    cursor.close();

                } else {
                    Log.i(DEBUG_TABLES, "LoadAllPage: cursor is null");
                }
                flipBooKActivity.getFlip_list().add(new Flip_model(LAST_PAGE_TYPE, "back_cover", EMPTY_PAGE));
                flipBooKActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flipBooKActivity.getFlipBook_adapter().notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public Thread adding_pageInBackgroundTest(final Activity activity, final String pdf_name,final int page_no, final String current_book
            , final boolean is_from_pdf, final AddingPageListener addingPageListener, final Pages_table_model... mpages_table_model) {
//        page_no = activity.current_page_index/*>0?activity.current_page_index-1:0*/;
//        final String current_book = activity.current_book;
        pages_edited = new ArrayList<>();

        return new Thread(new Runnable() {
            @Override
            public void run() {
                if (is_from_pdf) {
                    for (int i = 0; i < mpages_table_model.length; i++) {
                        Pages_table_model pages_table_model = mpages_table_model[i];
                        File pdf_page = new File(pages_table_model.getSingle_picutre());
                        String pdf_page_path = pdf_page.getPath() + ".done";
                        if (!pdf_page.exists()) {
                            int pdf_page_no = PdfUtils.getPdf_page_no(pages_table_model.getSingle_picutre());
                            Log.i(DEBUG_TABLES, "is_from_pdf: page no is " + String.valueOf(pdf_page_no));
                            PdfUtils.SaveZoomablePdfPage_to(activity, pdf_name, pdf_page_path, pdf_page_no);
                            Log.i(DEBUG_TABLES, "is_from_pdf: page " + String.valueOf(pdf_page_no) + " is saved!!!");
                        } else {
                            if (pdf_page.renameTo(new File(pdf_page_path))) {
                                Log.i("pdf_bitmap", "this pdf_page_exist b4 but  rename to " +
                                        pdf_page_path);
                            }
                        }
                        addingPageListener.onAddingProgress(i, is_from_pdf);
                    }
                    if (mpages_table_model[0].getPicture_count() == SINGLE_PAGE) {
                        addingPageListener.onSavePdfFinished(mpages_table_model, is_from_pdf);
                    }
                }
                for (int i = 0; i < mpages_table_model.length; i++) {
                    Pages_table_model pages_table_model = mpages_table_model[i];
                    add_a_page(pages_table_model);
                    Pages_table_model pages_table_model_data = getLastPageInfo(pages_table_model.getBook_name());
                    String img_path = "";
                    if (pages_table_model_data.getPicture_count() == SINGLE_PAGE) {
//                        Log.i(DEBUG_TABLES, "adding_pageInBackground: has a picture");
                        File save_file = null;
                        if (is_from_pdf) {
                            save_file = new File(pages_table_model.getSingle_picutre() + ".done");
                        } else {
                            save_file = FileUtils.saveAPathAsPages(activity, current_book, pages_table_model.getSingle_picutre(), GALLERY);
                            addingPageListener.onAddingProgress(i, false);
                        }
                        new PICTURES_Table(databaseCreator).insert_picture(
                                new Picture_table_model(
                                        pages_table_model.getPage_no()
                                        , pages_table_model.getBook_name()
                                        , save_file != null ? save_file.getPath() : pages_table_model.getSingle_picutre()));
                        addingPageListener.onAddingProgress(i,is_from_pdf);
                        img_path = new PICTURES_Table(databaseCreator).getPagePictures(pages_table_model_data.getBook_name(), pages_table_model_data.getPage_no()).get(0);
                    }
                    int tmp_pg_no = pages_table_model_data.getPage_no();
                    Flip_model flip_model = new Flip_model(pages_table_model_data.getLayout_type(), img_path, pages_table_model_data.getPicture_count());
                    addingPageListener.onListReadyToUpdate(tmp_pg_no,flip_model,is_from_pdf);
                    pages_edited.add(new PageInfo(tmp_pg_no, img_path));
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mpages_table_model[0].getPicture_count() == SINGLE_PAGE) {
                            addingPageListener.onPageAdded();
                        }
//                        if (!is_from_pdf) {
                            onBookChanged.onBookChanged(current_book, pages_edited, false);
//                        }
                    }
                });

            }
        });
    }

    public void RemoveAPageTest(final Activity activity, final LinkedList<Flip_model> flip_list, final String current_book, final int page_no) {
//        final String current_book = flipBooKActivity.current_book;
        final Where where_book = new Where(BOOK_TITLE, current_book);
        final Where where_page = new Where(PAGE_NUMBER, page_no);
        final String where = get_AND_Where_Equalstatement(where_book) + AND + get_Greater_Equal_Statement(where_page);
        new Thread(new Runnable() {
            @Override
            public void run() {
                delete_where_using_AND(where_book, where_page);
                Log.i(DEBUG_TABLES, "RemoveAPage: page deleted in pages table");
                new BOOK_Table(databaseCreator).update_book_pageCount(current_book, -1);
                Log.i(DEBUG_TABLES, "RemoveAPage: page no decreased in Book table");
                IncreaseIntegerColumnValues(PAGE_NUMBER, -1, where);
                Log.i(DEBUG_TABLES, "RemoveAPage:  add_a_page: page increased");
                if (flip_list.get(page_no).getPic_count() > EMPTY_PAGE) {
                    //del pic
                    MyBook_fragment.pic_table.deletePicture(flip_list.get(page_no).getImage_path()
                            , current_book, page_no);
                    new PICTURES_Table(databaseCreator).updatePicture_PageIndex(-1, current_book, page_no);
                    Log.i(DEBUG_TABLES, "RemoveAPage: Image deleted from pic table");
                }
                flip_list.remove(page_no);
                Log.i(DEBUG_TABLES, "RemoveAPage: Image path removed from adapter");
                Runnable selection_runnable = new Runnable() {
                    public void run() {
//                        flipBooKActivity.getFlipBooKView().RefreshBookData();
                        List<PageInfo> pages_removed = Collections.singletonList(
                                new PageInfo(page_no, flip_list.get(page_no).getImage_path()));
                        onBookChanged.onBookChanged(current_book, pages_removed, true);
                        Log.i(DEBUG_TABLES, "RemoveAPage: adapter notified");
                        Toast.makeText(activity, "page removed", Toast.LENGTH_SHORT).show();
                    }
                };
                activity.runOnUiThread(selection_runnable);
            }
        }).start();
    }

    public Thread LoadAllPageTest(final TestFlipBooKActivity flipBooKActivity, final String book_name) {
        final PICTURES_Table pictures_table = new PICTURES_Table(databaseCreator);
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = fetch_All_Where_OrderedBy(get_Equal_Sign_Statement(BOOK_TITLE, book_name), PAGE_NUMBER, ASC);
                flipBooKActivity.getFlip_list().clear();
                flipBooKActivity.getFlip_list().add(new Flip_model(COVER_TYPE, "cover_test", EMPTY_PAGE));
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Log.i(DEBUG_TABLES, "LoadAllPage: inside data loop");
                        int page_no;
                        int layout_type;
                        int pic_count;
                        String image_path = "";
                        try {
                            layout_type = cursor.getInt(cursor.getColumnIndex(LAYOUT));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after layout_type");
                            page_no = cursor.getInt(cursor.getColumnIndex(PAGE_NUMBER));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after page_no");
                            pic_count = cursor.getInt(cursor.getColumnIndex(PAGE_PICTURE_COUNT));
                            Log.i(DEBUG_TABLES, "LoadAllPage: after page_count");
                            Log.i(DEBUG_TABLES, "index is " + page_no + " pic_count is " + pic_count);
                            if (pic_count == SINGLE_PAGE) {
                                Log.i(DEBUG_TABLES, "LoadAllPage: has picture");
                                image_path = pictures_table.getPage_PicturePaths(book_name, page_no).get(0);
                                Log.i(DEBUG_TABLES, "LoadAllPage: got the picture");
                            }

                            try {
                                Log.i(DEBUG_TABLES, "index " + page_no + " is to be added");
                                flipBooKActivity.getFlip_list().add(page_no, new Flip_model(layout_type, image_path, pic_count));
                                Log.i(DEBUG_TABLES, "index " + page_no + " is not out of bound");
                            } catch (IndexOutOfBoundsException e) {
                                Log.i(DEBUG_TABLES, "index " + page_no + " is out of bound");
                            }
                        } catch (Exception e) {
                            Log.i(DEBUG_TABLES, "LoadAllPage: something is wrong with these columns");
                        }
                    }
                    cursor.close();

                } else {
                    Log.i(DEBUG_TABLES, "LoadAllPage: cursor is null");
                }
                flipBooKActivity.getFlip_list().add(new Flip_model(LAST_PAGE_TYPE, "back_cover", EMPTY_PAGE));
                flipBooKActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flipBooKActivity.getFlipBooKView().RefreshBookData();
                    }
                });
            }
        });
    }

    public void add_A_PictureToCurrentPage(final FlipBooKActivity flipBooKActivity, final String picture) {
        final PICTURES_Table pictures_table = new PICTURES_Table(databaseCreator);
        final int page_no = flipBooKActivity.current_page_index;
        final String book_name = flipBooKActivity.current_book;
        final Where where_book = new Where(BOOK_TITLE, book_name);
        final Where where_page = new Where(PAGE_NUMBER, page_no);
        new Thread(new Runnable() {
            @Override
            public void run() {
                update_Column_many_Where(PAGE_PICTURE_COUNT, SINGLE_PAGE, AND, where_book, where_page);
                pictures_table.insert_picture(new Picture_table_model(page_no, book_name, picture));
                String img_path = pictures_table.getLastAddedPicturePath(book_name, page_no);
                Log.i(DEBUG_TABLES, "img_path is " + img_path);
//                Flip_model Flip_model = new Flip_model(SINGLE_PAGE_TYPE, img_path, SINGLE_PAGE);
                Flip_model prev_flip_model = flipBooKActivity.getFlip_list().get(page_no);
                if (prev_flip_model.getPic_count() != SINGLE_PAGE) {
                    prev_flip_model.setPic_count(SINGLE_PAGE);
                }
                prev_flip_model.setImage_path(img_path);
                flipBooKActivity.getFlip_list().set(page_no, prev_flip_model);
                flipBooKActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flipBooKActivity.flipBook_adapter.notifyDataSetChanged();
                        List<PageInfo> page_edited = Arrays.asList(
                                new PageInfo(page_no, flipBooKActivity.getFlip_list().get(page_no).getImage_path()));
                        onBookChanged.onBookChanged(book_name, page_edited, false);

                    }
                });
            }
        }).start();
    }

    int page_no;
    ProgressDialog dialog;
    ArrayList<PageInfo> pages_edited;

    public Thread adding_pageInBackground(final boolean is_from_pdf, final FlipBooKActivity flipBooKActivity, final Pages_table_model... mpages_table_model) {
        page_no = flipBooKActivity.current_page_index/*>0?flipBooKActivity.current_page_index-1:0*/;
        final String current_book = flipBooKActivity.current_book;
        pages_edited = new ArrayList<>();
        if (mpages_table_model[0].getPicture_count() == SINGLE_PAGE) {
            dialog = new ProgressDialog(flipBooKActivity);
//            dialog.setProgressStyle(R.style.Dial);
            dialog.setTitle("Saving pages...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(mpages_table_model.length);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }
        return new Thread(new Runnable() {
            @Override
            public void run() {
                if (is_from_pdf) {
                    for (int i = 0; i < mpages_table_model.length; i++) {
                        Pages_table_model pages_table_model = mpages_table_model[i];
                        File pdf_page = new File(pages_table_model.getSingle_picutre());
                        String pdf_path = flipBooKActivity.pdf_name;
                        String pdf_page_path = pdf_page.getPath() + ".done";
                        if (!pdf_page.exists()) {
                            int pdf_page_no = PdfUtils.getPdf_page_no(pages_table_model.getSingle_picutre());
                            Log.i(DEBUG_TABLES, "is_from_pdf: page no is " + String.valueOf(pdf_page_no));
                            PdfUtils.SaveZoomablePdfPage_to(flipBooKActivity, pdf_path, pdf_page_path, pdf_page_no);
                            Log.i(DEBUG_TABLES, "is_from_pdf: page " + String.valueOf(pdf_page_no) + " is saved!!!");
                        } else {
                            if (pdf_page.renameTo(new File(pdf_page_path))) {
                                Log.i("pdf_bitmap", "this pdf_page_exist b4 but  rename to " +
                                        pdf_page_path);
                            }
                        }
                        dialog.setProgress(i);
                    }
                    if (mpages_table_model[0].getPicture_count() == SINGLE_PAGE) {
                        flipBooKActivity.runOnUiThread(new Runnable() {
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
                for (int i = 0; i < mpages_table_model.length; i++) {
                    Pages_table_model pages_table_model = mpages_table_model[i];
                    add_a_page(pages_table_model);
                    Pages_table_model pages_table_model_data = getLastPageInfo(pages_table_model.getBook_name());
                    String img_path = "";
                    if (pages_table_model_data.getPicture_count() == SINGLE_PAGE) {
//                        Log.i(DEBUG_TABLES, "adding_pageInBackground: has a picture");
                        File save_file = null;
                        if (is_from_pdf) {
                            save_file = new File(pages_table_model.getSingle_picutre() + ".done");
                        } else {
                            save_file = flipBooKActivity.saveAPathAsPages(pages_table_model.getSingle_picutre(), GALLERY);
                            dialog.setProgress(i);
                        }
                        new PICTURES_Table(databaseCreator).insert_picture(
                                new Picture_table_model(
                                        pages_table_model.getPage_no()
                                        , pages_table_model.getBook_name()
                                        , save_file != null ? save_file.getPath() : pages_table_model.getSingle_picutre()));
                        dialog.setProgress(i);
                        img_path = new PICTURES_Table(databaseCreator).getPagePictures(pages_table_model_data.getBook_name(), pages_table_model_data.getPage_no()).get(0);
//                        Log.i(DEBUG_TABLES, "adding_pageInBackground: got " + img_path);
                    }
                    page_no = pages_table_model_data.getPage_no();
                    Flip_model flip_model = new Flip_model(pages_table_model_data.getLayout_type(), img_path, pages_table_model_data.getPicture_count());
                    try {
                        flipBooKActivity.getFlip_list().add(pages_table_model_data.getPage_no(), flip_model);
//                        Log.i(DEBUG_TABLES, "adding_pageInBackground: Flip_model added");
                    } catch (IndexOutOfBoundsException e) {
//                        Log.i(DEBUG_TABLES, "index " + pages_table_model_data.getPage_no() + " is out of bound");
                        e.printStackTrace();
                    }
                    pages_edited.add(new PageInfo(page_no, img_path));
                    page_no++;
                }
                flipBooKActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.i(DEBUG_TABLES, "adding_pageInBackground: saved");
                        if (mpages_table_model[0].getPicture_count() == SINGLE_PAGE) {
                            dialog.dismiss();
                        }
                        flipBooKActivity.mAdapterFlipView.setSelection(page_no);
                        if (!is_from_pdf) {
                            onBookChanged.onBookChanged(current_book, pages_edited, false);
                        }
                    }
                });

            }
        });
    }

//    public void deletePictureFromThisPage(final FlipBooKActivity flipBooKActivity, final Flip_model flip_model) {
//        page_no = flipBooKActivity.current_page_index;
//        final String current_book = flipBooKActivity.current_book;
//        final Where where_book = new Where(BOOK_TITLE, current_book);
//        final Where where_page = new Where(PAGE_NUMBER, flipBooKActivity.current_page_index);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (flip_model.getPic_count() > EMPTY_PAGE) {
//                    update_Column_many_Where(PAGE_PICTURE_COUNT, EMPTY_PAGE, AND, where_book, where_page);
//                    MyBook_fragment.pic_table.deletePicture(flip_model.getImage_path(), current_book, flipBooKActivity.current_page_index);
//                    flip_model.setPic_count(EMPTY_PAGE);
//                    flip_model.setImage_path("");
//                    flipBooKActivity.getFlip_list().set(flipBooKActivity.current_page_index, flip_model);
//                    flipBooKActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            flipBooKActivity.flipBook_adapter.notifyDataSetChanged();
//                            List<PageInfo> pages_removed = Arrays.asList(
//                                    new PageInfo(page_no, flipBooKActivity.getFlip_list().get(page_no).getImage_path()));
//                            onBookChanged.onBookChanged(current_book, pages_removed, true);
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    void deleteAllPages(String book_name) {
        final Where where_book = new Where(BOOK_TITLE, book_name);
        delete_where_using_AND(where_book);
        new PICTURES_Table(databaseCreator).deletePictureForABook(book_name);
    }

    public void RemoveAPage(final FlipBooKActivity flipBooKActivity, final int page_no) {
        final String current_book = flipBooKActivity.current_book;
        final Where where_book = new Where(BOOK_TITLE, current_book);
        final Where where_page = new Where(PAGE_NUMBER, page_no);
        final String where = get_AND_Where_Equalstatement(where_book) + AND + get_Greater_Equal_Statement(where_page);
        new Thread(new Runnable() {
            @Override
            public void run() {
                delete_where_using_AND(where_book, where_page);
                Log.i(DEBUG_TABLES, "RemoveAPage: page deleted in pages table");
                new BOOK_Table(databaseCreator).update_book_pageCount(flipBooKActivity.current_book, -1);
                Log.i(DEBUG_TABLES, "RemoveAPage: page no decreased in Book table");
                IncreaseIntegerColumnValues(PAGE_NUMBER, -1, where);
                Log.i(DEBUG_TABLES, "RemoveAPage:  add_a_page: page increased");
                if (flipBooKActivity.getFlip_list().get(page_no).getPic_count() > EMPTY_PAGE) {
                    //del pic
                    MyBook_fragment.pic_table.deletePicture(flipBooKActivity.getFlip_list().get(page_no).getImage_path()
                            , flipBooKActivity.current_book, page_no);
                    new PICTURES_Table(databaseCreator).updatePicture_PageIndex(-1, flipBooKActivity.current_book, page_no);
                    Log.i(DEBUG_TABLES, "RemoveAPage: Image deleted from pic table");
                }
                flipBooKActivity.getFlip_list().remove(page_no);
                Log.i(DEBUG_TABLES, "RemoveAPage: Image path removed from adapter");
                Runnable selection_runnable = new Runnable() {
                    public void run() {
                        flipBooKActivity.mAdapterFlipView.setSelection(page_no);
                        List<PageInfo> pages_removed = Arrays.asList(
                                new PageInfo(page_no, flipBooKActivity.getFlip_list().get(page_no).getImage_path()));
                        onBookChanged.onBookChanged(current_book, pages_removed, true);
                        Log.i(DEBUG_TABLES, "RemoveAPage: adapter notified");
                        Toast.makeText(flipBooKActivity, "page removed", Toast.LENGTH_SHORT).show();
                    }
                };
                flipBooKActivity.runOnUiThread(selection_runnable);
            }
        }).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void WritePagesToCSV(final String book_name, File exprt_dir) {
        CSV_writer csv_writer;
        if (!exprt_dir.exists()) exprt_dir.mkdirs();
        File csvFile = new File(exprt_dir, "pages.csv");
        try {
            csvFile.createNewFile();
            csv_writer = new CSV_writer(new FileWriter(csvFile));
            Cursor csvCursor = fetch_All_Where(get_StringEqual_Sign_Statement(BOOK_TITLE, book_name));
            csv_writer.writeNext(new String[]{PAGE_NUMBER, LAYOUT, PAGE_PICTURE_COUNT, BOOK_TITLE});
            while (csvCursor.moveToNext()) {
                String[] columns = new String[4];
                int page_no = csvCursor.getInt(csvCursor.getColumnIndex(PAGE_NUMBER));
                columns[0] = Integer.toString(page_no);
                int layout_type = csvCursor.getInt(csvCursor.getColumnIndex(LAYOUT));
                columns[1] = Integer.toString(layout_type);
                int page_pic_count = csvCursor.getInt(csvCursor.getColumnIndex(PAGE_PICTURE_COUNT));
                columns[2] = Integer.toString(page_pic_count);
                columns[3] = csvCursor.getString(csvCursor.getColumnIndex(BOOK_TITLE)) /*+ "_share"*/;
                csv_writer.writeNext(columns);
            }
            csv_writer.close();
            csvCursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ReadCsvToPagesTable(File csv_file) {
        CSV_reader csv_reader = null;
        try {
            csv_reader = new CSV_reader(new InputStreamReader(new FileInputStream(csv_file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (csv_reader != null) {
            try {
                String[] columns;
                String[] data_columns = null;
                int i = 0;
                ContentValues contentValues = new ContentValues();
//                String book_name = csv_file.getName().replace(".csv", "");
//                contentValues.put(TITLE,book_name);
                while ((columns = csv_reader.readNext()) != null) {
                    if (i == 0) {
                        data_columns = columns;
                    } else {
                        for (int x = 0; x < columns.length; x++) {
                            String column = columns[x];
                            if (i > 0) {
                                if (data_columns[x].equals(BOOK_TITLE)) {
                                    boolean is_book_exist = is_book_exist(column);
                                    if (is_book_exist) {
                                        column = column + SHARE_ID + SHARED_TIME;
                                    }
                                    contentValues.put(data_columns[x], column);
                                } else {
                                    contentValues.put(data_columns[x], Integer.parseInt(column));
                                }
//                                contentValues.put(data_columns[x], column);
                                Log.i(DEBUG_TABLES, data_columns[x] + " = " + column);
                            }
                        }
                        insert_values_to_columns(contentValues);
                    }
                    i++;
                }
            } catch (IOException e) {
                Log.i(DEBUG_TABLES, "readnext is not working");
                e.printStackTrace();
            }
        } else {
            Log.i(DEBUG_TABLES, "csv_reader is null");
        }
    }

    public boolean is_book_exist(String book) {
        return is_In_Column(BOOK_TITLE, book);
    }
    public ArrayList<ArrayList> getPagesInfo(String book_name) {
        Where where_book = new Where(BOOK_TITLE, book_name);
        return loop_column_list_where(get_AND_Where_Equalstatement(where_book), PAGE_NUMBER, LAYOUT);
    }

    private OnBookChanged onBookChanged;

    public void setOnBookChanged(OnBookChanged onBookChanged) {
        this.onBookChanged = onBookChanged;
    }

    public class PageInfo {
        private int page_no;
        private String page_path;

        PageInfo(int page_no, String page_path) {
            this.page_no = page_no;
            this.page_path = page_path;
        }
    }

    public interface AddingPageListener {
        void onAddingProgress(int i, boolean is_from_pdf);

        void onPageAdded();

        void onSavePdfFinished(Pages_table_model[] mpages_table_model, boolean is_from_pdf);

        void onListReadyToUpdate(int tmp_pg_no, Flip_model flip_model, boolean is_from_pdf);
    }

    public interface OnBookChanged {
        void onBookChanged(String book_name, List<PageInfo> page_images, boolean is_removed);
    }
}
