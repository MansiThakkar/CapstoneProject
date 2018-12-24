package com.example.mansi.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

import com.example.mansi.SqlDBHelper.DatabaseHandler;
import com.example.mansi.data.DataBaseHelper;

public class QuotesAsyncLoader extends CursorLoader {

    Context context;

    public QuotesAsyncLoader(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public QuotesAsyncLoader(@NonNull Context context, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.context = context;
    }

    @Override
    public Cursor loadInBackground() {
        DatabaseHandler db = new DatabaseHandler(context);
        String selectQuery;
        selectQuery = "SELECT Q.QuoteId, Q.QuoteText, Q.CategoryId, Q.Favorite,"
                + " A.AuthorName, C.CategoryName, Q.AuthorId FROM " + DataBaseHelper.tblQuote.TABLE_NAME + " As Q"

                + " INNER JOIN " + DataBaseHelper.tblCategory.TABLE_NAME + " As C ON Q.CategoryId = C.CategoryId"
                + " INNER JOIN " + DataBaseHelper.tblAuthor.TABLE_NAME + " As A ON Q.AuthorId = A.AuthorId";

        selectQuery += " Order By RANDOM()";


        Cursor cursor = db.getReadableDatabase().rawQuery(selectQuery, null);
        return cursor;
    }
}
