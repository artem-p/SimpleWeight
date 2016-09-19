package ru.artempugachev.simpleweight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText etWeightInput;
    private Button saveButton;
    private WeightDBOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new WeightDBOpenHelper(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etWeightInput = (EditText) findViewById(R.id.input_weight);
        saveButton = (Button) findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new SaveOnClickListener());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] weightProjection = {
                WeightDBContract.WeightEntry._ID,
                WeightDBContract.WeightEntry.COLUMN_NAME_TIME,
                WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT
        };

        String weightSortOrder = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " DESC";

        Cursor cursor = db.query(
                WeightDBContract.WeightEntry.TABLE_NAME,
                weightProjection, null, null, null, null, weightSortOrder
        );



        ListView lvWeight = (ListView) findViewById(R.id.weight_list);
        WeightCursorAdapter weightCursorAdapter = new WeightCursorAdapter(this, cursor);
        lvWeight.setAdapter(weightCursorAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Show settings
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private class SaveOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int weight;
            String sWeight = String.valueOf(etWeightInput.getText());

            if(!sWeight.equals("")) {
                long timestamp = System.currentTimeMillis();
                weight = Integer.parseInt(sWeight);
                //  todo async task для записи
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(WeightDBContract.WeightEntry.COLUMN_NAME_TIME, timestamp);
                values.put(WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT, weight);

                long newRowId = db.insert(WeightDBContract.WeightEntry.TABLE_NAME,
                        null, values);
            } else {
                Toast.makeText(getApplicationContext(), R.string.wrong_weight, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
