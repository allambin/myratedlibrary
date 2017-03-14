package com.inextends.myratedlibrary;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.AuthorContract;
import com.inextends.myratedlibrary.data.BookContract;

public class AuthorDetailsActivity extends AppCompatActivity {

    private Uri mCurrentAuthorUri;
    private TextView mAuthorNameTextView;
    private static final int EXISTING_AUTHOR_LOADER_ID = 4;
    private static final int EXISTING_AUTHOR_BOOKS_LOADER_ID = 5;

    private AuthorLoader mAuthorLoader = new AuthorLoader();
    private BookLoader mBookLoader = new BookLoader();
    private BookCursorAdapter mBookCursorAdapter;

    private static final String TAG = "AuthorDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);

        Intent intent = getIntent();
        mCurrentAuthorUri = intent.getData();
        if (mCurrentAuthorUri != null) {
            getLoaderManager().initLoader(EXISTING_AUTHOR_LOADER_ID, null, mAuthorLoader);
            getLoaderManager().initLoader(EXISTING_AUTHOR_BOOKS_LOADER_ID, null, mBookLoader);
        }

        mAuthorNameTextView = (TextView) findViewById(R.id.toolbar_title);

        ListView booksListView = (ListView) findViewById(R.id.list_author_books);
        mBookCursorAdapter = new BookCursorAdapter(this, null);
        booksListView.setAdapter(mBookCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthorDetailsActivity.this, AuthorEditorActivity.class);
                intent.setData(mCurrentAuthorUri);
                startActivity(intent);
            }
        });
    }

    private class AuthorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String[] projection = {
                    AuthorContract.AuthorEntry._ID,
                    AuthorContract.AuthorEntry.COLUMN_NAME
            };
            return new CursorLoader(AuthorDetailsActivity.this, mCurrentAuthorUri, projection, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
                mAuthorNameTextView.setText(name);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAuthorNameTextView.setText("");
        }
    }

    private class BookLoader implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String TAG = "BookLoader";

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String[] projection = {
                    BookContract.BookEntry._ID,
                    BookContract.BookEntry.COLUMN_TITLE
            };

            long authorId = ContentUris.parseId(mCurrentAuthorUri);

            Uri.Builder builder = BookContract.BookEntry.CONTENT_URI.buildUpon();
            builder.appendPath("authorid");
            Uri uri = builder.build();
            return new CursorLoader(AuthorDetailsActivity.this, ContentUris.withAppendedId(uri, authorId), projection, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mBookCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mBookCursorAdapter.swapCursor(null);
        }
    }
}
