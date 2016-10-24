package ru.artempugachev.simpleweight;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private Toolbar toolbar;
    private EditText etWeightInput;
    private Button saveButton;
//    private WeightCursorAdapter weightCursorAdapter;
    private MenuItem deleteActionBtn;
    private Entry selectedEntry;        //  selected (highlightedt) entry on chart
    private WeightChart chart;
    private DBWrapper dbWrapper;
    private FloatingActionButton addWeightFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbWrapper = new DBWrapper(getApplicationContext());
        dbWrapper.connect();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addWeightFab = (FloatingActionButton) findViewById(R.id.addWeightFab);
        addWeightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWeightDialog();
            }
        });
//        etWeightInput = (EditText) findViewById(R.id.input_weight);
//        saveButton = (Button) findViewById(R.id.btnSave);
//        saveButton.setOnClickListener(new SaveOnClickListener());


//        Cursor listCursor = dbWrapper.getCurrentCursor();
//        // list
//        ListView lvWeight = (ListView) findViewById(R.id.weight_list);
//        weightCursorAdapter = new WeightCursorAdapter(this, listCursor);
//        lvWeight.setAdapter(weightCursorAdapter);
//        lvWeight.setOnItemLongClickListener(new OnWeightItemLongClickListener());

        // chart
        chart = new WeightChart(this, findViewById(R.id.weight_chart),
                R.layout.weight_marker_layout, getString(R.string.input_weight_label), this);
        chart.build();

        chart.addData(dbWrapper);

    }

    @Override
    protected void onStop() {
        super.onStop();
        dbWrapper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        deleteActionBtn = menu.findItem(R.id.action_delete);
        deleteActionBtn.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Show settings
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete:
                //  delete selected on chart entry
                if(selectedEntry != null) {
                    long id = (long) selectedEntry.getData();
                    deleteWithDialog(id);
                    chart.clearSelection();
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void deletePoint(long id) {
        dbWrapper.deletePoint(id);
        chart.addData(dbWrapper);
        //  Обновляем курсор, чтобы обновился список
//        Cursor c = dbWrapper.getCurrentCursor();
//        weightCursorAdapter.changeCursor(c);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        deleteActionBtn.setVisible(true);
        selectedEntry = e;
    }

    @Override
    public void onNothingSelected() {
        deleteActionBtn.setVisible(false);
        selectedEntry = null;

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
                long newPointId = dbWrapper.addPoint(timestamp, weight);


//                Cursor cursor = dbWrapper.getCurrentCursor();

//                weightCursorAdapter.changeCursor(cursor);
                chart.addData(dbWrapper);
            } else {
                Toast.makeText(getApplicationContext(), R.string.wrong_weight, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class OnWeightItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int position, final long id) {
            deleteWithDialog(id);
            return false;
        }

    }

    private void deleteWithDialog(final long id) {
        //  Удаляем строку с айди из базы
        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setTitle(R.string.delete_question);
        adb.setMessage("Are you sure you want to delete this record?");
        adb.setNegativeButton(R.string.cancel, null);
        adb.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePoint(id);
            }
        });
        adb.show();

    }

    private void showAddWeightDialog() {
        // todo android design dialog
    }
}
