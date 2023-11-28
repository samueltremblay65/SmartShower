package com.example.smartshower;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        // Recommendation based on average usage
        UserPreset recommendedSettings = generateUsageBasedRecommendation();
        String recommendedSettingsInfo = "Recommended settings based on your usage habits";

        // Special earth month event
        UserPreset earthMonth = new UserPreset("Environmentally friendly", 25, 40, 80, 300, "earthDay2", -1, 0);
        String earthMonthInfo = "Help save our planet with a cooler and shorter shower to save energy and water";

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

    public UserPreset generateUsageBasedRecommendation()
    {
        String name = generatePresetName();
        int temperature = recommendTemperature();
        int flow = statisticsCompiler.averageFlowRate;

        int seconds = statisticsCompiler.averageShowerDuration;
        int minutes = seconds / 60;
        int timer = minutes * 60;

        UserPreset preset = new UserPreset(0, name, temperature, 40, flow, timer, "bg1", -1, 0);
        return preset;
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

    private String generatePresetName()
    {
        String name;
        // Generate name
        Date date = Calendar.getInstance().getTime();
        if(date.getHours() < 8)
        {
            name = "Early morning shower";
        }
        else if(date.getHours() < 12)
        {
            name = "Afternoon shower";
        }
        else if(date.getHours() < 18)
        {
            name = "Evening shower";
        }
        else
        {
            name = "Night shower";
        }

        return name;
    }

    private int recommendTemperature()
    {
        Date date = Calendar.getInstance().getTime();

        int hour = date.getHours();

        // Get all showers for time of day
        List<Statistics> timeBasedStatistics = statisticsCompiler.getStatisticsForTimeRange(hour - 1, hour + 1);
        int timeTemperature = StatisticsCompiler.calculateAverageTemperatureFromList(timeBasedStatistics);
        
        List<Statistics> monthBasedStatistics = statisticsCompiler.getStatisticsForMonth(date.getMonth());
        int monthTemperature = StatisticsCompiler.calculateAverageTemperatureFromList(monthBasedStatistics);

        List<Statistics> dayBasedStatistics = statisticsCompiler.getStatisticsForWeekday(date.getDay());
        int dayTemperature = StatisticsCompiler.calculateAverageTemperatureFromList(dayBasedStatistics);

        return (50 * timeTemperature + 25 * monthTemperature + 25 * dayTemperature) / 100;
    }
}
