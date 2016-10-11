package ru.artempugachev.simpleweight;

import android.content.Context;

/**
 * Created by artem on 11.10.16.
 */

public class DBWrapper {
    // wrapper for db stuff
    private WeightDBOpenHelper dbHelper;
    private Context context;

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
}
