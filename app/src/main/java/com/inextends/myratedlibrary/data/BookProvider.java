package com.inextends.myratedlibrary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.inextends.myratedlibrary.ArrayUtils;

public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    private BookDbHelper mDbHelper;
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final int AUTHORS = 102;
    private static final int AUTHORS_ID = 103;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //TODO create an url "books with authors"
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
        sUriMatcher.addURI(AuthorContract.CONTENT_AUTHORITY, AuthorContract.PATH_AUTHORS, AUTHORS);
        sUriMatcher.addURI(AuthorContract.CONTENT_AUTHORITY, AuthorContract.PATH_AUTHORS + "/#", AUTHORS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS: {
                String sql = "SELECT b." + BookContract.BookEntry._ID + " as _id, " + BookContract.BookEntry.COLUMN_TITLE + ", " + AuthorContract.AuthorEntry.COLUMN_NAME + " " +
                        "FROM " + BookContract.BookEntry.TABLE_NAME + " as b " +
                        "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                        "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                        "INNER JOIN " + AuthorContract.AuthorEntry.TABLE_NAME + " as a " +
                        "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " = a." + AuthorContract.AuthorEntry._ID + " ";
                cursor = database.rawQuery(sql, selectionArgs);
                //cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case BOOKS_ID:
                //selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                String sql = "SELECT b." + BookContract.BookEntry._ID + " as _id, " + BookContract.BookEntry.COLUMN_TITLE + ", " +
                        AuthorContract.AuthorEntry.COLUMN_NAME + ", " + BookContract.BookEntry.COLUMN_COMMENT + " " +
                        "FROM " + BookContract.BookEntry.TABLE_NAME + " as b " +
                        "INNER JOIN " + BookAuthorContract.BookAuthorEntry.TABLE_NAME + " as ba " +
                        "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + " = b." + BookAuthorContract.BookAuthorEntry._ID + " " +
                        "INNER JOIN " + AuthorContract.AuthorEntry.TABLE_NAME + " as a " +
                        "ON ba." + BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID + " = a." + AuthorContract.AuthorEntry._ID + " " +
                        "WHERE b." + BookContract.BookEntry._ID + "=?";
                cursor = database.rawQuery(sql, selectionArgs);
                //cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case AUTHORS: {
                cursor = database.query(AuthorContract.AuthorEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case AUTHORS_ID:
                selection = AuthorContract.AuthorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AuthorContract.AuthorEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            case AUTHORS:
                return AuthorRepository.insert(getContext(), mDbHelper, uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String title = values.getAsString(BookContract.BookEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a title");
        }
        String authorsNames = values.getAsString(AuthorContract.AuthorEntry.COLUMN_NAME);
        values.remove(AuthorContract.AuthorEntry.COLUMN_NAME);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.d(TAG, "insertBook: insert has failed");
            return null;
        }

        String[] authorsNamesArray = ArrayUtils.explode(authorsNames);
        if (deleteBookAuthors(database, id)) {
            for (int i = 0; i < authorsNamesArray.length; i++) {
                long authorId = saveAuthor(database, authorsNamesArray[i]);
                if (authorId > 0) {
                    saveBookAuthor(database, id, authorId);
                }
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteBook(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int deleteBook(Uri uri, String selection, String[] selectionArgs) {
        long bookId = ContentUris.parseId(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int numRowsAffected = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
        if (numRowsAffected != 0) {
            deleteBookAuthors(database, bookId);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (values.containsKey(BookContract.BookEntry.COLUMN_TITLE)) {
            String title = values.getAsString(BookContract.BookEntry.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }
        String authorsNames = values.getAsString(AuthorContract.AuthorEntry.COLUMN_NAME);
        values.remove(AuthorContract.AuthorEntry.COLUMN_NAME);

        int numRowsAffected = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (numRowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        long bookId = ContentUris.parseId(uri);
        String[] authorsNamesArray = ArrayUtils.explode(authorsNames);
        if (deleteBookAuthors(database, bookId)) {
            for (int i = 0; i < authorsNamesArray.length; i++) {
                long authorId = saveAuthor(database, authorsNamesArray[i]);
                if (authorId > 0) {
                    saveBookAuthor(database, bookId, authorId);
                }
            }
        }

        return numRowsAffected;
    }

    private long saveAuthor(SQLiteDatabase database, String authorName) {
        long authorId = 0;
        if (authorName != null && !TextUtils.isEmpty(authorName)) {
            ContentValues authorValues = new ContentValues();
            authorValues.put(AuthorContract.AuthorEntry.COLUMN_NAME, authorName);
            authorId = database.insertWithOnConflict(AuthorContract.AuthorEntry.TABLE_NAME, null, authorValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        if (authorId == -1) {
            String sql = "SELECT " + AuthorContract.AuthorEntry._ID + " " +
                    "FROM " + AuthorContract.AuthorEntry.TABLE_NAME + " " +
                    "WHERE " + AuthorContract.AuthorEntry.COLUMN_NAME + " = ?";
            Cursor cursor = database.rawQuery(sql, new String[]{authorName});
            if (cursor != null) {
                cursor.moveToFirst();
                authorId = cursor.getLong(cursor.getColumnIndex("_id"));
            }
        }

        return authorId;
    }

    private boolean saveBookAuthor(SQLiteDatabase database, long bookId, long authorId) {
        ContentValues bookAuthorValues = new ContentValues();
        bookAuthorValues.put(BookAuthorContract.BookAuthorEntry.COLUMN_AUTHOR_ID, authorId);
        bookAuthorValues.put(BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID, bookId);
        long id = database.insert(BookAuthorContract.BookAuthorEntry.TABLE_NAME, null, bookAuthorValues);
        if (id == -1) {
            return false;
        }

        return true;
    }

    private boolean deleteBookAuthors(SQLiteDatabase database, long bookId) {
        String selection = BookAuthorContract.BookAuthorEntry.COLUMN_BOOK_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(bookId)};
        int rowsAffected = database.delete(BookAuthorContract.BookAuthorEntry.TABLE_NAME, selection, selectionArgs);
        return rowsAffected > -1;
    }
}
