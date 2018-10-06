package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    private BookDbHelper mDbHelper;

    private static final int BOOKS =100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                cursor = db.query(BookEntry.TABLE_NAME,
                        projection,
                        null, null, null,
                        null, sortOrder);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(BookEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

                default:
                    return null;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

                default:
                    return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        //Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null){
            return null;
        }

        //If availability is provided check that it is >= 0
        Integer availability = values.getAsInteger(BookEntry.COLUMN_AVAILABILITY);
        if (availability != null && availability < 0){
            return null;
        }

        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return insertBook(uri, values);

                default:
                    return null;
        }
    }

    private Uri insertBook(Uri uri, ContentValues values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match){
            case BOOKS:
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateBook (Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size() == 0) {
            return 0;
        }

        //Check that if name column is present, it's not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)){
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (name == null){
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_AVAILABILITY)){
            Integer availability = values.getAsInteger(BookEntry.COLUMN_AVAILABILITY);
            if (availability != null && availability < 0){
                return 0;
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}