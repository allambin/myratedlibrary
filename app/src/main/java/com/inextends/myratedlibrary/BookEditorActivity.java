package com.inextends.myratedlibrary;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inextends.myratedlibrary.data.BookContract;
import com.inextends.myratedlibrary.data.BookDbHelper;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button mSaveBookButton;
    private EditText mBookTitleEditText;
    private EditText mBookCommentEditText;
    private BookDbHelper mDbHelper;
    private Uri mCurrentBookUri;
    private static final int EXISTING_BOOK_LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri != null) {
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);
        }

        mSaveBookButton = (Button) findViewById(R.id.button_save);
        mBookTitleEditText = (EditText) findViewById(R.id.edit_title);
        mBookCommentEditText = (EditText) findViewById(R.id.edit_comment);

        mDbHelper = new BookDbHelper(this);

        mSaveBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBook();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentBookUri != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_book_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                return deleteBook();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveBook() {
        String title = mBookTitleEditText.getText().toString().trim();
        if (mCurrentBookUri == null && TextUtils.isEmpty(title)) {
            return;
        }
        String comment = mBookCommentEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_TITLE, title);
        values.put(BookContract.BookEntry.COLUMN_COMMENT, comment);

        if (mCurrentBookUri == null) {
            Uri result = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            if (result == null) {
                Toast.makeText(this, getString(R.string.insert_book_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_book_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_book_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean deleteBook() {
        int rowDeleted = 0;
        if (mCurrentBookUri != null) {
            rowDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowDeleted > 0) {
                Toast.makeText(this, getString(R.string.delete_book_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_book_failure),
                        Toast.LENGTH_SHORT).show();
            }
        }

        return rowDeleted > 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_TITLE,
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
            mBookTitleEditText.setText(title);
            mBookCommentEditText.setText(comment);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTitleEditText.setText("");
        mBookCommentEditText.setText("");
    }
}
