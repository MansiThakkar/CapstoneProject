package com.example.mansi.Constant;

import android.net.Uri;
import android.provider.BaseColumns;

public class QuoteDBContract {

    private QuoteDBContract() {
    }

    public static final String AUTHORITY = "com.example.mansi";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_QUOTES = "quotes";

    public static final class Quote implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUOTES).build();

        public static Uri urlForItems(int limit) {
            return CONTENT_URI.buildUpon().appendPath("offset").appendPath(String.valueOf(limit)).build();
        }

        public static final String TABLE_NAME = "finalQuote";
        public static final String COLUMN_QUOTE_ID = "QId";
        public static final String COLUMN_QUOTE_TEXT = "QuoteText";
        public static final String COLUMN_AUTHOR_ID = "AuthorId";
        public static final String COLUMN_CATEGORY_ID = "CategoryId";

        public static final String CREATE_TABLE_QUOTES = "CREATE TABLE " +
                Quote.TABLE_NAME + "(" +
                Quote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Quote.COLUMN_QUOTE_ID + " INTEGER NOT NULL," +
                Quote.COLUMN_QUOTE_TEXT + " TEXT NOT NULL," +
                Quote.COLUMN_AUTHOR_ID + " TEXT NOT NULL," +
                Quote.COLUMN_CATEGORY_ID + " TEXT NOT NULL," +
                ");";
    }
}
