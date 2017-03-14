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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.inextends.myratedlibrary.data.AuthorContract;
import com.inextends.myratedlibrary.data.BookContract;
import com.inextends.myratedlibrary.data.BookDbHelper;

import java.util.ArrayList;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button mSaveBookButton;
    private EditText mBookTitleEditText;
    private EditText mBookCommentEditText;
    private AutoCompleteTextView mBookAuthorNameAutocomplete;
    private LinearLayout mContainerAuthor;
    private BookDbHelper mDbHelper;
    private Uri mCurrentBookUri;
    private static final int EXISTING_BOOK_LOADER_ID = 2;
    private ArrayList<EditText> mAuthorNameEditTexts = new ArrayList<>();

    SimpleCursorAdapter mAuthorAutocompleteAdapter;

    private static final String TAG = "BookEditorActivity";

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
        mBookAuthorNameAutocomplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_author);
        mContainerAuthor = (LinearLayout) findViewById(R.id.container_author);

        mAuthorNameEditTexts.add(mBookAuthorNameAutocomplete);

        int[] to = new int[]{R.id.text_author_name};
        String[] from = new String[]{AuthorContract.AuthorEntry.COLUMN_NAME};
        mDbHelper = new BookDbHelper(this);
        mAuthorAutocompleteAdapter = new SimpleCursorAdapter(
                this, R.layout.autocomplete_author, null, from, to, 0
        );
        mBookAuthorNameAutocomplete.setAdapter(mAuthorAutocompleteAdapter);
        mAuthorAutocompleteAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                final int columnIndex = cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME);
                final String str = cursor.getString(columnIndex);
                return str;
            }
        });
        mAuthorAutocompleteAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence str) {
                String select = "(" + AuthorContract.AuthorEntry.COLUMN_NAME + " LIKE ?) ";
                String[] selectArgs = {"%" + str + "%"};
                String[] contactsProjection = new String[]{
                        AuthorContract.AuthorEntry._ID,
                        AuthorContract.AuthorEntry.COLUMN_NAME};

                return getContentResolver().query(AuthorContract.AuthorEntry.CONTENT_URI, contactsProjection, select, selectArgs, null);
            }
        });

        mSaveBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBook();
            }
        });

        ImageButton buttonAuthor = (ImageButton) findViewById(R.id.button_author);
        buttonAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAuthorNameEditText();
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
        String authorName = mBookAuthorNameAutocomplete.getText().toString().trim();

        String[] authorNames = new String[mAuthorNameEditTexts.size()];
        int i = 0;
        for (EditText editText : mAuthorNameEditTexts) {
            authorNames[i++] = editText.getText().toString();
        }
        String authorsNamesString = ArrayUtils.implode(authorNames);

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_TITLE, title);
        values.put(BookContract.BookEntry.COLUMN_COMMENT, comment);
        values.put(AuthorContract.AuthorEntry.COLUMN_NAME, authorsNamesString);

        if (mCurrentBookUri == null) {
            Uri result = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            if (result == null) {
                Toast.makeText(this, getString(R.string.insert_book_failure),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_book_success),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BookEditorActivity.this, BookDetailsActivity.class);
                intent.setData(result);
                startActivity(intent);
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

            Intent intent = new Intent(BookEditorActivity.this, BookDetailsActivity.class);
            intent.setData(mCurrentBookUri);
            startActivity(intent);
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

        ArrayList<String> authorsNames = new ArrayList<>();

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_TITLE));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_COMMENT));
            String authorName = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
            mBookTitleEditText.setText(title);
            mBookCommentEditText.setText(comment);
            authorsNames.add(authorName);
        }

        String[] authorsNamesArray = BookCursorUtils.getNextAuthorsNames(cursor, authorsNames);

        // If we have more authors names than available Edit Texts, we create some.
        if (mAuthorNameEditTexts.size() < authorsNamesArray.length) {
            for (int i = mAuthorNameEditTexts.size(); i < authorsNamesArray.length; i++) {
                createAuthorNameEditText();
            }
        }

        // Then we fill them.
        for (int i = 0; i < authorsNamesArray.length; i++) {
            mAuthorNameEditTexts.get(i).setText(authorsNamesArray[i]);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookTitleEditText.setText("");
        mBookCommentEditText.setText("");
        mBookAuthorNameAutocomplete.setText("");
    }

    private void createAuthorNameEditText() {
        createAuthorNameEditText("");
    }

    private void createAuthorNameEditText(String value) {
        LinearLayout layout = new LinearLayout(BookEditorActivity.this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        AutoCompleteTextView authorAutoComplete = new AutoCompleteTextView(BookEditorActivity.this);
        authorAutoComplete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        authorAutoComplete.setHint(R.string.author);
        authorAutoComplete.setText(value);
        authorAutoComplete.setAdapter(mAuthorAutocompleteAdapter);
        layout.addView(authorAutoComplete);

        mAuthorNameEditTexts.add(authorAutoComplete);

        mContainerAuthor.addView(layout);
    }
}
