package ru.artempugachev.simpleweight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by user on 17.09.2016.
 */

public class WeightRecyclerAdapter extends RecyclerView.Adapter<WeightRecyclerAdapter.ViewHolder>{
    //  Заглушка с фиксированными строками
    private String[] mDataSet;


    // Constructor depends on the kind of dataset
    public WeightRecyclerAdapter(String[] dataset) {
        mDataSet = dataset;
    }

    // Create new views. Invoked by the layout manager.
    @Override
    public WeightRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_view, parent, false);

        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }

    // Replace the contents of a view. Invoked by the layout manager
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataSet[position]);
    }

    // Return the size of dataset. Invoked by the layout manager
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    // Holder for views for each data item. Complex data item
    // may need more than one view and all the views stored in view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(TextView itemView) {
            super(itemView);
            mTextView = itemView;
        }
    }
}
