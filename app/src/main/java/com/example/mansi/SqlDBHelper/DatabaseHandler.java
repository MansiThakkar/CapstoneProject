package com.example.mansi.SqlDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mansi.Constant.QuoteDBContract;
import com.example.mansi.Entities.AuthorEntity;
import com.example.mansi.Entities.CategoryEntity;
import com.example.mansi.Entities.QuoteEntity;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quotedb";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_QUOTE = "tblQuote";
    private static final String TABLE_CATEGORY = "tblCategory";
    private static final String TABLE_AUTHOR = "tblAuthor";


    public static final String Item = null;

    SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public ArrayList<AuthorEntity> GetAllAuthors() {

        ArrayList<AuthorEntity> authorList = new ArrayList<AuthorEntity>();
        String selectQuery = "SELECT AuthorId, AuthorName FROM " + TABLE_AUTHOR
                + " Order By AuthorName";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AuthorEntity author = new AuthorEntity();
                author.setAuthorId(cursor.getInt(cursor.getColumnIndex("AuthorId")));
                author.setAuthorName(cursor.getString(cursor.getColumnIndex("AuthorName")));
//                author.setAuthorImageFile(cursor.getString(2));

                authorList.add(author);
            } while (cursor.moveToNext());
        }

        return authorList;
    }

    public ArrayList<CategoryEntity> GetAllCategories() {

        ArrayList<CategoryEntity> categoryList = new ArrayList<CategoryEntity>();
        String selectQuery = "SELECT CategoryId, CategoryName FROM " + TABLE_CATEGORY + " Order By CategoryName";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CategoryEntity category = new CategoryEntity();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndex("CategoryId")));
                category.setCategoryName(cursor.getString(cursor.getColumnIndex("CategoryName")));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        return categoryList;
    }

    public ArrayList<QuoteEntity> GetFilteredQuotes(int authorId, int categoryId, int sortOrder, String queryString) {

        String selectQuery;
        selectQuery = "SELECT Q.QuoteId, Q.QuoteText, Q.CategoryId, Q.Favorite,"
                + " A.AuthorName, C.CategoryName FROM " + TABLE_QUOTE + " As Q"

                + " INNER JOIN " + TABLE_CATEGORY + " As C ON Q.CategoryId = C.CategoryId"
                + " INNER JOIN " + TABLE_AUTHOR + " As A ON Q.AuthorId = A.AuthorId"
                + " WHERE 1 = 1 ";

        if (authorId != 1) {
            selectQuery += " AND Q.AuthorId = " + authorId;
        }
        if (categoryId != 1) {
            selectQuery += " AND Q.CategoryId = " + categoryId;
        }


        if (queryString != "") {
            selectQuery += " AND Q.QuoteText Like '%" + queryString + "%'";
        }

        if (sortOrder == 0) {
            selectQuery += " Order By QuoteId";
        } else if (sortOrder == 1) {
            selectQuery += " Order By QuoteId Desc";
        } else {
            selectQuery += " Order By RANDOM()";
        }


        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return getQuoteListFromCursor(cursor);
    }

    private ArrayList<QuoteEntity> getQuoteListFromCursor(Cursor cursor) {
        ArrayList<QuoteEntity> quoteList = new ArrayList<QuoteEntity>();
        if (cursor.moveToFirst()) {
            do {
                QuoteEntity quote = new QuoteEntity();
                quote.setQuoteId(cursor.getInt(0));
                quote.setQuoteText(cursor.getString(1));
                quote.setCategoryId(cursor.getInt(2));
                quote.setAuthorName(cursor.getString(4));
                quote.setCategoryName(cursor.getString(5));
                quoteList.add(quote);
            } while (cursor.moveToNext());
        }

        return quoteList;
    }


}
