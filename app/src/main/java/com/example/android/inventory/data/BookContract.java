package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public class BookContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     +     * The "Content authority" is a name for the entire content provider, similar to the
     +     * relationship between a domain name and its website.  A convenient string to use for the
     +     * content authority is the package name for the app, which is guaranteed to be unique on the
     +     * device.
     +     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    /**
     +     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     +     * the content provider.
     +     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     +     * Possible path (appended to base content URI for possible URI's)
     +     * For instance, content://com.example.android.pets/pets/ is a valid path for
     +     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     +     * as the ContentProvider hasn't been given any information on what to do with "staff".
     +     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class BookEntry implements BaseColumns{

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**You’ll notice that we’re making use of the constants defined in the
         * ContentResolver class: CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir")
         * and CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”).
         * Hence, add this additional import statement for the ContentResolver class at the top of the PetContract
         * (if it’s not automatically imported already).
         *
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "name";
        public final static String COLUMN_AUTHOR_NAME = "author";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
        public final static String COLUMN_AVAILABILITY = "availability";

    }
}
