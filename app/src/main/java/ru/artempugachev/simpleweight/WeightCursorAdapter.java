package ru.artempugachev.simpleweight;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeightCursorAdapter extends CursorAdapter {
    public WeightCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.weight_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvWeight = (TextView) view.findViewById(R.id.tvWeight);
        String weight = cursor.getString(cursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_WEIGHT));
        tvWeight.setText(weight);

        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        String sTimestamp = cursor.getString(cursor.getColumnIndexOrThrow(WeightDBContract.WeightEntry.COLUMN_NAME_TIME));
        long timestamp = Long.parseLong(sTimestamp);
        Date date = new Date(timestamp);
        String sTime = DateFormat.getDateTimeInstance().format(date);

        tvTime.setText(sTime);
    }

}
