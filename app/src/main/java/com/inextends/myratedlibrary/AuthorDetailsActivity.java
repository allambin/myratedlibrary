package com.inextends.myratedlibrary;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.AuthorContract;

public class AuthorDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentAuthorUri;
    private TextView mAuthorNameTextView;
    private static final int EXISTING_AUTHOR_LOADER_ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);

        Intent intent = getIntent();
        mCurrentAuthorUri = intent.getData();
        if (mCurrentAuthorUri != null) {
            getLoaderManager().initLoader(EXISTING_AUTHOR_LOADER_ID, null, this);
        }

        mAuthorNameTextView = (TextView) findViewById(R.id.toolbar_title);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AuthorContract.AuthorEntry._ID,
                AuthorContract.AuthorEntry.COLUMN_NAME
        };
        return new CursorLoader(this, mCurrentAuthorUri, projection, null, null, null);
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
