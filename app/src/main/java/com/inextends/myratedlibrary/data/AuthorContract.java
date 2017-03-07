package com.inextends.myratedlibrary.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AuthorContract {

    public static final String CONTENT_AUTHORITY = "com.inextends.myratedlibrary";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_AUTHORS = "authors";

    private AuthorContract() {
    }

    public static class AuthorEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_AUTHORS);

        public static final String TABLE_NAME = "authors";
        public static final String COLUMN_NAME = "name";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTHORS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTHORS;
    }
}
