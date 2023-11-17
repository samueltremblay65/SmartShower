package com.example.smartshower;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartRecommendationCreator {
    List<UserPreset> recommendations;
    Context context;

    private UserAccount account;

    private FirebaseFirestore db;
    
    private StatisticsCompiler statisticsCompiler;

    public SmartRecommendationCreator(Context context, StatisticsCompiler statisticsCompiler)
    {
        this.context = context;
        this.statisticsCompiler = statisticsCompiler;

        generateRecommendations();
    }

    
    private void generateRecommendations()
    {
        recommendations = new ArrayList<>();
        int temperature = statisticsCompiler.averageTemperature;
        int flowrate = statisticsCompiler.averageFlowRate;

        int seconds = statisticsCompiler.averageShowerDuration;

        int minutes = seconds / 60;

        int timer = minutes * 60;
        
        UserPreset recommendedSettings = new UserPreset("Night time shower", temperature, 40, flowrate, timer, "bg3", -1, 0);
        recommendations.add(recommendedSettings);
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
