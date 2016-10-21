package ru.artempugachev.simpleweight;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ru.artempugachev.simpleweight.WeightChart.TIME_CONSTANT;

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
    TimeAxisFormatter timeAxisFormatter;


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
//        xAxis.setGranularityEnabled(true);
//        xAxis.setGranularity(60*1000*60*2);
        timeAxisFormatter = new TimeAxisFormatter(0L);
        xAxis.setValueFormatter(timeAxisFormatter);
        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setGranularity(1f);
        leftYAxis.setGranularityEnabled(true);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
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

        long[] minMaxTimeStamps = getMinMaxViewportTimeStamp();
        long minMaxTimeDelta = 0;
        if(minMaxTimeStamps.length > 0) {
            minMaxTimeDelta = minMaxTimeStamps[1] - minMaxTimeStamps[0];
        }
        timeAxisFormatter.setMinMaxTimeDelta(minMaxTimeDelta);
        timeAxisFormatter.setLabelCount(chart.getXAxis().getLabelCount());
    }

    private long[] getMinMaxViewportTimeStamp() {
        ViewPortHandler handler = chart.getViewPortHandler();
        MPPointD left = chart.getValuesByTouchPoint(handler.contentLeft(), handler.contentTop(), YAxis.AxisDependency.LEFT);
        MPPointD right = chart.getValuesByTouchPoint(handler.contentRight(), handler.contentBottom(), YAxis.AxisDependency.LEFT);

        long minVal = (long) left.x;
        long maxVal = (long) right.x;

        long trueMin = minVal + TIME_CONSTANT;
        long trueMax = maxVal + TIME_CONSTANT;

        return new long[]{trueMin, trueMax};
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

class TimeAxisFormatter implements AxisValueFormatter {
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat hourMinSecFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat format = dayFormat;
    private int labelCount;

    public void setMinMaxTimeDelta(long minMaxTimeDelta) {
        this.minMaxTimeDelta = minMaxTimeDelta;
    }

    public void setLabelCount(int labelCount) {
        this.labelCount = labelCount;
    }

    private long minMaxTimeDelta;

    public TimeAxisFormatter(long minMaxTimeDelta) {
        this.minMaxTimeDelta = minMaxTimeDelta;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // Choose label format depending of min and max time value in viewport
        if(minMaxTimeDelta < 60*1000) {
            //  less than a minute
            format = hourMinSecFormat;
        } else if (minMaxTimeDelta < 60*1000*60) {
            // less than hour
            format = hourMinFormat;
        } else if (minMaxTimeDelta < 60*1000*60*24) {
            // day
            format = hourMinFormat;
        } else if (minMaxTimeDelta < 60*1000*60*24*labelCount) {
            // few days less than label count
            format = hourMinFormat;
        } else if (minMaxTimeDelta < (long) 60*1000*60*24*30) {
            // month
            format = dayFormat;
        } else {
            format = monthFormat;
        }

        // add previosly subtracted constant
        long trueTimestampValue = (long) value + TIME_CONSTANT;
//                    long millis = TimeUnit.HOURS.toMillis((long) value);
        return format.format(new Date((trueTimestampValue)));

//                    Date time = new Date(millis);
//                    String sTime = DateFormat.getDateTimeInstance().format(time);
//                    return sTime;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }

}