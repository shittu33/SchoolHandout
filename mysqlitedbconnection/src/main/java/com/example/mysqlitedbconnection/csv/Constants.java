package com.example.mysqlitedbconnection.csv;

/**
 * Created by Abu Muhsin on 21/08/2018.
 */

public class Constants  {
    public static final String DEBUG_TABLES = "debug_tables";

    static final int next = 0;
    static final int end = 1;
    static final int delete = 2;
    static final int no_text = 0;
    static final int no_image = 0;
    /**Layout Constants to identify selected layouts...*/
    public static final int COVER_TYPE = 0;
    public static final int SINGLE_PAGE_TYPE = 1;
    public static final int LAST_PAGE_TYPE = 2;
    static final int grid_new = 1;
    static final int green = 2;
    /**Database Table names*/
    public static final String BOOK = "BOOK";
    public static final String PAGES = "PAGES";
    public static final String PICTURES = "PICTURES";
    public static final String PICTURE_BUCKET = "Bucket_Table";
    public static final String TEXT = "TEXT";
    /**Database Column names...*/
    public static final String TITLE = "title";
    public static final String BOOK_TYPE = "Book_type";
    public static final String COURSE_CODE= "course_code";
    public static final String DATE_CREATED = "Date_Created";
    public static final String TYPE_HANDOUT = "Handout";
    public static final String NO_OF_PAGES = "Number_of_Pages";
    public static final String BOOK_TITLE = "book_title";
    public static final String PAGE_NUMBER= "pageNumber";
    public static final String PAGE_INDEX= "page_index";
    public static final String PAGE_PICTURE_COUNT= "page_picture_count";
    public static final String PATH = "path";
    public static final String LAYOUT = "layout";
    public static final String POSITION = "image_position";
    public static final String FIRST_PAGE = "First_page";
    public static final int EMPTY_PAGE = 0;
    public static final int SINGLE_PAGE = 1;
    public static final String CONTENT = "content";
    public static final String BACKGROUND= "background";
    public static final String BOOK_NUMBER= "bookNumber";
    public static final String ID= "Id";
    /**Database Operators*/
    public  static final String AND = " AND ";
    public  static final String OR = " OR ";
    public  static final String GREATER_EQUAL = " >= ";
    /**Order type*/
    public  static final String ASC = "ASC";
    public  static final String DESC = "DESC";
    /** Data type Identifiers*/
    public  static final String STRING = "String";
    public  static final String INTEGER = "int";



}
