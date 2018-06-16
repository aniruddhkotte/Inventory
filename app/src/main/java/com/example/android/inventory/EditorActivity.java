package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static Uri mCurrentBookUri;
    private static final int EXISTING_BOOK_LOADER = 0;

    private EditText mTitleEditText;
    private EditText mAuthorEditText;
    private EditText mSupplierEditText;
    private EditText mAvailabilityEditText;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        mTitleEditText = findViewById(R.id.edit_book_name);
        mAuthorEditText = findViewById(R.id.edit_book_author);
        mSupplierEditText = findViewById(R.id.edit_supplier);
        mAvailabilityEditText = findViewById(R.id.edit_availability);

        if (mCurrentBookUri == null){
            setTitle(R.string.editor_activity_title_new_book);
            invalidateOptionsMenu();
        }
        else {
            setTitle(R.string.editor_activity_title_edit_book);
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mAvailabilityEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookUri==null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.action_save:
                saveBook();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.home:
                if(!mBookHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBook(){
        String title = mTitleEditText.getText().toString().trim();
        String author = mAuthorEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        String availabilityString = mAvailabilityEditText.getText().toString().trim();
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(title) && TextUtils.isEmpty(author) &&
                TextUtils.isEmpty(supplier)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, title);
        values.put(BookEntry.COLUMN_AUTHOR_NAME, author);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplier);

        int availability = 0;
        if(!TextUtils.isEmpty(availabilityString)){
            availability = Integer.parseInt(availabilityString);
        }
        values.put(BookEntry.COLUMN_AVAILABILITY, availability);

        if (mCurrentBookUri == null){
            Uri returnUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (returnUri == null){
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                long newRowId;
                newRowId = ContentUris.parseId(returnUri);
                Toast.makeText(this, getString(R.string.editor_insert_book_successful)+ " " + Long.toString(newRowId),
                        Toast.LENGTH_SHORT).show();
            }
        }

        else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if(rowsAffected == 0){
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, Integer.toString(rowsAffected) + getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_AVAILABILITY};

        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String title = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));
        String author = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_AUTHOR_NAME));
        String supplier = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME));
        int availability = data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));

        mTitleEditText.setText(title);
        mAuthorEditText.setText(author);
        mSupplierEditText.setText(supplier);
        mAvailabilityEditText.setText(String.valueOf(availability));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.getText().clear();
        mAuthorEditText.getText().clear();
        mSupplierEditText.getText().clear();
        mAvailabilityEditText.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}