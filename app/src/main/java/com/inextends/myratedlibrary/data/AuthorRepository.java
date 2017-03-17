package com.inextends.myratedlibrary.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

class AuthorRepository {

    static Uri insert(Context context, BookDbHelper dbHelper, Uri uri, ContentValues values) {
        String name = values.getAsString(AuthorContract.AuthorEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Author requires a name");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(AuthorContract.AuthorEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        context.getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    static int update(Context context, BookDbHelper dbHelper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        if (values.containsKey(AuthorContract.AuthorEntry.COLUMN_NAME)) {
            String name = values.getAsString(AuthorContract.AuthorEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Author requires a name");
            }
        }

        int numRowsAffected = database.update(AuthorContract.AuthorEntry.TABLE_NAME, values, selection, selectionArgs);
        if (numRowsAffected != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return numRowsAffected;
    }

    static Cursor fetchAllWithRatings(SQLiteDatabase database) {
        String sql = "SELECT a." + AuthorContract.AuthorEntry._ID + ", " +
                AuthorContract.AuthorEntry.COLUMN_NAME + ", " +
                "avg(b." + BookContract.BookEntry.COLUMN_RATING + ") as " + AuthorContract.AuthorEntry.COLUMN_RATING + ", " +
                "count(b." + BookContract.BookEntry._ID + ") as " + AuthorContract.AuthorEntry.COLUMN_BOOKS_COUNT + " " +
                "FROM " + AuthorContract.AuthorEntry.TABLE_NAME + " as a " +
                "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " = a." + AuthorContract.AuthorEntry._ID + " " +
                "INNER JOIN " + BookContract.BookEntry.TABLE_NAME + " as b " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                "GROUP BY a." + AuthorContract.AuthorEntry._ID;
        return database.rawQuery(sql, new String[]{});
    }
}
