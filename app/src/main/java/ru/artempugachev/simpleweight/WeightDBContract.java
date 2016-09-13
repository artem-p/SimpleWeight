package ru.artempugachev.simpleweight;

import android.provider.BaseColumns;

public final class WeightDBContract {




    public WeightDBContract() {

    }

    public static abstract class WeightEntry implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_WEIGHT = "weight_val";
    }
}
