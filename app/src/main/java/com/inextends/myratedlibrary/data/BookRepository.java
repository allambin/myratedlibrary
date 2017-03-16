package com.inextends.myratedlibrary.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.inextends.myratedlibrary.ArrayUtils;

class BookRepository {
    private static final String TAG = "BookRepository";

    static Cursor fetchAllWithAuthors(SQLiteDatabase database, String[] selectionArgs) {
        String sql = "SELECT b." + BookContract.BookEntry._ID + " as _id, " + BookContract.BookEntry.COLUMN_TITLE + ", " +
                BookContract.BookEntry.COLUMN_RATING + ", " + AuthorContract.AuthorEntry.COLUMN_NAME + " " +
                "FROM " + BookContract.BookEntry.TABLE_NAME + " as b " +
                "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                "INNER JOIN " + AuthorContract.AuthorEntry.TABLE_NAME + " as a " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " = a." + AuthorContract.AuthorEntry._ID + " ";
        return database.rawQuery(sql, selectionArgs);
    }

    static Cursor fetchWithAuthors(SQLiteDatabase database, Uri uri, String[] selectionArgs) {
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        String sql = "SELECT b." + BookContract.BookEntry._ID + " as _id, " + BookContract.BookEntry.COLUMN_TITLE + ", " +
                BookContract.BookEntry.COLUMN_RATING + ", " + AuthorContract.AuthorEntry.COLUMN_NAME + ", " + BookContract.BookEntry.COLUMN_COMMENT + " " +
                "FROM " + BookContract.BookEntry.TABLE_NAME + " as b " +
                "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                "INNER JOIN " + AuthorContract.AuthorEntry.TABLE_NAME + " as a " +
                "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " = a." + AuthorContract.AuthorEntry._ID + " " +
                "WHERE b." + BookContract.BookEntry._ID + "=?";
        return database.rawQuery(sql, selectionArgs);
    }

    static Cursor fetchAll(SQLiteDatabase database, String[] projection, String selection, String[] selectionArgs,
                           String sortOrder) {
        return database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    static Cursor fetchOne(SQLiteDatabase database, String[] projection, String selection, String[] selectionArgs,
                           String sortOrder) {
        return database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Nullable
    static Uri insertWithAuthors(Context context, BookDbHelper dbHelper, Uri uri, ContentValues values) {
        String title = values.getAsString(BookContract.BookEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a title");
        }
        String authorsNames = values.getAsString(BookContract.BookEntry.COLUMN_AUTHORS);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.d(TAG, "insertBook: insert has failed");
            return null;
        }

        String[] authorsNamesArray = ArrayUtils.explode(authorsNames);
        if (deleteBookAuthors(database, id)) {
            for (int i = 0; i < authorsNamesArray.length; i++) {
                long authorId = saveAuthor(context, database, authorsNamesArray[i]);
                if (authorId > 0) {
                    saveBookAuthor(database, id, authorId);
                }
            }
        }

        context.getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    static int updateWithAuthors(Context context, BookDbHelper dbHelper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        if (values.containsKey(BookContract.BookEntry.COLUMN_TITLE)) {
            String title = values.getAsString(BookContract.BookEntry.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }
        String authorsNames = values.getAsString(BookContract.BookEntry.COLUMN_AUTHORS);

        int numRowsAffected = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (numRowsAffected != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        long bookId = ContentUris.parseId(uri);
        String[] authorsNamesArray = ArrayUtils.explode(authorsNames);
        if (deleteBookAuthors(database, bookId)) {
            for (int i = 0; i < authorsNamesArray.length; i++) {
                long authorId = saveAuthor(context, database, authorsNamesArray[i]);
                if (authorId > 0) {
                    saveBookAuthor(database, bookId, authorId);
                }
            }
        }

        return numRowsAffected;
    }

    static int delete(Context context, BookDbHelper dbHelper, Uri uri, String selection, String[] selectionArgs) {
        long bookId = ContentUris.parseId(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int numRowsAffected = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
        if (numRowsAffected != 0) {
            deleteBookAuthors(database, bookId);
            context.getContentResolver().notifyChange(uri, null);
        }

        return numRowsAffected;
    }

    private static long saveAuthor(Context context, SQLiteDatabase database, String authorName) {
        long authorId = 0;
        if (authorName != null && !TextUtils.isEmpty(authorName)) {
            ContentValues authorValues = new ContentValues();
            authorValues.put(AuthorContract.AuthorEntry.COLUMN_NAME, authorName);
            authorId = database.insertWithOnConflict(AuthorContract.AuthorEntry.TABLE_NAME, null, authorValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        if (authorId == -1) { // author was already in DB
            String sql = "SELECT " + AuthorContract.AuthorEntry._ID + " " +
                    "FROM " + AuthorContract.AuthorEntry.TABLE_NAME + " " +
                    "WHERE " + AuthorContract.AuthorEntry.COLUMN_NAME + " = ?";
            Cursor cursor = database.rawQuery(sql, new String[]{authorName});
            if (cursor != null) {
                cursor.moveToFirst();
                authorId = cursor.getLong(cursor.getColumnIndex("_id"));
            }
        } else {
            context.getContentResolver().notifyChange(AuthorContract.AuthorEntry.CONTENT_URI, null);
        }

        return authorId;
    }

    private static boolean saveBookAuthor(SQLiteDatabase database, long bookId, long authorId) {
        ContentValues bookAuthorValues = new ContentValues();
        bookAuthorValues.put(BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID, authorId);
        bookAuthorValues.put(BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID, bookId);
        long id = database.insert(BookAuthorContract.BookAuthorEntry.TABLE_NAME, null, bookAuthorValues);
        if (id == -1) {
            return false;
        }

        return true;
    }

    private static boolean deleteBookAuthors(SQLiteDatabase database, long bookId) {
        String selection = BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(bookId)};
        int rowsAffected = database.delete(BookAuthorContract.BookAuthorEntry.TABLE_NAME, selection, selectionArgs);
        return rowsAffected > -1;
    }
}
