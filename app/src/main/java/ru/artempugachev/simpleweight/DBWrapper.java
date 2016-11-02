package ru.artempugachev.simpleweight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBWrapper {
    // wrapper for db stuff
    private WeightDBOpenHelper dbHelper;
    private Context context;
    private final String[] WEIGHT_PROJECTION = {
            WeightDBContract.WeightEntry._ID,
            WeightDBContract.WeightEntry.COLUMN_NAME_TIME,
            WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT
    };

    private final String WEIGHT_LIST_SORT_ORDER = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " DESC";
    private final String WEIGHT_CHART_SORT_ORDER = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " ASC";

    public DBWrapper(Context context) {
        this.context = context;
    }

    public void connect() {
        // create helper
        dbHelper = new WeightDBOpenHelper(context);
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    public Cursor getCurrentCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                WeightDBContract.WeightEntry.TABLE_NAME,
                WEIGHT_PROJECTION, null, null, null, null, WEIGHT_LIST_SORT_ORDER
        );

        return cursor;
    }

    public Cursor getCursorForChart() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                WeightDBContract.WeightEntry.TABLE_NAME,
                WEIGHT_PROJECTION, null, null, null, null, WEIGHT_CHART_SORT_ORDER
        );

        return cursor;
    }

    public long addPoint(long timestamp, float weight) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightDBContract.WeightEntry.COLUMN_NAME_TIME, timestamp);
        values.put(WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT, weight);
        return db.insert(WeightDBContract.WeightEntry.TABLE_NAME,
                null, values);
    }

    public void deletePoint(final long id) {
        String selection = WeightDBContract.WeightEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(WeightDBContract.WeightEntry.TABLE_NAME, selection, selectionArgs);
    }
}