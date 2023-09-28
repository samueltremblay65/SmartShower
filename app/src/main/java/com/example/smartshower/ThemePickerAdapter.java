package com.example.smartshower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThemePickerAdapter extends
        RecyclerView.Adapter<ThemePickerAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ThemePickerClickListener mClickListener;

    private List<String> themeSources;

    // data is passed into the constructor
    ThemePickerAdapter(Context context, List<String> themeSources) {
        this.mInflater = LayoutInflater.from(context);
        this.themeSources = themeSources;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.theme_picker_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String themeSource = themeSources.get(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return themeSources.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(themeSources.get(getAdapterPosition()));
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ThemePickerClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}

