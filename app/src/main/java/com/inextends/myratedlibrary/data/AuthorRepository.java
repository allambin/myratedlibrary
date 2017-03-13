package com.inextends.myratedlibrary.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
}
