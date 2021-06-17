package com.example.abumuhsin.udusmini_library.Tables;

import android.content.ContentValues;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.HandoutFilter;
import com.example.abumuhsin.udusmini_library.utils.Constants;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Where;
import com.example.mysqlitedbconnection.csv.sqlite.Table;

import java.io.Serializable;

import static com.example.abumuhsin.udusmini_library.utils.Constants.COURSE_CODE;
import static com.example.abumuhsin.udusmini_library.utils.Constants.DEPARTMENT;
import static com.example.abumuhsin.udusmini_library.utils.Constants.Handout_ID;
import static com.example.abumuhsin.udusmini_library.utils.Constants.LEVEL;
import static com.example.mysqlitedbconnection.csv.Constants.DATE_CREATED;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class HandoutFilterTable extends Table implements Serializable {
    public HandoutFilterTable(DatabaseCreator database) {
        super(database, Constants.TABLE_HANDOUT_Filter
                , getStringColumn_with_Only_PrimaryKeyStatement(Handout_ID)
                , getString_ColumnStatement(DEPARTMENT)
                , getString_ColumnStatement(LEVEL)
                , getString_ColumnStatement(COURSE_CODE)
                , getDateColumnWithDefaultDate(DATE_CREATED));
    }

    public void savefilter(HandoutFilter handoutFilter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Handout_ID, handoutFilter.getHandout_id());
        contentValues.put(DEPARTMENT, handoutFilter.getDepartment());
        contentValues.put(LEVEL, handoutFilter.getLevel());
        contentValues.put(COURSE_CODE, handoutFilter.getCourse_code());
        insert_values_to_columns(contentValues);
    }

    public void FetchFilter(String column,final OnFilterFetch onFilterFetch) {
        loop_column_WithListener(column, String.class, new OnColumnMoveToNext() {
            @Override
            public void onMoveToNext(Object row_data, Class class_type) {
                onFilterFetch.onEachFilterRead(((String) row_data));
            }
        });
    }
    public void FetchFilterWhere(String column,String where_statemnt,final OnFilterFetch onFilterFetch) {
        loop_column_WithListener(column, String.class,where_statemnt, new OnColumnMoveToNext() {
            @Override
            public void onMoveToNext(Object row_data, Class class_type) {
                if (class_type.equals(String.class)) {
                    onFilterFetch.onEachFilterRead(((String) row_data));
                }
            }
        });
    }

    public void deleteAllBookFilters(String book_name) {
        final Where where_book = new Where(Handout_ID, book_name);
        delete_where_using_AND(where_book);
    }

    public interface OnFilterFetch {
        void onEachFilterRead(String filter);
    }
}
