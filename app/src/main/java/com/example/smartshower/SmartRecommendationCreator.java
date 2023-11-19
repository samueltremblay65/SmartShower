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
    List<String> infoStrings;
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
        infoStrings = new ArrayList<>();
        int temperature = statisticsCompiler.averageTemperature;
        int flowrate = statisticsCompiler.averageFlowRate;

        int seconds = statisticsCompiler.averageShowerDuration;

        int minutes = seconds / 60;

        int timer = minutes * 60;

        // Recommendation based on average usage
        UserPreset recommendedSettings = new UserPreset("Night time shower", temperature, 40, flowrate, timer, "night", -1, 0);
        String recommendedSettingsInfo = "Recommended settings based on your usage habits";
        
        // Special earth month event
        UserPreset earthMonth = new UserPreset("Earth month shower", 25, 40, 80, 300, "earthDay2", -1, 0);
        String earthMonthInfo = "Celebrate Earth Month with a cooler and shorter shower to help save energy and water";

        UserPreset decreasingTemperatureShower = new UserPreset("Decreasing temperature shower", 38, 40, 80, 600, "decreasing", -1, 0);
        decreasingTemperatureShower.setInputSequenceName("decreasing");
        String decreasingShowerInfo = "Take a cold shower without the initial shock. Temperature starts at 37°C and gradually decreases with time";
        
        recommendations.add(earthMonth);
        infoStrings.add(earthMonthInfo);
        
        recommendations.add(recommendedSettings);
        infoStrings.add(recommendedSettingsInfo);
        
        recommendations.add(decreasingTemperatureShower);
        infoStrings.add(decreasingShowerInfo);
    }

    public void updatePresets(List<UserPreset> presets)
    {
        this.recommendations = presets;
    }

    public List<UserPreset> getRecommendations()
    {
        return recommendations;
    }

    public List<String> getInformationStrings()
    {
        return infoStrings;
    }
}
