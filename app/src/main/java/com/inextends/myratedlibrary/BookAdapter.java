package com.inextends.myratedlibrary;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView bookTitle = (TextView) listItemView.findViewById(R.id.text_title);
        TextView bookAuthorName = (TextView) listItemView.findViewById(R.id.text_author_name);

        bookTitle.setText(currentBook.getTitle());
        bookAuthorName.setText(currentBook.getAuthorName());

        return listItemView;
    }
}
