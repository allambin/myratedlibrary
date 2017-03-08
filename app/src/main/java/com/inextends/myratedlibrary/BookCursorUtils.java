package com.inextends.myratedlibrary;

import android.database.Cursor;

import com.inextends.myratedlibrary.data.AuthorContract;

import java.util.ArrayList;

public class BookCursorUtils {

    public static String[] getNextAuthorsNames(Cursor cursor, ArrayList<String> authorsNames) {
        boolean moveToNext = true;
        while (moveToNext && cursor.moveToNext()) {
            String nextAuthorName = cursor.getString(cursor.getColumnIndexOrThrow(AuthorContract.AuthorEntry.COLUMN_NAME));
            authorsNames.add(nextAuthorName);
        }

        return authorsNames.toArray(new String[0]);
    }

}
