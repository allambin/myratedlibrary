package com.inextends.myratedlibrary;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.BookContract;

public class BookDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentBookUri;
    private static final int EXISTING_BOOK_LOADER_ID = 2;
    private TextView mBookTitleTextView;
    private TextView mBookCommentTextView;
    private TextView mAuthorNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri != null) {
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);
        }

        mBookTitleTextView = (TextView) findViewById(R.id.toolbar_title);
        mBookCommentTextView = (TextView) findViewById(R.id.text_comment);
        mAuthorNameTextView = (TextView) findViewById(R.id.text_author);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, BookEditorActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_TITLE,
                BookContract.BookEntry.COLUMN_RATING,
                BookContract.BookEntry.COLUMN_AUTHORS,
                BookContract.BookEntry.COLUMN_COMMENT
        };
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_TITLE));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_COMMENT));
            String authorsNames = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_AUTHORS));
            mBookTitleTextView.setText(title);
            mBookCommentTextView.setText(comment);
            mAuthorNameTextView.setText(authorsNames);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTitleTextView.setText("");
        mBookCommentTextView.setText("");
    }
}
