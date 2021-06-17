package com.example.abumuhsin.udusmini_library.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.mysqlitedbconnection.csv.CSV_reader;
import com.example.mysqlitedbconnection.csv.CSV_writer;
import com.example.mysqlitedbconnection.csv.Constants;
import com.example.mysqlitedbconnection.csv.sqlite.DatabaseCreator;
import com.example.mysqlitedbconnection.csv.sqlite.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import static com.example.mysqlitedbconnection.csv.Constants.BOOK_TITLE;
import static com.example.mysqlitedbconnection.csv.Constants.DEBUG_TABLES;
import static com.example.mysqlitedbconnection.csv.Constants.ID;
import static com.example.mysqlitedbconnection.csv.Constants.PATH;

public class Picture_BucketTable extends Table implements Serializable {

    public Picture_BucketTable(DatabaseCreator database) {
        super(database, Constants.PICTURE_BUCKET,
                getIntegerColumn_with_PrimaryKey_AutoIncrementStatement(ID),
                getString_ColumnStatement(BOOK_TITLE),
                getString_ColumnStatement(PATH)
        );
    }

    public void insert_picture(String book, String path) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_TITLE, book);
        contentValues.put(PATH, path);
        insert_values_to_columns(contentValues);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void SavePicturesTOCSV(String book_name, File exprt_dir) {
        CSV_writer csv_writer;
//        File exprt_dir = new File(FileUtils.getBookFilesPath(book_name));
        if (!exprt_dir.exists()) exprt_dir.mkdirs();
        File csvFile = new File(exprt_dir, "picturesBucket.csv");
        try {
            csvFile.createNewFile();
            csv_writer = new CSV_writer(new FileWriter(csvFile));
            Cursor csvCursor = fetch_All_Where(get_StringEqual_Sign_Statement(BOOK_TITLE, book_name));
            csv_writer.writeNext(new String[]{BOOK_TITLE, PATH});
            while (csvCursor.moveToNext()) {
                String[] columns = new String[2];
                columns[0] = csvCursor.getString(csvCursor.getColumnIndex(BOOK_TITLE)) /*+ "_share"*/;
                columns[1] = csvCursor.getString(csvCursor.getColumnIndex(PATH));
                csv_writer.writeNext(columns);
            }
            csv_writer.close();
            csvCursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadCsvToBucketTable(File csv_file) {
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
                while ((columns = csv_reader.readNext()) != null) {
                    if (i == 0) {
                        data_columns = columns;
                    } else {
                        for (int x = 0; x < columns.length; x++) {
                            String column = columns[x];
//                        Log.i(DEBUG_TABLES, "column is" + column);
                            if (i > 0) {
                                contentValues.put(data_columns[x], column);
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

}
