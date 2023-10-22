package com.example.smartshower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ThemePickerAdapter extends
        RecyclerView.Adapter<ThemePickerAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private ThemePickerClickListener mClickListener;

    private final List<String> themeSources;

    private final Context context;

    private MaterialCardView lastSelected;

    // data is passed into the constructor
    ThemePickerAdapter(Context context, List<String> themeSources) {
        this.mInflater = LayoutInflater.from(context);
        this.themeSources = themeSources;
        this.context = context;
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

        switch(themeSources.get(position))
        {
            case "bg1":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg1));
                break;
            case "bg2":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg2));
                break;
            case "bg3":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg3));
                break;
            case "bg4":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg4));
                break;
            case "bg5":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg5));
                break;
            case "bg6":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg6));
                break;
            case "bg7":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg7));
                break;
            case "bg8":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg8));
                break;
            case "bg9":
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg9));
                break;
        }

    }
    
    public int getPositionForTheme(String theme)
    {
        int position = -1;
        switch(theme)
        {
            case "bg1":
                position = 0;
                break;
            case "bg2":
                position = 1;
                break;
            case "bg3":
                position = 2;
                break;
            case "bg4":
                position = 3;
                break;
            case "bg5":
                position = 4;
                break;
            case "bg6":
                position = 5;
                break;
            case "bg7":
                position = 6;
                break;
            case "bg8":
                position = 7;
                break;
            case "bg9":
                position = 8;
                break;
        }

        return position;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return themeSources.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            imageView = itemView.findViewById(R.id.theme_image);

            cardView = itemView.findViewById(R.id.theme_picker_item_card);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(themeSources.get(getAdapterPosition()));

            cardView.setStrokeWidth(8);
            cardView.setStrokeColor(context.getResources().getColor(R.color.shower_blue300));

            // Remove border from last selected element
            if(lastSelected != null)
            {
                lastSelected.setStrokeWidth(0);
            }

            lastSelected = cardView;
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ThemePickerClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}

