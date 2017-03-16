package com.inextends.myratedlibrary;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inextends.myratedlibrary.data.AuthorContract;

public class AuthorEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentAuthorUri;
    private static final int EXISTING_AUTHOR_LOADER_ID = 4;
    private EditText mAuthorNameEditText;
    private Button mSaveAuthorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_editor);

        mAuthorNameEditText = (EditText) findViewById(R.id.edit_name);
        mSaveAuthorButton = (Button) findViewById(R.id.button_save);

        Intent intent = getIntent();
        mCurrentAuthorUri = intent.getData();
        if (mCurrentAuthorUri != null) {
            getLoaderManager().initLoader(EXISTING_AUTHOR_LOADER_ID, null, this);
        }

        mSaveAuthorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAuthor();
            }
        });
    }

    private void saveAuthor() {
        String name = mAuthorNameEditText.getText().toString().trim();
        if (mCurrentAuthorUri == null && TextUtils.isEmpty(name)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AuthorContract.AuthorEntry.COLUMN_NAME, name);

        if (mCurrentAuthorUri == null) {
            Uri result = getContentResolver().insert(AuthorContract.AuthorEntry.CONTENT_URI, values);
            if (result == null) {
                Toast.makeText(this, getString(R.string.insert_author_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_author_success),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AuthorEditorActivity.this, AuthorDetailsActivity.class);
                intent.setData(result);
                startActivity(intent);
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentAuthorUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_author_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_author_success),
                        Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(AuthorEditorActivity.this, AuthorDetailsActivity.class);
            intent.setData(mCurrentAuthorUri);
            startActivity(intent);
        }
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
            mAuthorNameEditText.setText(name);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAuthorNameEditText.setText("");
    }
}
