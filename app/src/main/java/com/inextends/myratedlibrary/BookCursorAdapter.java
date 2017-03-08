package com.inextends.myratedlibrary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.AuthorContract;
import com.inextends.myratedlibrary.data.BookContract;

import java.util.ArrayList;

public class BookCursorAdapter extends CursorAdapter {

    private static final String TAG = "BookCursorAdapter";
    private ArrayList<Long> mHiddenItems = new ArrayList<>();

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long currentBookId = cursor.getLong(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));
        if (mHiddenItems.contains(currentBookId)) {
            return;
        }
        mHiddenItems.add(currentBookId);

        TextView titleTextView = (TextView) view.findViewById(R.id.text_title);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_TITLE));
        TextView authorNameTextView = (TextView) view.findViewById(R.id.text_author_name);
        String authorName = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
        ArrayList<String> authorsNames = new ArrayList<>();
        authorsNames.add(authorName);

        boolean moveToNext = true;
        while (moveToNext && cursor.moveToNext()) {
            long bookId = cursor.getLong(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));
            if (bookId == currentBookId) {
                String nextAuthorName = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
                authorsNames.add(nextAuthorName);
            } else {
                moveToNext = false;
            }
        }

        if (titleTextView != null) {
            titleTextView.setText(title);
        }
        if (authorNameTextView != null) {
            String[] authorsNamesArray = authorsNames.toArray(new String[0]);
            authorNameTextView.setText(ArrayUtils.implode(authorsNamesArray));
        }
    }
}
