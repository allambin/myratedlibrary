package com.inextends.myratedlibrary.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.inextends.myratedlibrary.data.BookContract;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "ratedlibrary.db";
    private static final String TAG = "BookDbHelper";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                    BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookContract.BookEntry.COLUMN_TITLE + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME;

    private static final String SQL_ADD_COMMENT_COLUMN =
            "ALTER TABLE " + BookContract.BookEntry.TABLE_NAME + " ADD COLUMN " +
                    BookContract.BookEntry.COLUMN_COMMENT + " TEXT";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        try {
            sqLiteDatabase.execSQL(SQL_ADD_COMMENT_COLUMN);
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: could not add comment column, is probably already there");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
