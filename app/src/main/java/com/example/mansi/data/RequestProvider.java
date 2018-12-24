package com.example.mansi.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class RequestProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.mansi";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri QUOTE_URI = Uri.parse("content://" + AUTHORITY).buildUpon().appendPath(DataBaseHelper.QuoteTable.TABLE_NAME).build();
    public static final Uri QUOTE_URI_2 = Uri.parse("content://" + AUTHORITY).buildUpon().appendPath(DataBaseHelper.tblQuote.TABLE_NAME).build();
    public static final Uri AUTHOR_URI = Uri.parse("content://" + AUTHORITY).buildUpon().appendPath(DataBaseHelper.tblQuote.TABLE_NAME).build();
    public static final Uri CATEGORY_URI = Uri.parse("content://" + AUTHORITY).buildUpon().appendPath(DataBaseHelper.tblQuote.TABLE_NAME).build();

    private SQLiteOpenHelper mSqliteOpenHelper;
    private static final UriMatcher sUriMatcher;

    private static final int
            TABLE_QUOTE = 100, TABLE_QUOTE_OFFSET = 101;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DataBaseHelper.QuoteTable.TABLE_NAME + "/offset/" + "#", TABLE_QUOTE_OFFSET);
        sUriMatcher.addURI(AUTHORITY, DataBaseHelper.tblQuote.TABLE_NAME, TABLE_QUOTE);
        sUriMatcher.addURI(AUTHORITY, DataBaseHelper.tblAuthor.TABLE_NAME, TABLE_QUOTE);
        sUriMatcher.addURI(AUTHORITY, DataBaseHelper.tblCategory.TABLE_NAME, TABLE_QUOTE);
    }

    public static Uri urlForItems(int limit) {
        return Uri.parse("content://" + AUTHORITY + "/" + DataBaseHelper.QuoteTable.TABLE_NAME + "/offset/" + limit);
    }

    @Override
    public boolean onCreate() {
        mSqliteOpenHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    synchronized public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mSqliteOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        Cursor c;
        String offset;
        String limitArg = "";

        switch (sUriMatcher.match(uri)) {
            case TABLE_QUOTE: {
                sqb.setTables(DataBaseHelper.QuoteTable.TABLE_NAME);
                break;
            }
            case TABLE_QUOTE_OFFSET: {
                sqb.setTables(DataBaseHelper.QuoteTable.TABLE_NAME);
                offset = uri.getLastPathSegment();
                if (offset != null) {
                    int intOffset = Integer.parseInt(offset);
                    limitArg = intOffset + ", " + 30;
                }
                break;
            }

            default:
                throw new IllegalArgumentException("uri not recognized!");
        }


        c = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limitArg);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return AUTHORITY + ".item";
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table = DataBaseHelper.QuoteTable.TABLE_NAME;
        switch (sUriMatcher.match(uri)) {
            case TABLE_QUOTE: {
                table = DataBaseHelper.QuoteTable.TABLE_NAME;
                break;
            }
            case TABLE_QUOTE_OFFSET: {
                table = DataBaseHelper.QuoteTable.TABLE_NAME;
                break;
            }
        }

        Log.e("INSERT INTO", ": " + table);

        long result = mSqliteOpenHelper.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        Uri retUri = ContentUris.withAppendedId(uri, result);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return -1;
    }
}