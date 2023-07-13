package com.example.smartshower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PresetAdapter extends
        RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<UserPreset> allPresets;

    // Pass in the contact array into the constructor
    public PresetAdapter(List<UserPreset> presets, PresetClickListener presetClickListener) {
        allPresets = presets;
        this.presetClickListener = presetClickListener;
    }

    // Item click listener for selecting presets
    public PresetClickListener presetClickListener;

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PresetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View presetView = inflater.inflate(R.layout.shower_preset, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(presetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(PresetAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        UserPreset userPreset = allPresets.get(position);

        // Setting text in each textView of the preset layout
        TextView nameView = holder.nameTextView;
        nameView.setText(userPreset.name);

        TextView tempView = holder.tempTextView;
        tempView.setText(String.format("%s degrees", userPreset.temp));

        TextView maxTempView = holder.maxTempTextView;
        maxTempView.setText(String.format("%d degrees limit", userPreset.tempLimit));

        TextView flowRateView = holder.flowRateTextView;
        flowRateView.setText(String.format("%d flow rate", userPreset.flowRate));

        // Adding on click listener
        holder.presetContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetClickListener.onItemClick(userPreset);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return allPresets.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView tempTextView;
        public TextView maxTempTextView;
        public TextView flowRateTextView;

        public LinearLayout presetContainer;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.tv_preset_name);
            tempTextView = (TextView) itemView.findViewById(R.id.tv_preset_temp);
            maxTempTextView = (TextView) itemView.findViewById(R.id.tv_preset_maxTemp);
            flowRateTextView = (TextView) itemView.findViewById(R.id.tv_preset_flowrate);
            presetContainer = (LinearLayout) itemView.findViewById(R.id.presetContainer);
        }
    }
}
