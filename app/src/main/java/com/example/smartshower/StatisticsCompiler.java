package com.example.smartshower;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import java.util.Date;
import java.util.List;

public class StatisticsCompiler {
    List<Statistics> allStatistics;
    ArrayList<Statistics> todayStatistics;

    ArrayList<Statistics> weekStatistics;

    // Calculated values
    float todayWaterUsage;
    int todayNumberShowers;
    int todayAverageTemperature;
    float todayCost;
    int todayAverageDuration;
    int todayTotalDuration;

    // Average values
    int averageWaterUsage;
    int averageFlowRate;
    int totalNumberShowers;
    int averageTemperature;
    float averageCost;
    int averageShowerDuration;

    StatisticsCompiler(List<Statistics> allStatistics)
    {
        this.allStatistics = allStatistics;

        allStatistics.sort(new StatisticDateComparator());

        todayStatistics = new ArrayList();
        weekStatistics = new ArrayList();

        for(Statistics statistic: allStatistics)
        {
            Date date = statistic.parseDate();
            if(DateUtils.isToday(date))
            {
                todayStatistics.add(statistic);
            }

            if(isWithinLastWeek(date))
            {
                weekStatistics.add(statistic);
            }
        }

        calculateTodayStatistics();

        calculateAllTimeStatistics();
    }
    
    public static boolean isWithinLastWeek(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date lastWeek = cal.getTime();

        return date.after(lastWeek);
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

        if(todayStatistics.isEmpty())
        {
            return;
        }

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

    public void calculateAllTimeStatistics()
    {
        averageTemperature = calculateAverageTemperature();
        averageFlowRate = calculateAverageFlowrate();
        averageShowerDuration = calculateAverageDuration();
    }

    public float calculateCost(int temperature, float waterUsage)
    {
        return waterUsage / 1000 + temperature * waterUsage / 10000;
    }

    public DataPoint<Integer>[] calculateDailyWaterUsageWeek()
    {
        List<Integer>[] weekDayDividedStatistics = new List[7];
        for(int i = 0; i < 7; i++)
        {
            weekDayDividedStatistics[i] = new ArrayList<Integer>();
        }

        for (Statistics statistic: weekStatistics) {
            Date date = statistic.parseDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
            weekDayDividedStatistics[weekday-1].add(statistic.averageTemperature);
        }

        DataPoint<Integer>[] dayAverages = new DataPoint[7];
        for(int i = 0; i < 7; i++)
        {
            dayAverages[i] = calculateSum(weekDayDividedStatistics[i]);
        }
        return dayAverages;
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

    public DataPoint<Integer> calculateSum(List<Integer> values)
    {
        if(values.isEmpty())
        {
            return new DataPoint();
        }
        int sum = 0;
        for (Integer value: values) {
            sum += value;
        }
        return new DataPoint<Integer>(sum);
    }

    public int calculateAverageDuration()
    {
        if(allStatistics.size() == 0)
        {
            return 0;
        }

        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.duration;
        }
        return sum / allStatistics.size();
    }

    public int calculateAverageFlowrate()
    {
        if(allStatistics.size() == 0)
            return 0;
        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.averageFlow;
        }
        return sum / allStatistics.size();
    }
    public int calculateAverageWaterUsage()
    {
        return calculateAverageDuration() * 7 / 60;
    }

    public int calculateAverageTemperature()
    {
        if(allStatistics.size() == 0)
        {
            return 0;
        }

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
