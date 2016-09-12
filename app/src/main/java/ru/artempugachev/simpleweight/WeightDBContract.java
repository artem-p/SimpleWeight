package ru.artempugachev.simpleweight;

import android.provider.BaseColumns;

public final class WeightDBContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    // todo правильный запрос на создание
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WeightEntry.TABLE_NAME + " (" +
                    WeightEntry._ID + " INTEGER PRIMARY KEY," +
//                    WeightEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
//                    WeightEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WeightEntry.TABLE_NAME;

    public WeightDBContract() {

    }

    public static abstract class WeightEntry implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String COLUMN_NAME_TIME = "time";
    }
}
