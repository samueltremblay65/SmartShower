package com.example.smartshower;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecommendedSliderFragment extends Fragment {
    UserPreset preset;
    View.OnClickListener listener;
    public RecommendedSliderFragment(UserPreset preset, View.OnClickListener listener)
    {
        this.preset = preset;
        this.listener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shower_preset, container, false);

        // Setup elements within the fragment with preset info
        TextView name = view.findViewById(R.id.tv_preset_name);
        TextView temperature = view.findViewById(R.id.tv_preset_temp);
        TextView temperatureLimit = view.findViewById(R.id.tv_preset_maxTemp);
        TextView flowRate = view.findViewById(R.id.tv_preset_flowrate);
        TextView timer = view.findViewById(R.id.tv_preset_timer);
        LinearLayout presetContainer = view.findViewById(R.id.presetContainer);
        ImageView backgroundView = view.findViewById(R.id.presetThemeBackground);
        ViewHelpers.setBackgroundTheme(backgroundView, preset);

        name.setText(preset.name);
        temperature.setText(String.format("%d°C", preset.temp));
        temperatureLimit.setText(String.format("%d°C limit", preset.tempLimit));
        flowRate.setText(String.format("%d flow rate", preset.flowRate));
        timer.setText(ViewHelpers.formatSeconds(getContext(), preset.secondsLimit));

        // Adding on click listener
        presetContainer.setOnClickListener(listener);
        return view;
    }
}