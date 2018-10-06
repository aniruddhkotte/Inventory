package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //ImageView iconImageView = (ImageView) view.findViewById(R.id.icon_image);
        TextView nameTextView = view.findViewById(R.id.title_text);
        TextView authorTextView = view.findViewById(R.id.author_text);
        //TextView bullet = view.findViewById(R.id.bullet);
        TextView availabilityTextView = view.findViewById(R.id.availability_text);
        //Button saleButton = (Button) view.findViewById(R.id.sale_button);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_AUTHOR_NAME));
        String availability = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_AVAILABILITY));

        nameTextView.setText(name);
        authorTextView.setText(author);
        availabilityTextView.setText(availability);
    }
}