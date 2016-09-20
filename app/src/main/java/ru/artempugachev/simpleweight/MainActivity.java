package ru.artempugachev.simpleweight;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText etWeightInput;
    private Button saveButton;
    private WeightDBOpenHelper dbHelper;
    private WeightCursorAdapter weightCursorAdapter;
    private final String[] WEIGHT_PROJECTION = {
            WeightDBContract.WeightEntry._ID,
            WeightDBContract.WeightEntry.COLUMN_NAME_TIME,
            WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT
    };

    private final String WEIGHT_SORT_ORDER = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " DESC";


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

        Cursor cursor = getCurrentCursor(db);

        ListView lvWeight = (ListView) findViewById(R.id.weight_list);
        weightCursorAdapter = new WeightCursorAdapter(this, cursor);
        lvWeight.setAdapter(weightCursorAdapter);

        // todo OnItemLongClickListener в отдельый класс
        lvWeight.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, final long id) {
                //  Удаляем строку с айди из базы
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle(R.string.delete_question);
                adb.setMessage("Are you sure you want to delete this record?");
                adb.setNegativeButton(R.string.cancel, null);
                adb.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selection = WeightDBContract.WeightEntry._ID + " LIKE ?";
                        String[] selectionArgs = { String.valueOf(id) };
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        db.delete(WeightDBContract.WeightEntry.TABLE_NAME, selection, selectionArgs);

                        //  Обновляем курсор, чтобы обновился список
                        Cursor c = getCurrentCursor(db);
                        weightCursorAdapter.changeCursor(c);
                        db.close();
                    }
                });
                adb.show();

                return false;
            }

        });

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

    private Cursor getCurrentCursor(SQLiteDatabase db) {
        Cursor cursor = db.query(
                WeightDBContract.WeightEntry.TABLE_NAME,
                WEIGHT_PROJECTION, null, null, null, null, WEIGHT_SORT_ORDER
        );

        return cursor;
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
                Cursor cursor = getCurrentCursor(db);

                weightCursorAdapter.changeCursor(cursor);
            } else {
                Toast.makeText(getApplicationContext(), R.string.wrong_weight, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
