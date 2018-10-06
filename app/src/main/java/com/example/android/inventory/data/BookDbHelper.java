package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books.db";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME +
                " (" + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_AUTHOR_NAME + " TEXT DEFAULT 'N/A', " +
                BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, " +
                BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT DEFAULT 'N/A', " +
                BookEntry.COLUMN_AVAILABILITY + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
