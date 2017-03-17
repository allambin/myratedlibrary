package com.inextends.myratedlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.AuthorContract;

public class AuthorCursorAdapter extends CursorAdapter {

    private static final String TAG = "AuthorCursorAdapter";
    private StarsHandler mStarsHandler = new StarsHandler();
    private Resources mResources;

    public AuthorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mResources = context.getResources();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.author_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mStarsHandler.clearStarImageViews();

        TextView nameTextView = (TextView) view.findViewById(R.id.text_name);
        TextView booksCountTextView = (TextView) view.findViewById(R.id.text_books_count);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_RATING));
        int booksCount = cursor.getInt(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_BOOKS_COUNT));

        if (nameTextView != null) {
            nameTextView.setText(name);
        }

        if (booksCountTextView != null) {
            booksCountTextView.setText(mResources.getQuantityString(R.plurals.books_count, booksCount, booksCount));
        }

        mStarsHandler.setStarsImageViews(view);
        mStarsHandler.displayStarsStatus(rating);
    }
}