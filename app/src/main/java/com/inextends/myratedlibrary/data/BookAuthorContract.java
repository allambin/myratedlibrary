package com.inextends.myratedlibrary.data;

import android.provider.BaseColumns;

public class BookAuthorContract {

    private BookAuthorContract() {
    }

    public static class BookAuthorEntry implements BaseColumns {
        public static final String TABLE_NAME = "books_authors";
        public static final String COLUMN_BOOK_ID = "bookid";
        public static final String COLUMN_AUTHOR_ID = "authorid";
    }
}
