package com.example.smartshower;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class RecommendedSliderFragment extends Fragment {
    UserPreset preset;

    public RecommendedSliderFragment(UserPreset preset)
    {
        this.preset = preset;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shower_preset, container, false);

        // Setup elements within the fragment with preset info
        TextView name = view.findViewById(R.id.tv_preset_name);
        TextView temperature = view.findViewById(R.id.tv_preset_temp);
        TextView temperatureLimit = view.findViewById(R.id.tv_preset_maxTemp);
        TextView flowRate = view.findViewById(R.id.tv_preset_flowrate);

        name.setText(preset.name);
        temperature.setText(String.format("%s degrees", preset.name));
        temperatureLimit.setText(String.format("%d degrees limit", preset.tempLimit));
        flowRate.setText(String.format("%d flow rate", preset.flowRate));

        return view;
    }
}