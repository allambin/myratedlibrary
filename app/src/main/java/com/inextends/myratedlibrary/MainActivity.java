package com.inextends.myratedlibrary;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.inextends.myratedlibrary.data.BookContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookCursorAdapter mBookCursorAdapter;
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_list);

        final ArrayList<Book> books = new ArrayList<>();
        books.add(new Book("Death on the Nile", "Agatha Christie", 5));
        books.add(new Book("Dome", "Stephen King", 5));
        books.add(new Book("Doctor Sleep", "Stephen King", 3));

        ListView booksListView = (ListView) findViewById(R.id.list_books);

        mBookCursorAdapter = new BookCursorAdapter(this, null);
        booksListView.setAdapter(mBookCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_TITLE
        };
        return new CursorLoader(this, BookContract.BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mBookCursorAdapter.swapCursor(null);
    }
}
