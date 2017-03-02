package com.inextends.myratedlibrary;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inextends.myratedlibrary.data.BookContract;

public class BookEditorActivity extends AppCompatActivity {

    private Button mSaveBook;
    private EditText mBookTitle;
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_editor);

        mSaveBook = (Button) findViewById(R.id.button_save);
        mBookTitle = (EditText) findViewById(R.id.edit_title);

        mDbHelper = new BookDbHelper(this);

        mSaveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertBook();
            }
        });
    }

    private void insertBook() {
        String title = mBookTitle.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_TITLE, title);

        Uri result = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
        if (result == null) {
            Toast.makeText(this, getString(R.string.insert_book_failure),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_book_success),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
