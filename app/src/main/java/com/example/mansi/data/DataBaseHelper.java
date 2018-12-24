package com.example.mansi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "main";
    private static final int DATABASE_VERSION = 1;


    public DataBaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QuoteTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(QuoteTable.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public class tblQuote {
        public static final String TABLE_NAME = "tblQuote";

        public static final String QuoteId = "QuoteId";
        public static final String QuoteText = "QuoteText";
        public static final String AuthorId = "AuthorId";
        public static final String CategoryId = "CategoryId";
        public static final String Favorite = "Favorite";
    }
    public class tblCategory {
        public static final String TABLE_NAME = "tblCategory";

        public static final String CategoryId = "CategoryId";
        public static final String CategoryName = "CategoryName";
    }

    public class tblAuthor {
        public static final String TABLE_NAME = "tblAuthor";

        public static final String AuthorId = "AuthorId";
        public static final String AuthorName = "AuthorName";
        public static final String AuthorImageFile = "AuthorImageFile";
    }

    public class QuoteTable {

        public static final String TABLE_NAME = "finalQuote";
        public static final String _ID = "id";
        public static final String COLUMN_QUOTE_ID = "QId";
        public static final String COLUMN_QUOTE_TEXT = "ext";
        public static final String COLUMN_AUTHOR_ID = "AuthorId";
        public static final String COLUMN_CATEGORY_ID = "CategoryId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_QUOTE_ID + " INTEGER NOT NULL UNIQUE," +
                COLUMN_QUOTE_TEXT + " TEXT NOT NULL," +
                COLUMN_AUTHOR_ID + " TEXT NOT NULL," +
                COLUMN_CATEGORY_ID + " TEXT NOT NULL" +
                ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
