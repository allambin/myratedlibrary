package com.inextends.myratedlibrary.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "ratedlibrary.db";
    private static final String TAG = "BookDbHelper";

    private static final String SQL_CREATE_BOOKS_TABLE =
            "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                    BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookContract.BookEntry.COLUMN_TITLE + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME;

    private static final String SQL_ADD_COMMENT_COLUMN =
            "ALTER TABLE " + BookContract.BookEntry.TABLE_NAME + " ADD COLUMN " +
                    BookContract.BookEntry.COLUMN_COMMENT + " TEXT";

    private static final String SQL_CREATE_AUTHORS_TABLE =
            "CREATE TABLE " + AuthorContract.AuthorEntry.TABLE_NAME + " (" +
                    AuthorContract.AuthorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AuthorContract.AuthorEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    "CONSTRAINT unique_name UNIQUE(" + AuthorContract.AuthorEntry.COLUMN_NAME + "))";

    private static final String SQL_CREATE_BOOKS_AUTHORS_TABLE =
            "CREATE TABLE " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " (" +
                    BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " INTEGER NOT NULL, " +
                    BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + ") " +
                    "REFERENCES " + BookContract.BookEntry.TABLE_NAME + "(" + BookContract.BookEntry._ID + "), " +
                    "FOREIGN KEY(" + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + ") " +
                    "REFERENCES " + AuthorContract.AuthorEntry.TABLE_NAME + "(" + AuthorContract.AuthorEntry._ID + ")" +
                    "CONSTRAINT unique_fks UNIQUE(" + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + ", " + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + "))";

    private static final String SQL_ADD_RATING_COLUMN =
            "ALTER TABLE " + BookContract.BookEntry.TABLE_NAME + " ADD COLUMN " +
                    BookContract.BookEntry.COLUMN_RATING + " INTEGER NOT NULL DEFAULT 0";

    private static final String SQL_ADD_AUTHORS_COLUMN =
            "ALTER TABLE " + BookContract.BookEntry.TABLE_NAME + " ADD COLUMN " +
                    BookContract.BookEntry.COLUMN_AUTHORS + " TEXT";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
        try {
            sqLiteDatabase.execSQL(SQL_ADD_COMMENT_COLUMN);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: could not add comment column, is probably already there");
        }
        sqLiteDatabase.execSQL(SQL_CREATE_AUTHORS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_AUTHORS_TABLE);
        try {
            sqLiteDatabase.execSQL(SQL_ADD_RATING_COLUMN);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: could not add rating column, is probably already there");
        }
        try {
            sqLiteDatabase.execSQL(SQL_ADD_AUTHORS_COLUMN);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: could not add authors column, is probably already there");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
