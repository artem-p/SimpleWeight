package ru.artempugachev.simpleweight;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class WeightRecyclerAdapter extends RecyclerView.Adapter<WeightRecyclerAdapter.ViewHolder>{

    // using cursor adapter inside
    private CursorAdapter mCursorAdapter;
    private Context mContext;


    // Build cursor adapter with given cursor
    public WeightRecyclerAdapter(Context context, Cursor c) {
        mContext = context;
        mCursorAdapter = new WeightCursorAdapter(context, c);
    }

    // Create new views. Invoked by the layout manager.
    @Override
    public WeightRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);

        return new ViewHolder((TextView) v);
    }

    // Replace the contents of a view. Invoked by the layout manager
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    // Return the size of dataset. Invoked by the layout manager
    @Override
    public int getItemCount() {
        return mCursorAdapter.getCursor().getCount();
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
