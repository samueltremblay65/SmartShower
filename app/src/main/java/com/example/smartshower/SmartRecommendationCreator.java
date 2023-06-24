package com.example.smartshower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class SmartRecommendationCreator {
    List<UserPreset> recommendations;
    Context context;

    public SmartRecommendationCreator(Context context)
    {
        this.context = context;
    }

    public void updatePresets(List<UserPreset> presets)
    {
        this.recommendations = presets;
    }

    public List<UserPreset> getRecommendations()
    {
        return recommendations;
    }

}
