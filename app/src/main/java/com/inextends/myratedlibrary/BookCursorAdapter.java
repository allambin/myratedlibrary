package com.inextends.myratedlibrary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.BookContract;

import java.util.ArrayList;

public class BookCursorAdapter extends CursorAdapter {

    private static final String TAG = "BookCursorAdapter";
    private ArrayList<Long> mHiddenItems = new ArrayList<>();
    private int mParentViewId;
    private StarsHandler mStarsHandler = new StarsHandler();

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout = R.layout.book_item;
        if (mParentViewId == R.id.list_author_books) {
            layout = R.layout.book_item_for_author;
        }
        return LayoutInflater.from(context).inflate(layout, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mParentViewId == 0) {
            mParentViewId = parent.getId();
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mStarsHandler.clearStarImageViews();

        TextView titleTextView = (TextView) view.findViewById(R.id.text_title);

        if (mParentViewId == R.id.list_author_books) { // we are in the author details page
            String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_TITLE));
            if (titleTextView != null) {
                titleTextView.setText(title);
            }
            return;
        }

        TextView authorNameTextView = (TextView) view.findViewById(R.id.text_author_name);

        String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_TITLE));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_RATING));
        String authorsNames = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_AUTHORS));

        mStarsHandler.setStarsImageViews(view);

        if (titleTextView != null) {
            titleTextView.setText(title);
        }
        if (authorNameTextView != null) {
            authorNameTextView.setText(authorsNames);
        }

        mStarsHandler.displayStarsStatus(rating);
    }
}
