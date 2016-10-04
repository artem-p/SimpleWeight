package ru.artempugachev.simpleweight;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private final String WEIGHT_LIST_SORT_ORDER = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " DESC";

    private final String WEIGHT_CHART_SORT_ORDER = WeightDBContract.WeightEntry.COLUMN_NAME_TIME + " ASC";


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

        Cursor listCursor = getCurrentCursor(db);
        // list
        ListView lvWeight = (ListView) findViewById(R.id.weight_list);
        weightCursorAdapter = new WeightCursorAdapter(this, listCursor);
        lvWeight.setAdapter(weightCursorAdapter);
        lvWeight.setOnItemLongClickListener(new OnWeightItemLongClickListener());

        // chart
        Cursor chartCursor = getCursorForChart(db);
        try {
            LineChart chart = (LineChart) findViewById(R.id.weight_chart);
            ChartMarkerView mv = new ChartMarkerView(this, R.layout.weight_marker_layout);
            chart.setMarkerView(mv);
            chart.setDescription("");
            chart.setDragDecelerationFrictionCoef(0.9f);
            chart.setAutoScaleMinMaxEnabled(true);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setDrawGridBackground(false);
            chart.setHighlightPerDragEnabled(true);

            // set an alternative background color
            chart.setBackgroundColor(Color.WHITE);
//            chart.setViewPortOffsets(0f, 0f, 0f, 0f);
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(Color.rgb(255, 192, 56));
            xAxis.setCenterAxisLabels(true);
//            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new AxisValueFormatter() {

                private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {

//                    long millis = TimeUnit.HOURS.toMillis((long) value);
                    return mFormat.format(new Date((long)value));

//                    Date time = new Date(millis);
//                    String sTime = DateFormat.getDateTimeInstance().format(time);
//                    return sTime;
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
            YAxis rightYAxis = chart.getAxisRight();
            rightYAxis.setEnabled(false);

            List<Entry> entries = new ArrayList<Entry>();
            while (chartCursor.moveToNext()) {
                String sWeight = chartCursor.getString(chartCursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT));
                String sTimestamp = chartCursor.getString(chartCursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_TIME));

                float weight = Float.parseFloat(sWeight);
                long timestamp = Long.parseLong(sTimestamp);

                entries.add(new Entry(timestamp, weight));
            }

            LineDataSet dataSet = new LineDataSet(entries, getString(R.string.input_weight_label));
            dataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
            dataSet.setCircleRadius(5f);
            dataSet.setFillColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawValues(false);
            dataSet.setValueTextSize(10f);
            dataSet.setValueTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
            dataSet.setHighlightEnabled(true);
            dataSet.setDrawHorizontalHighlightIndicator(false);
            dataSet.setDrawVerticalHighlightIndicator(false);
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf(value);
                }
            });
            LineData weightData = new LineData(dataSet);
            chart.setData(weightData);
            chart.invalidate();
        }
        finally {
            chartCursor.close();

        }



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
                WEIGHT_PROJECTION, null, null, null, null, WEIGHT_LIST_SORT_ORDER
        );

        return cursor;
    }

    private Cursor getCursorForChart(SQLiteDatabase db) {
        Cursor cursor = db.query(
                WeightDBContract.WeightEntry.TABLE_NAME,
                WEIGHT_PROJECTION, null, null, null, null, WEIGHT_CHART_SORT_ORDER
        );

        return cursor;
    }

    private class SaveOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            float weight;
            String sWeight = String.valueOf(etWeightInput.getText());

            if(!sWeight.equals("")) {
                long timestamp = System.currentTimeMillis();
                weight = Float.parseFloat(sWeight);
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

    private class OnWeightItemLongClickListener implements AdapterView.OnItemLongClickListener {
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
    }

}
