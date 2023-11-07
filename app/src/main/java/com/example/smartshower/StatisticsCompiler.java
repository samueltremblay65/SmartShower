package com.example.smartshower;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import static java.lang.Math.max;
import java.util.List;

public class StatisticsCompiler {
    List<Statistics> allStatistics;
    ArrayList<Statistics> todayStatistics;

    // Calculated values
    float todayWaterUsage;
    int todayNumberShowers;
    int todayAverageTemperature;
    float todayCost;
    int todayAverageDuration;
    int todayTotalDuration;

    // Average values
    int averageWaterUsage;
    int totalNumberShowers;
    int averageTemperature;
    float averageCost;
    int averageShowerDuration;

    StatisticsCompiler(List<Statistics> allStatistics)
    {
        this.allStatistics = allStatistics;

        allStatistics.sort(new StatisticDateComparator());

        todayStatistics = new ArrayList<Statistics>();

        for(Statistics statistic: allStatistics)
        {
            if(DateUtils.isToday(statistic.parseDate()))
            {
                todayStatistics.add(statistic);
            }
        }

        calculateTodayStatistics();
    }

    public ArrayList<Statistics> getLatestShowerStatistics(int n)
    {
        ArrayList<Statistics> latestShowerData = new ArrayList<Statistics>();
        for(int i = Math.max(0, allStatistics.size() - n - 1); i < allStatistics.size(); i++)
        {
            latestShowerData.add(allStatistics.get(i));
        }
        return latestShowerData;
    }

    public void calculateTodayStatistics()
    {
        todayAverageTemperature = 0;
        todayCost = 0;
        todayWaterUsage = 0;
        todayAverageDuration = 0;

        for(Statistics statistic: todayStatistics)
        {
            todayAverageTemperature += statistic.averageTemperature;
            todayWaterUsage += statistic.waterUsage;
            todayCost += calculateCost(statistic.averageTemperature, statistic.waterUsage);
            todayAverageDuration += statistic.duration;
        }
        todayTotalDuration = todayAverageDuration;
        todayAverageTemperature = todayAverageTemperature / todayStatistics.size();
        todayAverageDuration = todayAverageDuration / todayStatistics.size();
    }

    public float calculateCost(int temperature, float waterUsage)
    {
        return waterUsage / 1000 + temperature * waterUsage / 10000;
    }

    public DataPoint<Integer>[] calculateAverageTemperaturePerMonth()
    {
        List<Integer>[] monthDividedStatistics = new List[12];
        for(int i = 0; i < 12; i++)
        {
            monthDividedStatistics[i] = new ArrayList<Integer>();
        }

        for (Statistics statistic: allStatistics) {
            int month = statistic.getMonth();
            monthDividedStatistics[month].add(statistic.averageTemperature);
        }

        DataPoint<Integer>[] monthAverages = new DataPoint[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateAverage(monthDividedStatistics[i]);
        }
        return monthAverages;
    }

    public DataPoint<Integer> calculateAverage(List<Integer> values)
    {
        if(values.isEmpty())
        {
            return new DataPoint();
        }
        int sum = 0;
        for (Integer value: values) {
            sum += value;
        }
        return new DataPoint<Integer>(sum / values.size());
    }

    public int calculateAverageDuration()
    {
        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.duration;
        }
        return sum / allStatistics.size();
    }

    public int calculateAverageWaterUsage()
    {
        return calculateAverageDuration() * 7 / 60;
    }

    public int calculateAverageWaterUsagePerDay()
    {
        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.duration;
        }
        return sum / 365; // TODO: calculate the number of days from the first day
    }

    public int calculateAverageTemperature()
    {
        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.averageTemperature;
        }
        return sum / allStatistics.size();
    }

    public int numberShowers()
    {
        return allStatistics.size();
    }

    public float calculateDailyWaterUsage()
    {
        float waterUsage = 0;
        for(Statistics statistic: todayStatistics)
        {
            waterUsage += statistic.waterUsage;
        }
        return waterUsage;
    }

    private class StatisticDateComparator implements Comparator<Statistics> {
        @Override
        public int compare(Statistics o1, Statistics o2) {
            return Long.compare(o1.dateTime, o2.dateTime);
        }
    }
}
