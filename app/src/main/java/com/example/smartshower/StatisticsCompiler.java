package com.example.smartshower;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StatisticsCompiler {
    List<Statistics> allStatistics;
    ArrayList<Statistics> todayStatistics;

    ArrayList<Statistics> weekStatistics;

    // Calculated values
    float todayWaterUsage;
    int todayAverageTemperature;
    int todayAverageDuration;
    int todayTotalDuration;

    // Average values
    int averageWaterUsage;
    int averageFlowRate;
    int averageTemperature;
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
        cal.add(Calendar.DATE, -6);
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

        ArrayList<Statistics> reverseList = new ArrayList<>();
        for(int i = 0; i < latestShowerData.size();i++)
        {
            reverseList.add(latestShowerData.get(latestShowerData.size() - i - 1));
        }

        return reverseList;
    }

    public void calculateTodayStatistics()
    {
        todayAverageTemperature = 0;
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
        averageWaterUsage = calculateAverageDailyWaterUsage();
    }

    public DataPoint<Float>[] calculateDailyWaterUsageWeek()
    {
        List<Float>[] weekDayDividedStatistics = new List[7];
        for(int i = 0; i < 7; i++)
        {
            weekDayDividedStatistics[i] = new ArrayList<Float>();
        }

        for (Statistics statistic: weekStatistics) {
            Date date = statistic.parseDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
            weekDayDividedStatistics[weekday-1].add(statistic.waterUsage);
        }

        DataPoint<Float>[] dayUsages= new DataPoint[7];
        for(int i = 0; i < 7; i++)
        {
            dayUsages[i] = calculateFloatSum(weekDayDividedStatistics[i]);
        }
        return dayUsages;
    }

    public DataPoint<Integer>[] calculateDailyTemperatureWeek()
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
            dayAverages[i] = calculateAverage(weekDayDividedStatistics[i]);
        }
        return dayAverages;
    }

    public DataPoint<Integer>[] calculateDailyDurationWeek()
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
            weekDayDividedStatistics[weekday-1].add(statistic.duration);
        }

        DataPoint<Integer>[] dayAverages = new DataPoint[7];
        for(int i = 0; i < 7; i++)
        {
            dayAverages[i] = calculateAverage(weekDayDividedStatistics[i]);
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
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -365);
            Date lastYear = cal.getTime();

            if(statistic.parseDate().after(lastYear))
            {
                int month = statistic.getMonth();
                monthDividedStatistics[month].add(statistic.averageTemperature);
            }
        }

        DataPoint<Integer>[] monthAverages = new DataPoint[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateAverage(monthDividedStatistics[i]);
        }
        return monthAverages;
    }

    public DataPoint<Float>[] calculateAverageDailyUsageYear()
    {
        List<Float>[] monthDividedStatistics = new List[12];
        for(int i = 0; i < 12; i++)
        {
            monthDividedStatistics[i] = new ArrayList<Float>();
        }

        for (Statistics statistic: allStatistics) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -365);
            Date lastYear = cal.getTime();

            if(statistic.parseDate().after(lastYear))
            {
                int month = statistic.getMonth();
                monthDividedStatistics[month].add(statistic.waterUsage);
            }
        }

        DataPoint<Float>[] monthAverages = new DataPoint[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateFloatSum(monthDividedStatistics[i]);
        }

        for(DataPoint<Float> point: monthAverages)
        {
            point.value = point.value / 30;
        }

        return monthAverages;
    }

    public DataPoint<Integer>[] calculateAverageDurationPerMonth()
    {
        List<Integer>[] monthDividedStatistics = new List[12];
        for(int i = 0; i < 12; i++)
        {
            monthDividedStatistics[i] = new ArrayList<Integer>();
        }

        for (Statistics statistic: allStatistics) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -365);
            Date lastYear = cal.getTime();

            if(statistic.parseDate().after(lastYear))
            {
                int month = statistic.getMonth();
                monthDividedStatistics[month].add(statistic.duration);
            }
        }

        DataPoint<Integer>[] monthAverages = new DataPoint[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateAverage(monthDividedStatistics[i]);
            Log.i("StatisticsJiraf", monthAverages[i].toString());
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

    public DataPoint<Float> calculateFloatSum(List<Float> values)
    {
        if(values.isEmpty())
        {
            return new DataPoint();
        }
        float sum = 0;
        for (Float value: values) {
            sum += value;
        }
        return new DataPoint<Float>(sum);
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
        Log.i("StatisticsJiraf", "Flow rate: " + sum);
        return sum / allStatistics.size();
    }
    public int calculateAverageDailyWaterUsage()
    {
        if(allStatistics.size() == 0)
            return 0;

        float sum = 0;
        int numDays = 0;
        HashMap<String, Integer> dayCounterMap = new HashMap<>();

        for(Statistics statistic: allStatistics)
        {
            Date date = statistic.parseDate();
            int month = date.getMonth();
            int day = date.getDay();
            int year = date.getYear();
            @SuppressLint("DefaultLocale") String dayHashString = String.format("%d/%d/%d", month, day, year);

            if(!dayCounterMap.containsKey(dayHashString))
            {
                numDays++;
                dayCounterMap.put(dayHashString, 1);
            }
            sum += statistic.waterUsage;
        }
        return Math.round(sum / numDays);
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

    public int calculateWeeklyAverageFlow()
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

    public ArrayList<Statistics> getStatisticsForTimeRange(int start, int end)
    {
        ArrayList<Statistics> statistics = new ArrayList<>();
        for(Statistics statistic: allStatistics)
        {
            Date date = statistic.parseDate();
            if(date.getHours() >= start && date.getHours() <= end)
            {
                statistics.add(statistic);
            }
        }
        return statistics;
    }

    public ArrayList<Statistics> getStatisticsForMonth(int month)
    {
        ArrayList<Statistics> statistics = new ArrayList<>();
        for(Statistics statistic: allStatistics)
        {
            if(statistic.getMonth() == month)
            {
                statistics.add(statistic);
            }
        }
        return statistics;
    }

    public ArrayList<Statistics> getStatisticsForWeekday(int weekday)
    {
        ArrayList<Statistics> statistics = new ArrayList<>();
        for(Statistics statistic: allStatistics)
        {
            Date date = statistic.parseDate();
            if(date.getDay() == weekday)
            {
                statistics.add(statistic);
            }
        }
        return statistics;
    }
    
    public static int calculateAverageTemperatureFromList(List<Statistics> statistics)
    {
        if(statistics.isEmpty())
        {
            return 0;
        }

        int sum = 0;
        for(Statistics statistic: statistics)
        {
            sum += statistic.averageTemperature;
        }
        return sum / statistics.size();
    }

    private class StatisticDateComparator implements Comparator<Statistics> {
        @Override
        public int compare(Statistics o1, Statistics o2) {
            return Long.compare(o1.dateTime, o2.dateTime);
        }
    }
}
