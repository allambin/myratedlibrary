package com.inextends.myratedlibrary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    private BookDbHelper mDbHelper;
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final int BOOKS_FROM_AUTHOR = 104;
    private static final int AUTHORS = 102;
    private static final int AUTHORS_ID = 103;
    private static final int AUTHORS_WITH_RATINGS = 105;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/authorid/#", BOOKS_FROM_AUTHOR);
        sUriMatcher.addURI(AuthorContract.CONTENT_AUTHORITY, AuthorContract.PATH_AUTHORS, AUTHORS);
        sUriMatcher.addURI(AuthorContract.CONTENT_AUTHORITY, AuthorContract.PATH_AUTHORS + "/ratings", AUTHORS_WITH_RATINGS);
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
                cursor = BookRepository.fetchAll(database, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = BookRepository.fetchOne(database, projection, selection, selectionArgs, sortOrder);
                break;
            case BOOKS_FROM_AUTHOR:
                cursor = BookSqlHelper.getBooksFromAuthor(database, uri, projection, selection, selectionArgs, sortOrder);
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
            case AUTHORS_WITH_RATINGS:
                cursor = AuthorRepository.fetchAllWithRatings(database);
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
                return BookRepository.insertWithAuthors(getContext(), mDbHelper, uri, contentValues);
            case AUTHORS:
                return AuthorRepository.insert(getContext(), mDbHelper, uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return BookRepository.delete(getContext(), mDbHelper, uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return BookRepository.updateWithAuthors(getContext(), mDbHelper, uri, contentValues, selection, selectionArgs);
            case AUTHORS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return AuthorRepository.update(getContext(), mDbHelper, uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
}
