package com.inextends.myratedlibrary.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    public static final String CONTENT_AUTHORITY = "com.inextends.myratedlibrary";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    private BookContract() {
    }

    public static class BookEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String TABLE_NAME = "books";
        public static final String COLUMN_TITLE = "title";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
    }
}
