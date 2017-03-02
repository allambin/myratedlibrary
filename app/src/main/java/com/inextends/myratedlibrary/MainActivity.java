package com.inextends.myratedlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_list);

        final ArrayList<Book> books = new ArrayList<>();
        books.add(new Book("Death on the Nile", "Agatha Christie", 5));
        books.add(new Book("Dome", "Stephen King", 5));
        books.add(new Book("Doctor Sleep", "Stephen King", 3));

        BookAdapter bookAdapter = new BookAdapter(this, books);

        ListView booksListView = (ListView) findViewById(R.id.list_books);
        booksListView.setAdapter(bookAdapter);
    }
}
