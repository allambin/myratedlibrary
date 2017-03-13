package com.inextends.myratedlibrary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.inextends.myratedlibrary.data.AuthorContract;

public class AuthorCursorAdapter extends CursorAdapter {

    private static final String TAG = "AuthorCursorAdapter";

    public AuthorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.author_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.text_name);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));

        if (nameTextView != null) {
            nameTextView.setText(name);
        }
    }
}