package com.example.mysqlitedbconnection.csv.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mysqlitedbconnection.csv.sqlite.Models.Column_Values;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Row;
import com.example.mysqlitedbconnection.csv.sqlite.Models.Where;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.mysqlitedbconnection.csv.Constants.AND;
import static com.example.mysqlitedbconnection.csv.Constants.ASC;
import static com.example.mysqlitedbconnection.csv.Constants.DEBUG_TABLES;
import static com.example.mysqlitedbconnection.csv.Constants.DESC;
import static com.example.mysqlitedbconnection.csv.Constants.GREATER_EQUAL;
import static com.example.mysqlitedbconnection.csv.Constants.INTEGER;
import static com.example.mysqlitedbconnection.csv.Constants.OR;
import static com.example.mysqlitedbconnection.csv.Constants.STRING;

/**
 * Created by Abu Muhsin on 25/08/2018.
 */

public class Table implements Serializable {
    private String table_name;
    private static final String TAG = "debug";
    //    protected OnTableCreatedListener onTableCreatedListener;
//    private SQLiteOpenHelper sqlite;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db;
    private String[] columns = new String[]{null};

    public Table() {

    }

    public Table(String table_name) {
        this.table_name = table_name;
    }

    public Table(DatabaseCreator db_Creator, String table_name, String... columns) {
//        CreateDatabase(context, "UdusHandout.Sqlite");
        db_write = db_Creator.getWritableDatabase();
        db = db_Creator.getReadableDatabase();
        this.table_name = table_name;
        create_with_ColumnList(columns);
        this.columns = columns;
    }

    public void CreateDatabase(Activity context, String Database_name) {
//        sqlite = new SQLiteOpenHelper(context, Database_name, null, 1) {
//            @Override
//            public void onCreate(SQLiteDatabase db) {
//
//            }
//
//            @Override
//            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//            }
//        };
    }

    public void Create(String... columns) {
        create_with_ColumnList(columns);
        this.columns = columns;
    }

    /**
     * @param table_Statement accept sql_table statement to create table
     */

    private void createTable(String table_Statement) {
        db_write.execSQL(table_Statement);
    }

//    public void setOnTableCreatedListener(OnTableCreatedListener onTableCreatedListener) {
//        this.onTableCreatedListener = onTableCreatedListener;
//    }

    /**
     * @param columns_Statements Accept column statement only to crreate the table
     */

    public void create_with_ColumnStatement(String columns_Statements) {
        createTable("CREATE TABLE IF NOT EXISTS " +
                table_name.toUpperCase() +
                "("
                + columns_Statements +
                ")");
//        onTableCreatedListener.onTableCreated();
    }
//
//    /**
//     * @param columns used an array to collect your column name
//     *                {@link #getInteger_ColumnStatement(String)#getIntegerColumn_with_Only_PrimaryKeyStatement(String)
//     *                #getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(String)}
//     *                and other method like that to insert your columns...
//     */

    public void create_with_ColumnList(String... columns) {
        StringBuilder columns_Statements = new StringBuilder();
        for (String column : columns) {
            if (column.equals(columns[columns.length - 1])) {
                columns_Statements.append(column);
            } else {
                columns_Statements.append(column).append(",");
            }
        }
        create_with_ColumnStatement(columns_Statements.toString());
//        String create_statement = "CREATE TABLE IF NOT EXISTS " +
//                table_name +
//                "(" +
//                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                columns_Statements +
//                ")";
//        createTable(create_statement);
    }


    public void insert_Data_to(String pic1, String pic2, String pic3, String pic4, String pic5) {
        String t_statement = "INSERT INTO " + table_name + "(NULL,?,?,?,?,?)";
        SQLiteStatement statement = db_write.compileStatement(t_statement);
        statement.clearBindings();
        statement.bindString(1, pic1);
        statement.bindString(2, pic2);
        statement.bindString(3, pic3);
        statement.bindString(4, pic4);
        statement.bindString(5, pic5);
        statement.executeInsert();
    }

    /**
     * @param values contain columns as a key and column values as value
     */
    protected void insert_values_to_columns(ContentValues values) {
        db_write.insert(table_name, null, values);
//        db_write.close();
    }

    /**
     * @param column_values contains arrays of column and values, each column can accept more than a value
     */
    public void insert_values_to_columns(Column_Values... column_values) {
//        ContentValues contentValues = new ContentValues();
        for (Column_Values column_value : column_values) {
            String column = column_value.getColumn();
            Object[] values = column_value.getValues();
            for (Object value : values) {
                if (getColumnTypeByName(column).equals(STRING)) {
                    single_Insert(column, (String) value);
//                    contentValues.put(column,(String) value);
                } else if (getColumnTypeByName(column).equals(INTEGER)) {
                    single_Insert(column, (Integer) value);
//                    contentValues.put(column,(Integer)value);
                }
            }
        }
    }

    /**
     * Convinient method to add a value to a single column at a time
     *
     * @param column column_name to insert the given the value.
     * @param value  the String value to be inserted
     *               <p>
     *               Note: dont try to use the combination of this method to add values to columns at a time,
     *               use it in a situation where you have to insert a value in a single column at a time.
     */
    public void single_Insert(String column, String value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        insert_values_to_columns(values);
    }


    /**
     * Convinient method to add a value to a single column at a time
     *
     * @param column column_name to insert the given the value.
     * @param value  the String value to be inserted
     *               <p>
     *               Note: dont try to use the combination of this method to add values to columns at a time,
     *               use it in a situation where you have to insert a value in a single column at a time.
     */
    public void single_Insert(String column, Integer value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        insert_values_to_columns(values);
    }

    /**
     * Convinient method to add a value to a single column at a time
     *
     * @param column_index column_name to insert the given the value.
     * @param value        the String value to be inserted
     *                     <p>
     *                     Note: dont try to use the combination of this method to add values to columns at a time,
     *                     use it in a situation where you have to insert a value in a single column at a time.
     */
    public void single_Insert(int column_index, String value) {
//        insert_values_to_a_column(column,content);
        ContentValues values = new ContentValues();
        values.put(getColumnNameByPosition(column_index), value);
        db_write.insert(table_name, null, values);

    }

    /**
     * Convinient method to add a value to a single column at a time
     *
     * @param column_index column_name to insert the given the value.
     * @param value        the String value to be inserted
     *                     <p>
     *                     Note: dont try to use the combination of this method to add values to columns at a time,
     *                     use it in a situation where you have to insert a value in a single column at a time.
     */
    public void single_Insert(int column_index, Integer value) {
//        insert_values_to_a_column(column,content);
        ContentValues values = new ContentValues();
        values.put(getColumnNameByPosition(column_index), value);
        db_write.insert(table_name, null, values);

    }

    /**
     * @param column column_name to insert the given the value.
     * @param values an array of String value to be inserted
     */
    public void insert_values_to_a_column(String column, String... values) {
        for (String content : values) {
            single_Insert(column, content);
        }
    }

    /**
     * @param column column_name to insert the given the value.
     * @param values an array of Integer value to be inserted
     */
    public void insert_values_to_a_column(String column, Integer... values) {
        for (Integer content : values) {
            single_Insert(column, content);
        }
    }

    /**
     * @param column_index column_index to insert given the value.
     * @param value        array of Integer value to be inserted
     */
    public void insert_values_to_a_column(int column_index, Integer... value) {
//        insert_values_to_a_column(column,content);
        for (Integer content : value) {
            single_Insert(column_index, content);
        }

    }

    /**
     * @param column_index column_index to insert given the value.
     * @param value        array of String value to be inserted
     */
    public void insert_values_to_a_column(int column_index, String... value) {
//        insert_values_to_a_column(column,content);
        for (String content : value) {
            single_Insert(column_index, content);
        }

    }


    private Cursor fetchData_With_Statment(String f_satatement) {
        return db.rawQuery(f_satatement, null);
//     db.query(false,Constants.BOOK,null,"id=?,col=?",new String[]{"1","3"},null,null,"col DESCR",null);
    }

    public Cursor fetchAll() {
        String statement = "SELECT * FROM " + table_name;
        return fetchData_With_Statment(statement);
    }


    public Cursor fetch_All_Where(String where) {
        return fetchData_With_Statment("SELECT * FROM " + table_name + " WHERE " + where);
    }

    public Cursor fetch_All_Where_OrderedBy(String where, String orderBy_column, String order_type) {
        return fetchData_With_Statment("SELECT * FROM " + table_name
                + " WHERE " + where + " "
                + (order_type.equals(ASC) ?
                get_ASCENDING_ORDER_BY_Statement(orderBy_column)
                : (order_type.equals(DESC) ?
                get_DECENDING_ORDER_BY_Statement(orderBy_column) : "")));
    }

    public ArrayList<String> loopTest(String column) {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = fetchAll();
        while (c.moveToNext()) {
            if (c.getString(c.getColumnIndex(column)) != null) {
                list.add(c.getString(c.getColumnIndex(column)));
            }
        }
        return list;
    }

    public Cursor fetch_Column(String column) {
        return fetchData_With_Statment("SELECT " + column + " From " + table_name);
    }

    public String fetch_Column_Statement(String column) {
        return "SELECT " + column + " From " + table_name;
    }

    public Cursor fetch_Column_Where(String column, String where_statement) {
        try {
            return fetchData_With_Statment("SELECT " + column + " From " + table_name + " WHERE " + where_statement);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor fetch_Columns(String... columns) {
        return db.query(false, table_name, columns, null, null, null, null, null, null);
    }

    public Cursor fetch_Columns_By_Row(String[] columns, String... where_Rows) {
        String where_statement = "";
        if (columns != null && where_Rows != null) {
            for (String columnRow : where_Rows) {
                if (columnRow.equals(where_Rows[where_Rows.length - 1])) {
                    where_statement = columnRow;
                } else {
                    where_statement = columnRow + " OR ";
                }
            }
            return db.query(false, table_name, columns, where_statement, null, null, null, null, null);
        } else {
            Log.i(TAG, "Columns and rows are empty");
            return null;
        }
    }

    /**
     * @param contentValues a map from column names to new column values. null is a
     *                      valid value that will be translated to NULL.
     * @param where         the optional WHERE clause to apply when updating.
     *                      Passing null will update all rows.
     * @param where_args    You may include ?s in the where clause, which
     *                      will be replaced by the values from whereArgs. The values
     *                      will be bound as Strings.
     */
    private void update(ContentValues contentValues, String where, String... where_args) {
        db.update(table_name, contentValues, where, where_args);
    }

    private Cursor getIfExistStringRecordInColumnCursor(String column, String search_item, String where) {
        return db.rawQuery("SELECT EXISTS (SELECT * FROM " + table_name + " WHERE " + where + " AND "  + column + " ='" + search_item + "' LIMIT 1)", null);
    }
    private Cursor getIfExistIntegerRecordInColumnCursor(String column, int search_item, String where) {
//        return db.rawQuery("SELECT * FROM " + table_name + " WHERE    yourKey=? AND yourKey1=?", new String[]{keyValue,keyvalue1});
        return db.rawQuery("SELECT EXISTS (SELECT * FROM " + table_name + " WHERE " + where + " AND "  + column + " =" + search_item + " LIMIT 1)", null);
    }

    private Cursor getIfExistStringRecordInColumnCursor(String column, String search_item) {
        return db.rawQuery("SELECT EXISTS (SELECT * FROM " + table_name + " WHERE " + column + " ='" + search_item + "' LIMIT 1)", null);
    }

    private Cursor getIfExistIntegerRecordInColumnCursor(String column, int search_item) {
//        return db.rawQuery("SELECT * FROM " + table_name + " WHERE    yourKey=? AND yourKey1=?", new String[]{keyValue,keyvalue1});
        return db.rawQuery("SELECT EXISTS (SELECT * FROM " + table_name + " WHERE " + column + "=" + search_item + " LIMIT 1)", null);
    }

    /** Covinient method to check if a column contain a value
     *
     * @param column The column to search through
     * @param value the search item either String or integer
     * @param other_where_statements other where statement to filter the result
     * @return return true if the item was found, false otherwise.
     */
    public boolean is_valueExistInColumn(String column, Object value, String other_where_statements) {
        if (value instanceof String) {
            Cursor cursor = getIfExistStringRecordInColumnCursor(column, (String) value, other_where_statements);
            cursor.moveToFirst();
            return cursor.getInt(0) == 1;
        }else if (value instanceof Integer){
            Cursor cursor = getIfExistIntegerRecordInColumnCursor(column, (Integer) value, other_where_statements);
            cursor.moveToFirst();
            return cursor.getInt(0) == 1;
        }
        return false;
    }

    /** Covinient method to check if a column contain a value
     *
     * @param column The column to search through
     * @param value the search item either String or integer
     * @return return true if the item was found, false otherwise.
     */
    public boolean is_valueExistInColumn(String column, Object value) {
        if (value instanceof String) {
            Cursor cursor = getIfExistStringRecordInColumnCursor(column, (String) value);
            cursor.moveToFirst();
            return cursor.getInt(0) == 1;
        }else if (value instanceof Integer){
            Cursor cursor = getIfExistIntegerRecordInColumnCursor(column, (Integer) value);
            cursor.moveToFirst();
            return cursor.getInt(0) == 1;
        }
        return false;
    }

    /**
     * A convinient method to update each row in a column by adding some value
     * @param Column column to update
     * @param incrementValue a number to add
     */
    public void IncreaseIntegerColumnValues(String Column, int incrementValue) {
        db.execSQL("UPDATE " + table_name + " SET " + Column + "=" + Column + " + " + incrementValue);
    }

    /**
     * A convinient method to update each row in a column by adding some value
     * with WHERE statement
     * @param Column column to update
     * @param incrementValue a number to add
     * @param where a where statement used as filter of records excluding WHERE keyword
     */
    public void IncreaseIntegerColumnValues(String Column, int incrementValue, String where) {
        db.execSQL("UPDATE " + table_name + " SET " + Column + "=" + Column + " + " + incrementValue + " WHERE " + where);
    }

    public void updateTest(String column, String value, String where) {
        db.execSQL("UPDATE " + table_name + "SET" + column + "=" + "'" + value + "'" + " WHERE " + where);
    }

    /**
     * @param Column   column to update
     * @param newValue the new value to update
     * @param where    statement to specify rows to update
     */
    public void update_Column_Where(String Column, Object newValue, String where) {
        ContentValues values = new ContentValues();
        if (newValue instanceof String) {
            values.put(Column, (String) newValue);
        } else if (newValue instanceof Integer) {
            values.put(Column, (Integer) newValue);
        }
        update(values, where);
    }

    /**
     * @param Column    column to update
     * @param new_value the new value to replace the old one
     * @param where     an objet of {@link Where class} instantiate it as <>code  new where("Id",value);
     *                  </>
     */
    public void update_Column_Where(String Column, Object new_value, Where where) {
        String whereStatement = get_Equal_Sign_Statement(where.getColumn(), where.getValue());
        update_Column_Where(Column, new_value, whereStatement);

    }

    /**
     * @param Column    column to update
     * @param new_value the new value to replace the old one
     * @param operator  This can be sql operator AND,OR...
     * @param wheres    an array objet of {@link Where class} instantiate it as <>code  new where("Id1",value1),new where("Id2",value2);
     *                  </> and ...
     */
    public void update_Column_many_Where(String Column, Object new_value, String operator, Where... wheres) {
        String whereStatement = getWhere_EqualStatement(operator, wheres);
        update_Column_Where(Column, new_value, whereStatement);

    }
    /**
     * @param Column    column to update
     * @param new_value the new value to replace the old one
     * @param wheres    an array objet of {@link Where class} instantiate it as <>code  new where("Id1",value1),new where("Id2",value2);
     *                  </> and ...
     */
    public void update_Column_many_Where(String Column, Object new_value, Where... wheres) {
        String whereStatement = get_AND_Where_Equalstatement(wheres);
        update_Column_Where(Column, new_value, whereStatement);

    }

    public void updateRow(String column_to_update, Object new_value, Object row_key_value) {
//        update_Column_Where(column_to_update, new_value, OR, new Where(column_for_key, row_key_value));
        update_Column_Where(column_to_update, new_value, get_Equal_Sign_Statement(getPrimaryColumn(), row_key_value));

    }

    /**
     * Easy method for updating rows in a column using a unique id
     *
     * @param column_to_update the column you want to update
     * @param where_row_values where_row_values is of type Row like new Row(id,"value"), it indicate the rowID which serve as the key
     *                         and the new value
     */
    public void update_Rows(String column_to_update, Row... where_row_values) {
        for (int i = 0; i < where_row_values.length; i++) {
            Object row_id = where_row_values[i].getUnique_row();
            Object new_value = where_row_values[i].getNew_value();
            updateRow(column_to_update, new_value, row_id);
        }
    }

    /**
     * @param column_to_update the column you want to update
     * @param row_and_values   type of ContentValue, row_and_values.put("key","value")it contain the rowID which serve as the key
     *                         and the new value
     */
    public void update_Rows(String column_to_update, ContentValues row_and_values) {

        for (Map.Entry<String, Object> entry : row_and_values.valueSet()) {
            Object row_id = entry.getKey();
            Object new_value = entry.getValue();
            updateRow(column_to_update, new_value, row_id);
        }
    }

    /**
     * @param column_to_update the column you want to update
     * @param row_and_values   type of hashMap, row_and_values.put("key","value")it contain the rowID which serve as the key
     *                         and the new value
     */
    public void update_Rows(String column_to_update, HashMap<Object, Object> row_and_values) {
        for (Map.Entry<Object, Object> entry : row_and_values.entrySet()) {
            Object row_id = entry.getKey();
            Object new_value = entry.getValue();
            updateRow(column_to_update, new_value, row_id);
        }
    }

    /**
     * convenient method to loop through all the columns in a table
     *
     * @return this method will return an #ArrayList containing n number of arraylist, and 'n'
     * is the number of columns you passed to the method
     * but mind you the compiler will issue some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
//     * @see #loop_column_of_any_type(String column) for more details...
     * You can easily retrieved your data using this method like below:
     * <>code
     * <p>
     * ArrayList<ArrayList> data_list = tableloop_All_Columns();
     * ArrayList<String> String_list = new ArrayList<>();
     * ArrayList<Integer> Integer_list = new ArrayList<>();
     * String_list.addAll(data_list.get(0));
     * String_list.addAll(data_list.get(1));
     * and....
     * </>
     * OR You should check sample code to see how you can get more control of your data with this method
     * <p>
     * Note: This method will be heavy depend on the number of data you have in your table,
     * if you are not going to use all the columns in the table, use other method like
     */
//     * {@link #loop_column_list(String...)} ),#loop_String_Column()})} and so on...
    public ArrayList<ArrayList> loop_All_Columns() {
        ArrayList list_of_columns = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            list_of_columns.add(loop_column_of_any_type(getColumnNameByPosition(i)));
        }
        return list_of_columns;
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @return return {@link ArrayList} that contains the list of values in a column, but mind you the compiler will issue
     * some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
     * @Example- If the column you pass in to the method to loop contain data of string type, when you want to
     * collect the data do something do something like the following:
     * <code><pre>
     *  ArrayList<String> title_list = table.loop_column_of_any_type(column_of_String_type);
     * </pre></code>
     * if you use something like ArrayList<Integer> for a column_of_String_type, an error mmay occur
     * Use preferred method like loop_String_Column(String column) to be more specific}
     */
    public ArrayList loop_column_of_any_type(String column) {
        Cursor cursor = fetch_Column(column);
        String type = getColumnTypeByName(column);
        ArrayList ar = new ArrayList();
        ArrayList<Integer> tmpListInteger = new ArrayList<>();
        ArrayList<String> tmpList_String = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (type.equals(STRING)) {
//                if (cursor.isNull(cursor.getColumnIndex(column))) {
//                    tmpList_String.add("NULL");
//                }
                tmpList_String.add(cursor.getString(cursor.getColumnIndex(column)));
            } else if (type.equals(INTEGER)) {
                tmpListInteger.add(cursor.getInt(cursor.getColumnIndex(column)));
            }

        }
        cursor.close();//smtn might go wrong
        if (!tmpListInteger.isEmpty()) {
            return tmpListInteger;
        } else if (!tmpList_String.isEmpty()) {
            return tmpList_String;
        } else {
            return ar;
        }

    }

    public void loop_column_WithListener(String column,Class class_type,OnColumnMoveToNext onColumnMoveToNext){
        Cursor cursor = fetch_Column(column);
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(column);
            if (class_type.equals(String.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getString(columnIndex),class_type);
            }else if (class_type.equals(Integer.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getInt(columnIndex),class_type);
            }else if (class_type.equals(Float.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getFloat(columnIndex),class_type);
            }else if (class_type.equals(Blob.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getBlob(columnIndex),class_type);
            }else if (class_type.equals(Long.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getLong(columnIndex),class_type);
            }else if (class_type.equals(Double.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getDouble(columnIndex),class_type);
            }

        }
        cursor.close();//smtn might go wrong
    }
    public void loop_column_WithListener(String column,Class class_type,String where_statement,OnColumnMoveToNext onColumnMoveToNext){
        Cursor cursor = fetch_Column_Where(column,where_statement);
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(column);
            if (class_type.equals(String.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getString(columnIndex),class_type);
            }else if (class_type.equals(Integer.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getInt(columnIndex),class_type);
            }else if (class_type.equals(Float.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getFloat(columnIndex),class_type);
            }else if (class_type.equals(Blob.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getBlob(columnIndex),class_type);
            }else if (class_type.equals(Long.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getLong(columnIndex),class_type);
            }else if (class_type.equals(Double.class)){
                onColumnMoveToNext.onMoveToNext(cursor.getDouble(columnIndex),class_type);
            }

        }
        cursor.close();//smtn might go wrong
    }

    public interface OnColumnMoveToNext{
        void onMoveToNext(Object row_data, Class class_type);
    }
    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @param where  An sqlite where statement (excluding WHERE) such as: "id=1"
     * @return return {@link ArrayList} that contains the list of values in a column where the given condition is true,
     * but mind you the compiler will issue some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
     * @Example- If the column you pass in to the method to loop contain data of string type, when you want to
     * collect the data do something do something like th  following:
     * <code><pre>
     *  ArrayList<String> title_list = table.loop_column_of_any_type(column_of_String_type);
     * </pre></code>
     * if you use something like ArrayList<Integer> for a column_of_String_type, an error mmay occur.
     * Use preferred method like {loop_String_Column(String column) to be more specific}
     */
    public ArrayList loop_column_of_any_type(String column, String where) {
        Cursor cursor = fetch_Column_Where(column, where);
        String type = getColumnTypeByName(column);
        ArrayList<Integer> tmpListInteger = new ArrayList<>();
        ArrayList<String> tmpList_String = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (type.equals(STRING)) {
                tmpList_String.add(cursor.getString(cursor.getColumnIndex(column)));
            } else if (type.equals(INTEGER)) {
                tmpListInteger.add(cursor.getInt(cursor.getColumnIndex(column)));
            }

        }
        cursor.close();
        if (!tmpListInteger.isEmpty()) {
            return tmpListInteger;
        } else if (!tmpList_String.isEmpty()) {
            return tmpList_String;
        } else {
            return null;
        }

    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column_index the column to loop through...
     * @param where        An sqlite where statement (excluding WHERE) such as: "id=1"
     * @return return {@link ArrayList} that contains the list of values in a column where the given condition is true,
     * but mind you the compiler will issue some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
     * @Example- If the column you pass in to the method to loop contain data of string type, when you want to
     * collect the data do something do something like th  following:
     * <code><pre>
     *  ArrayList<String> title_list = table.loop_column_of_any_type(column_of_String_type);
     * </pre></code>
     * if you use something like ArrayList<Integer> for a column_of_String_type, an error mmay occur.
     */
//     * Use preferred method like {@link #loop_String_Column(String column) to be more specific}
    public ArrayList loop_column_of_any_type(int column_index, String where) {
        return loop_column_of_any_type(getColumnNameByPosition(column_index), where);

    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column_position the position(0...n-1) of the column as you listed them while creating your table...
     * @return return {@link ArrayList} that contains the list of values of any_data type in a column
     * loop_column_of_any_type(String column)
     * Use preferred method like {@link #loop_String_Column(int column_index) to be more specific}
     */
    public ArrayList loop_column_of_any_type(int column_position) {
        String column_name = getColumnNameByPosition(column_position);
        return loop_column_of_any_type(column_name);

    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @return return an {@link ArrayList} containing Column String values
     */
    public ArrayList<String> loop_String_Column(String column) {
        Cursor cursor = fetch_Column(column);
        ArrayList<String> stringArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            stringArrayList.add(cursor.getString(cursor.getColumnIndex(column)));

        }
        cursor.close();
        return stringArrayList;
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column_position the position(0...n-1) of the column as you listed them while creating your table...
//     * @return return {@link ArrayList} that contains the list of values of String type in a column.
     * loop_String_Column(String column)
     */
    public ArrayList<String> loop_String_Column(int column_position) {
        String column_name = getColumnNameByPosition(column_position);
        return loop_String_Column(column_name);
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @return return an {@link ArrayList} containing Column Integer values
     */
    public ArrayList<Integer> loop_Integer_Column(String column) {
        Cursor cursor = fetch_Column(column);
        ArrayList<Integer> tmpListInteger = new ArrayList<>();
        while (cursor.moveToNext()) {
            tmpListInteger.add(cursor.getInt(cursor.getColumnIndex(column)));

        }
        cursor.close();
        return tmpListInteger;
    }


    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @param where  An sqlite where statement (excluding WHERE) such as: "id=1"
     * @return return an {@link ArrayList} containing Column Integer values
     */
    public ArrayList<Integer> loop_Integer_Column(String column, String where) {
        Cursor cursor = fetch_Column_Where(column, where);
        ArrayList<Integer> tmpListInteger = new ArrayList<>();
        while (cursor.moveToNext()) {
            tmpListInteger.add(cursor.getInt(cursor.getColumnIndex(column)));

        }
        cursor.close();
        return tmpListInteger;
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column the column to loop through...
     * @param where  An sqlite where statement (excluding WHERE) such as: "id=1"
     * @return return an {@link ArrayList} containing Column Integer values
     */
    public ArrayList<String> loop_String_Column(String column, String where) {
        ArrayList<String> tmpListString = new ArrayList<>();
        Cursor cursor = fetch_Column_Where(column, where);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.i(DEBUG_TABLES, "inside data loop");
                tmpListString.add(cursor.getString(cursor.getColumnIndex(column)));
                Log.i(DEBUG_TABLES, "path added");
            }
            cursor.close();
        }
        return tmpListString;
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column_index the column to loop through... using column index instead of name.
     * @param where        An sqlite where statement (excluding WHERE) such as: "id=1"
     * @return return an {@link ArrayList} containing Column Integer values
     */
    public ArrayList<Integer> loop_Integer_Column(int column_index, String where) {
        return loop_Integer_Column(getColumnNameByPosition(column_index), where);
    }

    /**
     * This is a convenient method for looping through a column
     *
     * @param column_position the position(0...n-1) of the column as you listed them while creating your table...
     * @return return {@link ArrayList} that contains the list of values of {Integer} type in a column.
     * loop_Integer_Column(String column)
     */
    public ArrayList<Integer> loop_Integer_Column(int column_position) {
        String column_name = getColumnNameByPosition(column_position);
        return loop_Integer_Column(column_name);
    }


    /**
     * convenient method to loop group of columns
     *
     * @param columns this will accept list of columns like {loop_column_list_where("id","names","ages")}
     * @return this method will return an #ArrayList containing n number of arraylist, and 'n'
     * is the number of columns you passed to the method
     * but mind you the compiler will issue some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
     * loop_column_of_any_type(String column) for more details...
     * You can easily retrieved your data using this method like below:
     * <>code
     * <p>
     * ArrayList<ArrayList> data_list = table.loop_column_list("Words", "Numbers");
     * ArrayList<String> String_list = new ArrayList<>();
     * ArrayList<Integer> Integer_list = new ArrayList<>();
     * String_list.addAll(data_list.get(0));
     * String_list.addAll(data_list.get(1));
     * </>
     * OR You should check sample code to see how you can get more control of your data with this method
     */
    public ArrayList<ArrayList> loop_column_list(String... columns) {
        ArrayList list_of_columns = new ArrayList<>();
        for (String column : columns) {
            list_of_columns.add(loop_column_of_any_type(column));
        }
//        }
        return list_of_columns;
    }


    /**
     * convenient method to loop group of columns
     *
     * @param where   An sqlite where statement (excluding WHERE) such as: "id=1"
     * @param columns this will accept list of columns like {loop_column_list_where("id","names","ages")}
     * @return this method will return an #ArrayList containing n number of arraylist, and 'n'
     * is the number of columns you passed to the method
     * but mind you the compiler will issue some Unchecked Error, but you will be okay if you use the appropriate List_type to store accept the values.
     * loop_column_of_any_type(String column) for more details...
     */
    public ArrayList<ArrayList> loop_column_list_where(String where, String... columns) {
        ArrayList list_of_columns = new ArrayList<>();
        for (String column : columns) {
            list_of_columns.add(loop_column_of_any_type(column, where));
        }
//        }
        return list_of_columns;
    }
//
//    public ArrayList<Integer> Loop_Integer_Column(String column) {
//        ArrayList<Integer> tmpList = new ArrayList<>();
//        tmpList.addAll(loop_column_of_any_type(column, INTEGER));
//        return tmpList;
//    }
//
//    public ArrayList<String> Loop_String_Column(String column) {
//        ArrayList<String> tmpList = new ArrayList<>();
//        tmpList.addAll(loop_column_of_any_type(column, STRING));
//        return tmpList;
//    }
//
//    public ArrayList<Integer> Loop_Integer_Column_where(String column, String where) {
//        ArrayList<Integer> tmpList = new ArrayList<>();
//        tmpList.addAll(loop_column_of_any_type(column, INTEGER, where));
//        return tmpList;
//    }
//
//    public ArrayList<String> Loop_String_Column_Where(String column, String where) {
//        ArrayList<String> tmpList = new ArrayList<>();
//        tmpList.addAll(loop_column_of_any_type(column, STRING, where));
//        return tmpList;
//    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_value      this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of object type, like INTEGER,String,BLOB and other types
     */
    public Object loop_row(String column_to_loop, Object row_value) {
        return loop_column_of_any_type(column_to_loop, get_Equal_Sign_Statement(getPrimaryColumn(), row_value)).get(0);
    }

    /**
     * @param column_index this represent column you want to loop by row using the column index
     * @param row_value    this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of object type, like INTEGER,String,BLOB and other types
     */
    public Object loop_row(int column_index, Object row_value) {
        return loop_row(getColumnNameByPosition(column_index), row_value);
    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_value      this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of String type, like INTEGER,String,BLOB and other types
     */
    public String loop_String_row(String column_to_loop, Object row_value) {
        return (String) loop_column_of_any_type(column_to_loop, get_Equal_Sign_Statement(getPrimaryColumn(), row_value)).get(0);
    }

    /**
     * @param column_index this represent column you want to loop by row using the column index instead of its name
     * @param row_value    this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of String type, like INTEGER,String,BLOB and other types
     */
    public String loop_String_row(int column_index, Object row_value) {
        return loop_String_row(getColumnNameByPosition(column_index), row_value);
    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_value      this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of Integer type, like INTEGER,String,BLOB and other types
     */
    public Integer loop_Integer_row(String column_to_loop, Object row_value) {
        return (Integer) loop_column_of_any_type(column_to_loop, get_Equal_Sign_Statement(getPrimaryColumn(), row_value)).get(0);
    }

    /**
     * @param column_index this represent column you want to loop by row using the index of the column instead of its name.
     * @param row_value    this represent the unique value to identify a specific row.
     * @return this method return a single value of a row that is of Integer type, like INTEGER,String,BLOB and other types
     */
    public Integer loop_Integer_row(int column_index, Object row_value) {
        return loop_Integer_row(getColumnNameByPosition(column_index), row_value);
    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_values     this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of object type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<Object> loop_rows(String column_to_loop, Object... row_values) {
        ArrayList<Object> list = new ArrayList<>();
        for (Object row_id : row_values) {
            list.add(loop_row(column_to_loop, row_id));
        }
        return list;
    }

    /**
     * @param column_index this represent column you want to loop by row using column index insted of column name
     * @param row_values   this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of object type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<Object> loop_rows(int column_index, Object... row_values) {
        return loop_rows(getColumnNameByPosition(column_index), row_values);
    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_values     this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of Integer type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<Integer> loop_Integer_rows(String column_to_loop, Object... row_values) {
        ArrayList<Integer> list = new ArrayList<>();
        for (Object row_id : row_values) {
            list.add((Integer) loop_row(column_to_loop, row_id));
        }
        return list;
    }

    /**
     * @param column_index this represent column you want to loop by row using column index insted of column name
     * @param row_values   this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of Integer type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<Integer> loop_Integer_rows(int column_index, Object... row_values) {
        return loop_Integer_rows(getColumnNameByPosition(column_index), row_values);
    }

    /**
     * @param column_to_loop this represent column you want to loop by row
     * @param row_values     this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of String type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<String> loop_String_rows(String column_to_loop, Object... row_values) {
        ArrayList<String> list = new ArrayList<>();
        for (Object row_id : row_values) {
            list.add((String) loop_row(column_to_loop, row_id));
        }
        return list;
    }

    /**
     * @param column_index this represent column you want to loop by row using column index insted of column name
     * @param row_values   this represent the unique values that identify a specific row in the column.
     * @return this method return an ArrayList of values of a row that is of String type, like INTEGER,String,BLOB and other types
     */
    public ArrayList<String> loop_String_rows(int column_index, Object... row_values) {
        return loop_String_rows(getColumnNameByPosition(column_index), row_values);
    }

    public void del(String where) {
        String sql = "DELETE FROM " + table_name + " WHERE " + where;
        db.execSQL(sql);
    }

    public int Delete(String where, @Nullable String... whereArgs) {
        return db_write.delete(table_name, where, whereArgs);
    }

    /**
     * Convenient method to Delete a row.
     *
     * @param row the unique value(Object) that can identify each row, usually a primary key.
     * @return will return the 1 row affected.
     */
    public int delete(Object row) {
        return Delete(get_Equal_Sign_Statement(getPrimaryColumn(), row), (String[]) null);
    }

    /**
     * Convenient method to Delete a row.
     *
     * @param row the unique value(String) that can identify each row, usually a primary key.
     * @return will return the 1 row affected.
     */
    public int delete(String row) {
        return Delete(this.get_StringEqual_Sign_Statement(getPrimaryColumn(), row), (String[]) null);


    }

    /**
     * Convenient method to Delete a row.
     *
     * @param row the unique value(int) that can identify each row, usually a primary key.
     * @return will return the 1 row affected.
     */
    public int delete(int row) {
        return Delete(get_Equal_Sign_Statement(getPrimaryColumn(), row), (String[]) null);
    }

    /**
     * Convenient method to Delete many rows.
     *
     * @param rows the unique value(object...) that can identify each row, usually a primary key.
     *             this method accept list of values like <code>
     *             delete_many(row1,row2,row3...);
     *             </code>
     * @return will return the n number of  affected rows
     */
    public int delete_many(Object... rows) {
        int count = 0;
        for (Object row : rows) {
            count += delete(row);

        }
        return count;
    }

    /**
     * Convenient method to Delete many rows.
     *
     * @param rows the unique value(String...) that can identify each row, usually a primary key.
     *             this method accept list of values like <code>
     *             delete_many(row1,row2,row3...);
     *             </code>
     * @return will return the n number of  affected rows
     */
    public int delete_many(String... rows) {
        int count = 0;
        for (String row : rows) {
            count += delete(row);
        }
        return count;
    }

//    /**
//     * Convenient method to Delete many rows.
//     *
//     * @param rows the unique value(int...) that can identify each row, usually a primary key.
//     *             this method accept list of values like <code>
//     *             delete_many(row1,row2,row3...);
//     *             </code>
//     * @return will return the n number of  affected rows
//     */
//    public int delete_many(Integer... rows) {
//        String[] rows_in_string = new String[rows.length];
//        for (int i = 0; i < rows.length; i++) {
//            Integer row = rows[i];
//            rows_in_string[i]=String.valueOf(row);
//        }
//        Log.i(DEBUG_TABLES,getPrimaryColumn() +"=?");
//        return Delete(getPrimaryColumn() +"=?", rows_in_string);
//    }

    /**
     * Convenient method to Delete many rows.
     *
     * @param rows the unique value(int...) that can identify each row, usually a primary key.
     *             this method accept list of values like <code>
     *             delete_many(row1,row2,row3...);
     *             </code>
     * @return will return the n number of  affected rows
     */
    public int delete_many(Integer... rows) {
        int count = 0;
        for (Integer row : rows) {
            count += delete(row);
        }
        return count;
    }

    /**
     * This is a convenient method to Delete rows by your specified column other than primary key like (WHERE non_primary_column=deleteItem)
     *
     * @param column the column you want to use to Delete
     * @param value  the value of the column that will identify each item you intend to Delete
     * @return it returns the number of affected rows
     */
    public int delete_where(String column, Object value) {
        return Delete(get_Equal_Sign_Statement(column, value), (String[]) null);
    }

    /**
     * This is a convenient method to Delete rows by your specify column other than primary key like (WHERE non_primary_column=deleteItem)
     *
     * @param column the column you want to use to Delete
     * @param value  the value of the column that will identify each item you intend to Delete
     * @return it returns the number of affected rows
     */
    public int delete_where(String column, String value) {
        return Delete(this.get_Like_Statement(column, value), (String[]) null);
    }

    /**
     * This is a convenient method to Delete rows by your specify column other than primary key like (WHERE non_primary_column=deleteItem)
     *
     * @param wheres this will  accept an array of (columns and values) and join them as an Sqlite (OR statement),
     *               which means it is going to Delete all rows that certifies  the given conditions with OR operatoe
     * @return it returns the number of affected rows
     */
    public int delete_many_where_using_OR(Where... wheres) {
        return Delete(get_OR_Where_Equalstatement(wheres));
    }

    /**
     * This is a convenient method to Delete rows by your specify column other than primary key like (WHERE non_primary_column=deleteItem)
     *
     * @param wheres this will  accept an array of (columns and values) and join them as an Sqlite (AND statement),
     *               which means it is going to Delete all rows that certifies  the given conditions with AND operator
     * @return it returns the number of affected rows
     */
    public int delete_where_using_AND(Where... wheres) {
        return Delete(get_AND_Where_Equalstatement(wheres), (String[]) null);
    }

    /**
     * This is a convenient method to Delete rows by your specify column other than primary key like (WHERE non_primary_column=deleteItem)
     *
     * @param values this will  accept an contentValues of (columns and values) and join them as an Sqlite (AND statement),
     *               which means it is going to Delete all rows that certifies  the given conditions with AND operator
     * @return it returns the number of affected rows
     */
    public int delete_where_using_AND(ContentValues values) {
        return Delete(get_AND_Where_Equalstatement(values), (String[]) null);
    }

    public boolean is_In_Column(String column, Object value) {
        ArrayList page_numbers = loop_column_of_any_type(column);
        return page_numbers.contains(value);
    }


    public boolean is_In_Column(String column, int value) {
        ArrayList<Integer> page_numbers = loop_Integer_Column(column);
        return page_numbers.contains(value);
    }

    public boolean is_In_Column(String column, String value) {
        ArrayList<String> page_numbers = loop_String_Column(column);
        return page_numbers.contains(value);
    }

    public boolean is_In_Column_Where(String column, Object value, String where) {
        ArrayList page_numbers = loop_column_of_any_type(column, where);
        return page_numbers.contains(value);
    }

    public boolean is_In_Column_Where(String column, String value, String where) {
        ArrayList<String> page_numbers = (ArrayList<String>) loop_column_of_any_type(column, where);
        return page_numbers.contains(value);
    }

    public boolean is_In_Column_Where(String column, int value, String where) {
        ArrayList<Integer> page_numbers = (ArrayList<Integer>) loop_column_of_any_type(column, where);
        return page_numbers.contains(value);
    }


    //.....................Like Statements..................................

    public String get_Like_Statement(String Column, Object like_value) {
        return Column + " LIKE '%" + like_value + "%'";

    }

    public String get_Like_Where_statement_with_Operator(String operator, Where... where_array) {
        StringBuilder where = new StringBuilder();

        for (Where whereColumnValue : where_array) {
            String Where_column = whereColumnValue.getColumn();
            Object where_value = whereColumnValue.getValue();
            int pos = where_array.length - 1;
//            switch (operator) {
//                case AND:
            if (whereColumnValue.equals(where_array[pos])) {
                where.append(get_Like_Statement(Where_column, where_value));
            } else {
                where.append(get_Like_Statement(Where_column, where_value));
                where.append(operator);
            }
        }
        return where.toString();

    }

    public String get_Like_Where_statement_with_And(Where... where_values) {
        return get_Like_Where_statement_with_Operator(AND, where_values);
    }

    public String get_Like_Where_statement_with_OR(Where... where_values) {
        return get_Like_Where_statement_with_Operator(OR, where_values);
    }

    //............................OrderBy Statements................................
    private String get_ORDER_BY_Statement(String Column) {
        return " ORDER BY " + Column;

    }

    private String get_ORDER_BY_Statement(String Column, String Order_type) {
        return get_ORDER_BY_Statement(Column) + " " + Order_type;

    }

    public String get_ASCENDING_ORDER_BY_Statement(String Column) {
        return get_ORDER_BY_Statement(Column) + " " + ASC;

    }

    public String get_DECENDING_ORDER_BY_Statement(String Column) {
        return get_ORDER_BY_Statement(Column) + " " + DESC;

    }

    public String get_ASCENDING_ORDER_BY_StatementForColumns(String... Columns) {
        return get_ORDER_BY_StatementForColumns(ASC, Columns);
    }

    public String get_DECENDING_ORDER_BY_StatementForColumns(String... Columns) {
        return get_ORDER_BY_StatementForColumns(DESC, Columns);
    }

    private String get_ORDER_BY_StatementForColumns(String Order_type, String... Columns) {
        StringBuilder order_builder = new StringBuilder();
        order_builder.append(" ORDER BY ");
        for (int i = 0; i < Columns.length; i++) {
            String column = Columns[i];
            order_builder.append(column);
            if (!column.equals(Columns[Columns.length - 1])) {
                order_builder.append(", ");
            }
        }
        order_builder.append(" ");
        order_builder.append(Order_type);
        return order_builder.toString();
    }

    //...............................Equal Sign Statemennts........................

    public static String get_Equal_Sign_Statement(String Column, Object value) {
        return value instanceof String
                ?
                Column + " IS " + "'" + value + "'"
                :
                Column + "=" + value;
    }

    public String get_StringEqual_Sign_Statement(String Column, String value) {
        return get_Equal_Sign_Statement(Column, value);
    }

    public String get_a_Statement_with_Operator(String column, String operator, Object value) {
        return value instanceof String ?
                column + " " + operator + " " + "'" + value + "'"
                :
                column + " " + operator + " " + value;
    }

    public String get_Greater_Equal_Statement(String column, Object value) {
        return get_a_Statement_with_Operator(column, GREATER_EQUAL, value);
    }

    public String get_Greater_Equal_Statement(String column, int value) {
        return get_a_Statement_with_Operator(column, GREATER_EQUAL, value);
    }
    public String get_Greater_Equal_Statement(Where where) {
        return get_a_Statement_with_Operator(where.getColumn(), GREATER_EQUAL, where.getValue());
    }

    public String get_Greater_Equal_Statement(String column, String value) {
        return get_a_Statement_with_Operator(column, GREATER_EQUAL, value);
    }

    private String getWhere_EqualStatement(String operator, Where... where_array) {
        StringBuilder where = new StringBuilder();

        for (Where whereColumnValue : where_array) {
            String Where_column = whereColumnValue.getColumn();
            Object where_value = whereColumnValue.getValue();
            int pos = where_array.length - 1;
            if (where_value instanceof String) {
                where.append(get_StringEqual_Sign_Statement(Where_column, (String) where_value));
            } else {
                where.append(get_Equal_Sign_Statement(Where_column, where_value));

            }
            if (!whereColumnValue.equals(where_array[pos])) {
                where.append(operator);
            }
        }
        return where.toString();

    }

    public String get_AND_Where_Equalstatement(ContentValues where_values) {
        StringBuilder where = new StringBuilder();
        ArrayList<Map.Entry<String, Object>> list = new ArrayList<>(where_values.valueSet());
        for (Map.Entry<String, Object> entry : list) {
            String Where_column = entry.getKey();
            Object where_value = entry.getValue();
            int last_position = list.size() - 1;
            if (where_value instanceof String) {
                where.append(get_StringEqual_Sign_Statement(Where_column, (String) where_value));
            } else {
                where.append(get_Equal_Sign_Statement(Where_column, where_value));
            }
            if (!entry.equals(list.get(last_position))) {
                where.append(AND);
            }

        }

        return where.toString();
    }

    public String get_AND_Where_Equalstatement(Where... column_values) {
        return getWhere_EqualStatement(AND, column_values);
    }

    private String get_OR_Where_Equalstatement(Where... column_values) {
        return getWhere_EqualStatement(OR, column_values);
    }


    public void add_number_to_column(String column, int num) {
        ArrayList<Integer> page_numbers = loop_column_of_any_type(column);
        for (int i : page_numbers) {
            //num+i in the column Update
        }

    }

    private String getColumnNameByPosition(int column_position) {
        String column_name = "";
        if (columns != null) {
            String content = columns[column_position];
            column_name = get_a_word(content, " ", 0);
        }
        return column_name;
    }

    private String getColumnTypeByPosition(int column_position) {
        String column_Type = "";
        if (columns != null) {
            String content = columns[column_position];
            String sql_type = get_a_word(content, " ", 1);
            switch (sql_type) {
                case "INTEGER":
                    column_Type = INTEGER;
                    break;
                case "VARCHAR":
                    column_Type = STRING;
                    break;
            }
        }
        return column_Type;
    }

    private String getPrimaryColumn() {
        String primary_column = "";
        for (String column_clause : columns) {
            if (get_a_word(column_clause, " ", 2).equals("PRIMARY") && get_a_word(column_clause, " ", 3).equals("KEY")) {
                primary_column = get_a_word(column_clause, " ", 0);
                break;
            }
        }
        return primary_column;
    }

    private String getColumnTypeByName(String ColumnName) {
        String column_Type = "";
        for (int i = 0; i < columns.length; i++) {
            if (ColumnName.equals(getColumnNameByPosition(i))) {
                column_Type = getColumnTypeByPosition(i);
                break;
            }
        }

        return column_Type;
    }

    private String get_a_word(String text, String reg_exp, int pos) {
        String[] array_of_text = text.split(reg_exp);
        return array_of_text[pos];
    }


    public static String getInteger_ColumnStatement(String column) {
        return column + " INTEGER";
    }

    public static String getString_ColumnStatement(String column) {
        return column + " VARCHAR";
    }

    public static String getString_UNIQUE_Column(String column) {
        return column + " TEXT NOT NULL UNIQUE";
    }

    public static String getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(String column) {
        return column + " INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    public static String getIntegerColumn_with_AutoIncrementStatement(String column) {
        return column + " INTEGER AUTOINCREMENT";
    }

    public static String getDateColumnWithDefaultDate(String column) {
        return column + " DATETIME DEFAULT CURRENT_TIMESTAMP ";
    }


    public static String getIntegerColumn_with_Only_PrimaryKeyStatement(String column) {
        return column + " INTEGER PRIMARY KEY";
    }

    public static String getStringColumn_with_Only_PrimaryKeyStatement(String column) {
        return column + " TEXT PRIMARY KEY";
    }

    public static String getForeignKeyStatement(String Foreign_column, String Reference_table, String Reference_column) {
        return " FOREIGN KEY (" + Foreign_column + ") REFERENCES " + Reference_table + "(" + Reference_column + "));";
    }
}
