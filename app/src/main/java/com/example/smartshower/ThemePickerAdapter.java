package com.example.smartshower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThemePickerAdapter extends
        RecyclerView.Adapter<ThemePickerAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ThemePickerClickListener mClickListener;

    private List<String> themeSources;

    private Context context;

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

    // total number of rows
    @Override
    public int getItemCount() {
        return themeSources.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            imageView = itemView.findViewById(R.id.theme_image);
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

