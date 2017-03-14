package com.inextends.myratedlibrary.data;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

class BookSqlHelper {

    static Cursor getBooksFromAuthor(SQLiteDatabase database, Uri uri, String[] projection, String selection, String[] selectionArgs,
                                     String sortOrder) {
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        String sqlFromAuthor = "SELECT b." + BookContract.BookEntry._ID + " as _id, " + BookContract.BookEntry.COLUMN_TITLE + " " +
                "FROM " + BookContract.BookEntry.TABLE_NAME + " as b " +
                "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                "WHERE ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + "=?";
        return database.rawQuery(sqlFromAuthor, selectionArgs);
    }

}
