package com.inextends.myratedlibrary;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEditorActivity.class);
                startActivity(intent);
            }
        });
    }
}
