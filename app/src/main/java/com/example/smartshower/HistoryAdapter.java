package com.example.smartshower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Statistics> history;

    public HistoryAdapter(ArrayList<Statistics> history)
    {
        this.history = history;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View presetView = inflater.inflate(R.layout.history_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(presetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        Statistics statistic = history.get(position);

        String dateString;
        holder.dateTextView.setText(statistic.parseDate().toString());
        
        String descriptionString;

        descriptionString = String.format("%.1f litres of water used | %d°C average temperature", statistic.waterUsage, statistic.averageTemperature);
        holder.descriptionTextView.setText(descriptionString);

        String durationString = String.format("%d'%02d\"", statistic.duration / 60, statistic.duration % 60);
        holder.durationTextView.setText(durationString);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return history.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView durationTextView;
        TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            
            dateTextView = itemView.findViewById(R.id.history_item_date);
            durationTextView = itemView.findViewById(R.id.history_item_duration);
            descriptionTextView = itemView.findViewById(R.id.history_item_waterusage);
        }
    }
}
