package ru.artempugachev.simpleweight;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeightDBOpenHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_WEIGHT_TABLE =
            "CREATE TABLE " + WeightDBContract.WeightEntry.TABLE_NAME + " (" +
                    WeightDBContract.WeightEntry._ID + " INTEGER PRIMARY KEY, " +
                    WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " INTEGER " +
                    WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT + " INTEGER " +
                    " )";
    private static final String SQL_DELETE_WEIGHT_TABLE =
            "DROP TABLE IF EXISTS " + WeightDBContract.WeightEntry.TABLE_NAME;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Weight.db";

    public WeightDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WEIGHT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
