package com.example.abumuhsin.udusmini_library.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.abumuhsin.udusmini_library.fragments.MyBook_fragment;
import com.example.abumuhsin.udusmini_library.models.HandoutFilter;
import com.example.abumuhsin.udusmini_library.models.LocalHandout;
import com.example.abumuhsin.udusmini_library.models.top_filter_model;
import com.example.abumuhsin.udusmini_library.utils.FileUtils;
import com.example.mysqlitedbconnection.csv.CSV_reader;
import com.example.mysqlitedbconnection.csv.CSV_writer;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Book_table_model;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Where;
import com.example.mysqlitedbconnection.csv.sqlite.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import static com.example.abumuhsin.udusmini_library.utils.Constants.Handout_ID;
import static com.example.mysqlitedbconnection.csv.Constants.AND;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK;
import static com.example.mysqlitedbconnection.csv.Constants.BOOK_TYPE;
import static com.example.mysqlitedbconnection.csv.Constants.COURSE_CODE;
import static com.example.mysqlitedbconnection.csv.Constants.DATE_CREATED;
import static com.example.mysqlitedbconnection.csv.Constants.DEBUG_TABLES;
import static com.example.mysqlitedbconnection.csv.Constants.FIRST_PAGE;
import static com.example.mysqlitedbconnection.csv.Constants.NO_OF_PAGES;
import static com.example.mysqlitedbconnection.csv.Constants.OR;
import static com.example.mysqlitedbconnection.csv.Constants.TITLE;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class BOOK_Table extends Table implements Serializable {
    PAGES_Table pages_table;
    HandoutFilterTable handoutFilterTable;

    public BOOK_Table(DatabaseCreator database) {
        super(database, BOOK,
                getStringColumn_with_Only_PrimaryKeyStatement(TITLE),
                getString_ColumnStatement(COURSE_CODE),
                getString_ColumnStatement(BOOK_TYPE),
                getString_ColumnStatement(FIRST_PAGE),
                getInteger_ColumnStatement(NO_OF_PAGES),
                getDateColumnWithDefaultDate(DATE_CREATED));
        pages_table = new PAGES_Table(database);
        handoutFilterTable = new HandoutFilterTable(database);
    }


    public void Create_a_new_Book(Book_table_model book_table_model, HandoutFilter handoutFilter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, book_table_model.getBook_name());
        contentValues.put(COURSE_CODE, book_table_model.getCourse_code());
        contentValues.put(BOOK_TYPE, book_table_model.getBook_type());
        contentValues.put(FIRST_PAGE, book_table_model.getFirst_page());
        contentValues.put(NO_OF_PAGES, book_table_model.getNo_of_pages());
        insert_values_to_columns(contentValues);
        handoutFilterTable.savefilter(handoutFilter);

    }

    public void update_book_pageCount(String book, int increment_value) {
        IncreaseIntegerColumnValues(NO_OF_PAGES, increment_value, get_Equal_Sign_Statement(TITLE, book));
    }

    public boolean is_book_exist(String book) {
        return is_In_Column(TITLE, book);
    }

    public LocalHandout getLastBookInfo() {
        Cursor cursor = fetchAll();
        LocalHandout localHandout = new LocalHandout();
        if (cursor != null) {
            cursor.moveToLast();
            Log.i(DEBUG_TABLES, "inside data loop");
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            Log.i(DEBUG_TABLES, "after title");
            String course_code = cursor.getString(cursor.getColumnIndex(COURSE_CODE));
            Log.i(DEBUG_TABLES, "after couse code");
            String first_page = cursor.getString(cursor.getColumnIndex(FIRST_PAGE));
            Log.i(DEBUG_TABLES, "after first_page");
            String book_type = cursor.getString(cursor.getColumnIndex(BOOK_TYPE));
            Log.i(DEBUG_TABLES, "after book_type");
            int no_of_pages = cursor.getInt(cursor.getColumnIndex(NO_OF_PAGES));
            Log.i(DEBUG_TABLES, "after no_of_pages");
            localHandout.setTitle(title);
            localHandout.setCover(first_page);
            localHandout.setPage_no(no_of_pages);
            localHandout.setCourse_code(course_code);
            localHandout.setCover_type(book_type);
            return localHandout;
        } else {
            Log.i(DEBUG_TABLES, "cursor is null");
        }
        return null;
    }

    public void LoadBookInfo(final MyBook_fragment myBook_fragment) {
        Log.i(DEBUG_TABLES, "inside LoadBookInfo");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(DEBUG_TABLES, "Loading started");
                Cursor cursor = fetchAll();
                if (cursor != null) {
                    myBook_fragment.getPics_title_list().clear();
                    while (cursor.moveToNext()) {
                        Log.i(DEBUG_TABLES, "inside data loop");
                        String title = cursor.getString(cursor.getColumnIndex(TITLE));
                        int no_of_pages = cursor.getInt(cursor.getColumnIndex(NO_OF_PAGES));
                        String first_page = cursor.getString(cursor.getColumnIndex(FIRST_PAGE));
                        String course_code = cursor.getString(cursor.getColumnIndex(COURSE_CODE));
                        Log.i(DEBUG_TABLES, "after couse code");
                        myBook_fragment.getPics_title_list().add(new LocalHandout(title, course_code, first_page, no_of_pages));
                        //noinspection ConstantConditions
                        myBook_fragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myBook_fragment.getGrid_adapter().notifyDataSetChanged();
                            }
                        });
                    }
                    cursor.close();
                } else {
                    Log.i(DEBUG_TABLES, "cursor is null");
                }
            }
        }).start();
    }

    public void LoadBookInfoWhere(final MyBook_fragment myBook_fragment, final String where) {
        Log.i(DEBUG_TABLES, "inside LoadBookInfo");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(DEBUG_TABLES, "Loading started");
                Cursor cursor = fetch_All_Where(where);
                if (cursor != null) {
                    myBook_fragment.getPics_title_list().clear();
                    while (cursor.moveToNext()) {
                        Log.i(DEBUG_TABLES, "inside data loop");
                        String title = cursor.getString(cursor.getColumnIndex(TITLE));
                        int no_of_pages = cursor.getInt(cursor.getColumnIndex(NO_OF_PAGES));
                        String first_page = cursor.getString(cursor.getColumnIndex(FIRST_PAGE));
                        String course_code = cursor.getString(cursor.getColumnIndex(COURSE_CODE));
                        Log.i(DEBUG_TABLES, "after couse code");
                        myBook_fragment.getPics_title_list().add(new LocalHandout(title, course_code, first_page, no_of_pages));
                        //noinspection ConstantConditions
                        myBook_fragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myBook_fragment.getGrid_adapter().notifyDataSetChanged();
                            }
                        });
                    }
                    cursor.close();
                } else {
                    Log.i(DEBUG_TABLES, "cursor is null");
                }
            }
        }).start();
    }

    public String getWhereFromFilterList(List<top_filter_model> secondList) {
        final StringBuilder where_statements = new StringBuilder();
        for (top_filter_model top_filter_model : secondList) {
            if (top_filter_model.getFilter().equals("Dept")) {
                continue;
            }
            String where_statement = get_Equal_Sign_Statement(top_filter_model.getFilter_column(), top_filter_model.getFilter());
            where_statements.append(where_statement);
            where_statements.append(AND);
        }
        if (where_statements.toString().endsWith(AND)) {
            deleteLastOperatorfromWhere_statement(where_statements, AND);
        }
        Log.i(DEBUG_TABLES, "the list where is " + where_statements.toString());
        return where_statements.toString();
    }

    public void LoadHandoutWithFilter(MyBook_fragment myBook_fragment, String clicked_text, String filter_column
            , List<top_filter_model> secondList) {
        String where_statement = getWhereFromFilterList(secondList);
        final StringBuilder where_handout_ids = new StringBuilder();

        handoutFilterTable.loop_column_WithListener(Handout_ID, String.class, where_statement, new OnColumnMoveToNext() {
            @Override
            public void onMoveToNext(Object row_data, Class class_type) {
                String row_data1 = (String) row_data;
                String where_statement = get_Equal_Sign_Statement(TITLE, row_data1);
                where_handout_ids.append(where_statement);
                where_handout_ids.append(OR);
            }
        });
        if (where_handout_ids.toString().endsWith(OR)) {
            deleteLastOperatorfromWhere_statement(where_handout_ids, OR);
        }
        Log.i(DEBUG_TABLES, where_handout_ids.toString());

        LoadBookInfoWhere(myBook_fragment, where_handout_ids.toString());
    }

    public void DeleteBook(final MyBook_fragment book_fragment, final String book_name) {
        final Where wherebook = new Where(TITLE, book_name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                delete_where_using_AND(wherebook);
                handoutFilterTable.deleteAllBookFilters(book_name);
                pages_table.deleteAllPages(book_name);
                File book_dir = new File(FileUtils.getBookFilesPath(book_name));
                File[] files = book_dir.listFiles();
                if (book_dir.exists()) {
                    for (File filel : files) {
                        if (filel.isDirectory()) {
//                            File book_file = new File(FileUtils.getImagePagesFilePath(book_name));
                            if (filel.exists()) {
                                File[] images = filel.listFiles();
                                for (File image_file : images) {
                                    if (image_file.delete()) {
                                        Log.i(DEBUG_TABLES, "one image was deleted");
                                    }
                                }
                            }
                            if (filel.delete()) {
                                Log.i(DEBUG_TABLES, "the img_dir is deleted");
                            }
                        } else {
                            if (filel.delete()) {
                                Log.i(DEBUG_TABLES, "one file was deleted");
                            }
                        }
                    }
                }
                if (book_dir.delete()) {
//                    if (new File(FileUtils.getBookFilesPath(book_name)).delete()) {
//                    }
                    Log.i(DEBUG_TABLES, "the book_dir is deleted");
                }
            }
        }).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void WriteBookToCSV(final String book_name, File exprt_dir) {
        CSV_writer csv_writer;
        if (!exprt_dir.exists()) exprt_dir.mkdirs();
        File csvFile = new File(exprt_dir, "books.csv");
        try {
            csvFile.createNewFile();
            csv_writer = new CSV_writer(new FileWriter(csvFile));
            Cursor csvCursor = fetch_All_Where(get_StringEqual_Sign_Statement(TITLE, book_name));
            csv_writer.writeNext(new String[]{BOOK_TYPE, FIRST_PAGE, NO_OF_PAGES, DATE_CREATED, TITLE});
            while (csvCursor.moveToNext()) {
                String[] columns = new String[6];
                String book_type = csvCursor.getString(csvCursor.getColumnIndex(BOOK_TYPE));
                columns[0] = book_type;
                String first_page = csvCursor.getString(csvCursor.getColumnIndex(FIRST_PAGE));
                columns[1] = first_page;
                int no_of_pages = csvCursor.getInt(csvCursor.getColumnIndex(NO_OF_PAGES));
                columns[2] = Integer.toString(no_of_pages);
                long date = csvCursor.getLong(csvCursor.getColumnIndex(DATE_CREATED));
                columns[3] = Long.toString(date);
                columns[4] = csvCursor.getString(csvCursor.getColumnIndex(TITLE)) + "_share";
                columns[5] = csvCursor.getString(csvCursor.getColumnIndex(COURSE_CODE));
                csv_writer.writeNext(columns);
            }
            csv_writer.close();
            csvCursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ReadCsvToBookTable(File csv_file) {
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
//                String book_name =csv_file.getName().replace(".csv","");
//                contentValues.put(TITLE,book_name);
                ContentValues contentValues = null;
                while ((columns = csv_reader.readNext()) != null) {
//                    new String[]{BOOK_TYPE, FIRST_PAGE, NO_OF_PAGES, DATE_CREATED, TITLE}
                    if (i > 0) {
                        contentValues = new ContentValues();
                        String title = columns[4];
                        String course_code = columns[5];
                        String book_type = columns[0];
                        String first_page = columns[1];
                        int no_of_page = Integer.parseInt(columns[2]);
                        Long date = Long.parseLong(columns[3]);
                        contentValues.put(TITLE, title);
                        contentValues.put(COURSE_CODE, course_code);
                        contentValues.put(BOOK_TYPE, book_type);
                        contentValues.put(FIRST_PAGE, first_page);
                        contentValues.put(NO_OF_PAGES, no_of_page);
                        contentValues.put(DATE_CREATED, date);
                        insert_values_to_columns(contentValues);
                    }
                    i++;
                }
                Log.i(DEBUG_TABLES, "last entry " + getLastBookInfo().getTitle());
            } catch (IOException e) {
                Log.i(DEBUG_TABLES, "readnext is not working");
                e.printStackTrace();
            }
        } else {
            Log.i(DEBUG_TABLES, "csv_reader is null");
        }
    }

    private void deleteLastOperatorfromWhere_statement(StringBuilder where_statements, String last_operator) {
        int where_count = where_statements.length();
        int start = where_count - last_operator.length();
        int end = where_count - 1;
        Log.i(DEBUG_TABLES, "the delete where is from index " + start + " to " + end);
        Log.i(DEBUG_TABLES, "the delete where is from text " + where_statements.charAt(start + 1) + " to " + where_statements.charAt(end - 1));
        where_statements.delete(start, end);
    }


}
