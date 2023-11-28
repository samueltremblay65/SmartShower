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
    String info;

    public RecommendedSliderFragment(UserPreset preset, String info, View.OnClickListener listener)
    {
        this.preset = preset;
        this.listener = listener;
        this.info = info;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preset_recommendation, container, false);

        // Setup elements within the fragment with preset info
        TextView name = view.findViewById(R.id.tv_preset_name);
        TextView temperature = view.findViewById(R.id.tv_preset_temp);
        TextView temperatureLimit = view.findViewById(R.id.tv_preset_maxTemp);
        TextView flowRate = view.findViewById(R.id.tv_preset_flowrate);
        TextView timer = view.findViewById(R.id.tv_preset_timer);
        LinearLayout presetContainer = view.findViewById(R.id.presetContainer);
        ImageView backgroundView = view.findViewById(R.id.presetThemeBackground);
        ViewHelpers.setBackgroundTheme(backgroundView, preset);
        TextView infoView = view.findViewById(R.id.preset_recommendation_info);
        LinearLayout translucentBox = view.findViewById(R.id.presetTextContainer);

        ViewHelpers.setTranslucentBox(getContext(), translucentBox, preset.theme);

        name.setText(preset.name);
        temperature.setText(String.format("%d°C", preset.temp));
        temperatureLimit.setText(String.format("%d°C limit", preset.tempLimit));
        flowRate.setText(String.format("%d%% flow rate", preset.flowRate));
        timer.setText(ViewHelpers.formatSeconds(getContext(), preset.secondsLimit));

        infoView.setText(info);
        
        // Adding on click listener
        presetContainer.setOnClickListener(listener);
        return view;
    }
}