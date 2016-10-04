package ru.artempugachev.simpleweight;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by artem on 03.10.16.
 */

public class ChartMarkerView extends MarkerView {
    private TextView tvWeight;

    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvWeight = (TextView) findViewById(R.id.tvWeightMarker);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvWeight.setText(String.valueOf(e.getY()));
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight() - 15;
    }
}
