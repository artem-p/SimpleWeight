package ru.artempugachev.simpleweight;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.entries;

public class WeightChart {
    private LineChart chart;
    private final View chartView;
    private final Context context;
    private final int markerLayoutId;
    private final String weightLabel;
    private List<Entry> entries;
    private LineDataSet dataSet;
    private LineData weightData;
    private Entry selectedEntry;
    private OnChartValueSelectedListener onChartValueSelectedListener;
    public final static long TIME_CONSTANT = 1476600000000L; //1476696720644

    public WeightChart(Context context, View chartView, int markerLayoutId, String weightLabel,
                       OnChartValueSelectedListener onChartValueSelectedListener) {
        this.chartView = chartView;
        this.context = context;
        this.markerLayoutId = markerLayoutId;
        this.onChartValueSelectedListener = onChartValueSelectedListener;
        this.weightLabel = weightLabel;
    }

    public void build() {
        //  build chart
        chart = (LineChart) chartView;
        ChartMarkerView mv = new ChartMarkerView(context, markerLayoutId);
        chart.setMarkerView(mv);
        chart.setDescription("");
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setAutoScaleMinMaxEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setOnChartValueSelectedListener(onChartValueSelectedListener);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
//            xAxis.setGranularityEnabled(true);
//        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // add previosly subtracted constant
                long trueTimestampValue = (long) value + TIME_CONSTANT;
//                    long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date((trueTimestampValue)));

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
    }


    public void addData(DBWrapper dbWrapper) {
        //  add data to chart
        entries = new ArrayList<Entry>();
        Cursor cursor = dbWrapper.getCursorForChart();

        try {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry._ID));
                String sWeight = cursor.getString(cursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT));
                String sTimestamp = cursor.getString(cursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_TIME));

                float weight = Float.parseFloat(sWeight);
                long timestamp = Long.parseLong(sTimestamp);

                //  timestamp stores as long. Chart uses float. It result to precision missing (points
                //  with few seconds delta displays with the same time value).
                //  To avoid it, subtract the big constant from timestamp and it will be small enough for
                //  good precision. And add this constant in axis value formatter.
                Long timestampDelta = timestamp - TIME_CONSTANT;
                float chartTimestamp = timestampDelta.floatValue();
                entries.add(new Entry(chartTimestamp, weight, id));
            }
        }
        finally {
            cursor.close();

        }

        dataSet = new LineDataSet(entries, weightLabel);
        dataSet.setColor(ContextCompat.getColor(context, R.color.accent));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(ContextCompat.getColor(context, R.color.accent));
        dataSet.setCircleRadius(5f);
        dataSet.setFillColor(ContextCompat.getColor(context, R.color.accent));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.accent));
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf(value);
            }
        });
        weightData = new LineData(dataSet);
        chart.setData(weightData);
        chart.invalidate();
    }

    public void clear() {
        // clear chart
    }

    public Entry getSelectedEntry() {
        return selectedEntry;
    }

    public void clearSelection() {
        chart.highlightValues(null);
    }
}