package com.example.smartshower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class GaugeFragment extends Fragment {

    private String title;
    private float currentValue;
    private float maxValue;

    private String unit;
    private int color;

    private String informationText;

    public GaugeFragment(String title, float currentValue, float maxValue, String unit, int color)
    {
        this(title, currentValue, maxValue, unit, color, "");
    }

    public GaugeFragment(String title, float currentValue, float maxValue, String unit, int color, String informationText)
    {
        this.title = title;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
        this.unit = unit;
        this.color = color;
        this.informationText = informationText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.arc_gauge, container, false);

        ColorArcProgressBar gauge = view.findViewById(R.id.arc_gauge_chart);
        TextView informationTextView = view.findViewById(R.id.tv_gauge_explanation);

        gauge.setFgArcColor(color);
        gauge.setUnit(unit);
        gauge.setMaxValues(maxValue);
        gauge.setTitle(title);
        gauge.setCurrentValues(currentValue);

        informationTextView.setText(informationText);

        return view;
    }
}
