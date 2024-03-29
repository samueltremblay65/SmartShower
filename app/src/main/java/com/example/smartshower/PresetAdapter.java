package com.example.smartshower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apachat.swipereveallayout.core.SwipeLayout;
import com.apachat.swipereveallayout.core.ViewBinder;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PresetAdapter extends
        RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<UserPreset> allPresets;

    // Used for swipe reveal layout
    private final ViewBinder viewBinder = new ViewBinder();
    private SwipeLayout swipeRevealLayout;

    private Context context;

    // Pass in the contact array into the constructor
    public PresetAdapter(
            Context context,
            List<UserPreset> presets,
            PresetClickListener presetClickListener,
            PresetClickListener presetEditListener,
            PresetClickListener presetDeleteListener) {

        this.context = context;
        allPresets = presets;
        this.presetClickListener = presetClickListener;
        this.presetDeleteListener = presetDeleteListener;
        this.presetEditListener = presetEditListener;
    }

    // Item click listener for selecting presets
    public PresetClickListener presetClickListener;
    public PresetClickListener presetDeleteListener;
    public PresetClickListener presetEditListener;

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PresetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View presetView = inflater.inflate(R.layout.preset_swipeview, parent, false);

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

        viewBinder.bind(holder.swipeLayout, Integer.toString(userPreset.uid));
        viewBinder.setOpenOnlyOne(true);

        // Setting text in each textView of the preset layout
        TextView nameView = holder.nameTextView;
        nameView.setText(userPreset.name);

        TextView tempView = holder.tempTextView;
        tempView.setText(String.format("%s°C", userPreset.temp));

        TextView maxTempView = holder.maxTempTextView;
        maxTempView.setText(String.format("%d°C limit", userPreset.tempLimit));

        TextView flowRateView = holder.flowRateTextView;
        flowRateView.setText(String.format("%d%% flow rate", userPreset.flowRate));

        TextView timerView = holder.timerTextView;
        timerView.setText(ViewHelpers.formatSeconds(context, userPreset.secondsLimit));

        ImageView backgroundView = holder.backgroundView;
        ViewHelpers.setBackgroundTheme(backgroundView, userPreset);
        ViewHelpers.setTranslucentBox(context, holder.translucentBox, userPreset.theme);

        // Adding on click listener
        holder.presetContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetClickListener.onItemClick(userPreset);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                presetDeleteListener.onItemClick(userPreset);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                presetEditListener.onItemClick(userPreset);
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

        public TextView timerTextView;

        public LinearLayout presetContainer;

        public LinearLayout translucentBox;

        public ImageView backgroundView;

        public SwipeLayout swipeLayout;
        
        public MaterialButton deleteButton;

        public MaterialButton editButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tv_preset_name);
            tempTextView = itemView.findViewById(R.id.tv_preset_temp);
            maxTempTextView = itemView.findViewById(R.id.tv_preset_maxTemp);
            timerTextView = itemView.findViewById(R.id.tv_preset_timer);
            flowRateTextView = itemView.findViewById(R.id.tv_preset_flowrate);
            presetContainer = itemView.findViewById(R.id.presetContainer);
            backgroundView = itemView.findViewById(R.id.presetThemeBackground);
            translucentBox = itemView.findViewById(R.id.presetTextContainer);
            swipeLayout = itemView.findViewById(R.id.home_swipereveallayout);
            deleteButton = itemView.findViewById(R.id.swipe_delete_button);
            editButton = itemView.findViewById(R.id.swipe_edit_button);
        }
    }
}
