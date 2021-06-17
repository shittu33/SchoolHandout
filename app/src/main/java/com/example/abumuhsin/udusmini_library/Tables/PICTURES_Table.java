package com.example.abumuhsin.udusmini_library.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.mysqlitedbconnection.csv.CSV_reader;
import com.example.mysqlitedbconnection.csv.CSV_writer;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
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

import static com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask.SHARED_TIME;
import static com.example.abumuhsin.udusmini_library.tasks.UnZippingToBookTask.SHARE_ID;
import static com.example.mysqlitedbconnection.csv.Constants.AND;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK_TITLE;
import static com.example.mysqlitedbconnection.csv.Constants.DEBUG_TABLES;
import static com.example.mysqlitedbconnection.csv.Constants.ID;
import static com.example.mysqlitedbconnection.csv.Constants.PAGES;
import static com.example.mysqlitedbconnection.csv.Constants.PAGE_INDEX;
import static com.example.mysqlitedbconnection.csv.Constants.PAGE_NUMBER;
import static com.example.mysqlitedbconnection.csv.Constants.PATH;
import static com.example.mysqlitedbconnection.csv.Constants.PICTURES;
import static com.example.mysqlitedbconnection.csv.Constants.TITLE;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class PICTURES_Table extends Table implements Serializable {

    public PICTURES_Table(DatabaseCreator database) {
        super(database,
                PICTURES,
                getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(ID),
                getString_ColumnStatement(PATH),
                getInteger_ColumnStatement(PAGE_INDEX),
                getString_ColumnStatement(BOOK_TITLE),
                getForeignKeyStatement(PAGE_INDEX, PAGES, PAGE_NUMBER),
                getForeignKeyStatement(BOOK_TITLE, BOOK, TITLE)
        );
    }

    private static final String TAG = "PICTURES_Table";
    public ArrayList<String> get_BookPaths(String book_name){
        final ArrayList<String> list = new ArrayList<>();
        Where where_book = new Where(BOOK_TITLE, book_name);
        String filter_statement = get_AND_Where_Equalstatement(where_book)
                + " "
                + get_ASCENDING_ORDER_BY_StatementForColumns(PAGE_INDEX);
        loop_column_with_listener(PATH, filter_statement, new Loop_column_listener() {
            @Override
            public void onDataAddedProgress(Cursor cursor, int columnIndex, String column_type) {
                String data = cursor.getString(columnIndex);
                list.add(data);
                Log.e(TAG," the data is " + data);
            }
        });
        return list;
//        return loop_String_Column(PATH,get_AND_Where_Equalstatement(where_book));
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void SavePicturesTOCSV(String book_name,File exprt_dir) {
        CSV_writer csv_writer;
//        File exprt_dir = new File(FileUtils.getBookFilesPath(book_name));
        if (!exprt_dir.exists()) exprt_dir.mkdirs();
        File csvFile = new File(exprt_dir, "pictures.csv");
        try {
            csvFile.createNewFile();
            csv_writer = new CSV_writer(new FileWriter(csvFile));
            Cursor csvCursor = fetch_All_Where(get_StringEqual_Sign_Statement(BOOK_TITLE, book_name));
            csv_writer.writeNext(new String[]{BOOK_TITLE,PATH,PAGE_INDEX});
            while (csvCursor.moveToNext()) {
                String[] columns = new String[3];
                columns[0] = csvCursor.getString(csvCursor.getColumnIndex(BOOK_TITLE))/*+"_share"*/;
                columns[1] = csvCursor.getString(csvCursor.getColumnIndex(PATH))/*.replace(book_name,book_name+"_share")*/;
                int page_index = csvCursor.getInt(csvCursor.getColumnIndex(PAGE_INDEX));
                columns[2] = Integer.toString(page_index);
                csv_writer.writeNext(columns);
            }
            csv_writer.close();
            csvCursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void ReadCsvToPictureTable(File csv_file) {
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
//                String book_name =csv_file.getName().replace(".csv","");
//                contentValues.put(TITLE,book_name);
                String book_name = null;
                while ((columns = csv_reader.readNext()) != null) {
                    if (i == 0) {
                        data_columns = columns;
                    }else {
                        for (int x = 0; x < columns.length; x++) {
                            String column = columns[x];
//                        Log.i(DEBUG_TABLES, "column is" + column);
                            if (i > 0) {
                                if (data_columns[x].equals(PAGE_INDEX)){
                                    contentValues.put(data_columns[x],Integer.parseInt(column));
                                }else {
                                    if (data_columns[x].equals(BOOK_TITLE)){
                                        book_name =column;
                                        boolean is_book_exist = is_book_exist(column);
                                        if (is_book_exist) {
                                            column = column + SHARE_ID + SHARED_TIME;
                                        }
                                    }else if (data_columns[x].equals(PATH)){
                                        if (book_name!=null &&  is_book_exist(book_name) ) {
                                            column = column.replace(book_name,book_name+SHARE_ID + SHARED_TIME);
                                        }
                                    }
                                    contentValues.put(data_columns[x],column);
                                }
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
    public void insert_picture(Picture_table_model picture_table_model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PATH, picture_table_model.getPicture_path());
        contentValues.put(BOOK_TITLE, picture_table_model.getBook_name());
        contentValues.put(PAGE_INDEX, picture_table_model.getPage_no());
        insert_values_to_columns(contentValues);
    }

    public ArrayList<String> getBookPictures(String book_name) {
        Where where_book = new Where(BOOK_TITLE, book_name);
        return loop_String_Column(PATH, get_AND_Where_Equalstatement(where_book));
    }

    public String getLastAddedPicturePath(String book_name, int page_no) {
        Where where_book = new Where(BOOK_TITLE, book_name);
        Where where_page = new Where(PAGE_INDEX, page_no);
        Cursor cursor = fetch_All_Where(get_AND_Where_Equalstatement(where_book, where_page));
        String path = null;
        if (cursor.moveToLast()) {
            Log.i(DEBUG_TABLES, "inside data loop");
        }
        try {
            path = cursor.getString(cursor.getColumnIndex(PATH));
            Log.i(DEBUG_TABLES, "after picture_path");
        } catch (Exception e) {
            Log.i(DEBUG_TABLES, "something is wrong with these columns");
        }
        cursor.close();
        return path;
    }

    public void updatePicture_PageIndex(int incremental_value, String book_name, int page_no) {
        Where where_book = new Where(BOOK_TITLE, book_name);
        Where where_page = new Where(PAGE_INDEX, page_no);
        String where = get_AND_Where_Equalstatement(where_book) + AND + get_Greater_Equal_Statement(where_page);
        Log.i(DEBUG_TABLES, " updatePicture_PageIndex: has this page");
        IncreaseIntegerColumnValues(PAGE_INDEX, incremental_value, where);
        Log.i(DEBUG_TABLES, " updatePicture_PageIndex: page increased");
    }

    public ArrayList<String> getPage_PicturePaths(String book_name, int page_no) {
        ArrayList<String> paths = new ArrayList<>();
        Where where_book = new Where(BOOK_TITLE, book_name);
        Where where_page = new Where(PAGE_INDEX, page_no);
        Cursor cursor = fetch_All_Where(get_AND_Where_Equalstatement(where_book, where_page));
        while (cursor.moveToNext()) {
            Log.i(DEBUG_TABLES, "inside data loop");
            try {
                String path = cursor.getString(cursor.getColumnIndex(PATH));
                Log.i(DEBUG_TABLES, "after picture_path");
                paths.add(path);
            } catch (Exception e) {
                Log.i(DEBUG_TABLES, "something is wrong with these columns");
            }
        }
        cursor.close();
        return paths;
    }

    public ArrayList<String> getPagePictures(String book_name, int page_no) {
//        Where where_id = new Where(ID, 1);
        Where where_book = new Where(BOOK_TITLE, book_name);
        Where where_page = new Where(PAGE_INDEX, page_no);
//        try {
        Log.i(DEBUG_TABLES, "trying to load pictures...");
        return loop_String_Column(PATH,
                get_AND_Where_Equalstatement(where_book, where_page)
                        + " "
                        + get_ASCENDING_ORDER_BY_StatementForColumns(PAGE_INDEX)
        );
//        } catch (Exception e) {
//            Log.i(DEBUG_TABLES, " OOps, can't read data");
//            return null;
//        }
    }

    public void deletePictureForABook(String book_name) {
        Where where_book = new Where(BOOK_TITLE, book_name);
        delete_where_using_AND(where_book);
    }

    public void deletePicture(String path, String book, int page) {
        Where where_book = new Where(BOOK_TITLE, book);
        Where where_page = new Where(PAGE_INDEX, page);
        Where where_path = new Where(PATH, path);
        del(get_AND_Where_Equalstatement(where_book, where_page, where_path));
    }
}
