package com.example.smartshower;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class StatisticsHome extends ActivityWithHeader {

    private List<Statistics> allStatistics;

    private LinearLayout statisticsLayout;

    private ViewPager2 gaugePager;
    private FragmentStateAdapter gaugePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_home);
        super.setupUIElements();

        // Set the header text in the parent class
        this.setHeader("Statistics");

        statisticsLayout = findViewById(R.id.statistics_home_ll);

        // View pager setup
        gaugePager = findViewById(R.id.vp_stats_gauges);

        gaugePagerAdapter = new StatisticsHome.ScreenSlidePagerAdapter(this);
        gaugePager.setAdapter(gaugePagerAdapter);

        // Use following line to generate a year's worth of example shower data in the database
        populateStatisticsWithExampleData();
        loadStatistics();
    }

    private void createAverageTemperatureChart()
    {
        // Generate header
        TextView label = new TextView(getApplicationContext());
        label.setText(R.string.statistics_labels_monthly_temp);
        label.setTextSize(20.0f);
        label.setTextColor(Color.BLACK);
        label.setTypeface(null, Typeface.BOLD);
        statisticsLayout.addView(label);

        // Inflate chart and insert data
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LineChart chart = (LineChart) inflater.inflate(R.layout.line_chart, statisticsLayout, false);

        statisticsLayout.addView(chart);

        ArrayList<Entry> entries = new ArrayList<>();

        String[] monthLabels = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sept", "oct", "nov", "dec"};
        int[] dataset = calculateAverageTemperaturePerMonth();

        // Get monthly shower data

        int i = 0;
        for (int data: dataset) {
            entries.add(new Entry(i, data));
            i++;
        }

        // Main chart properties
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.disableScroll();
        chart.getLegend().setTextSize(16);

        // Dataset and related properties
        LineDataSet dataSet = new LineDataSet(entries, "Average temperature");
        dataSet.setCircleRadius(6);
        dataSet.setValueTextSize(0);

        // Left axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return monthLabels[(int) value];
            }
        });

        // Right axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(12);
        leftAxis.setDrawGridLines(false);

        // Do the thing with the chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

    private void createAverageDurationChart()
    {
        // Generate label
        TextView label = new TextView(getApplicationContext());
        label.setText(R.string.statistics_labels_monthly_duration);
        label.setTextSize(20.0f);
        label.setTextColor(Color.BLACK);
        label.setTypeface(null, Typeface.BOLD);

        // Set label top margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        float top = 24.0f;
        float dpRatio = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixelsForDp = (int)(top * dpRatio);

        params.setMargins(0, pixelsForDp,0,0);
        label.setLayoutParams(params);

        statisticsLayout.addView(label);

        // Inflate chart and insert data
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LineChart chart = (LineChart) inflater.inflate(R.layout.line_chart, statisticsLayout, false);

        statisticsLayout.addView(chart);

        ArrayList<Entry> entries = new ArrayList<>();

        String[] monthLabels = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sept", "oct", "nov", "dec"};
        int[] dataset = calculateAverageDurationPerMonth();

        // Get monthly shower data

        int i = 0;
        for (int data: dataset) {
            entries.add(new Entry(i, data));
            i++;
        }

        // Main chart properties
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.disableScroll();
        chart.getLegend().setTextSize(16);

        // Dataset and related properties
        LineDataSet dataSet = new LineDataSet(entries, "Average duration");
        dataSet.setCircleRadius(6);
        dataSet.setValueTextSize(0);

        // Left axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return monthLabels[(int) value];
            }
        });

        // Right axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(12);
        leftAxis.setDrawGridLines(false);

        // Do the thing with the chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            String title = "";
            float currentValue = 0;
            float maxValue = 100;
            String unit = "";
            String message = "";
            int color = ContextCompat.getColor(getApplicationContext(), R.color.shower_blue300);

            switch(position)
            {
                case 0:
                    title = "Water usage";
                    currentValue = 45;
                    unit = "L";
                    message = "You have used 40 litres of water today. This is 30% less than your average usage.";
                    break;
                case 1:
                    title = "Total shower time";
                    currentValue = 14;
                    maxValue = 60;
                    unit = " mins";
                    color = Color.GREEN;
                    break;
                case 2:
                    title = "Cost of operation";
                    currentValue = 2.12f;
                    maxValue = 5;
                    unit = "$";
                    color = Color.YELLOW;
                    break;
            }
            return new GaugeFragment(title, currentValue, maxValue, unit, color, message);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public int[] calculateAverageTemperaturePerMonth()
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
        int[] monthAverages = new int[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateAverage(monthDividedStatistics[i]);
        }
        return monthAverages;
    }

    public int[] calculateAverageDurationPerMonth()
    {
        List<Integer>[] monthDividedStatistics = new List[12];
        for(int i = 0; i < 12; i++)
        {
            monthDividedStatistics[i] = new ArrayList<Integer>();
        }
        for (Statistics statistic: allStatistics) {
            int month = statistic.getMonth();
            monthDividedStatistics[month].add(statistic.duration);
        }
        int[] monthAverages = new int[12];
        for(int i = 0; i < 12; i++)
        {
            monthAverages[i] = calculateAverage(monthDividedStatistics[i]);
        }
        return monthAverages;
    }

    public int calculateAverage(List<Integer> values)
    {
        int sum = 0;
        for (Integer value: values) {
            sum += value;
        }
        return sum / values.size();
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

    public int calculateAverageTemperature()
    {
        int sum = 0;
        for(Statistics statistic: allStatistics)
        {
            sum += statistic.averageTemperature;
        }
        return sum / allStatistics.size();
    }

    // This method calls the creation of all graphs in the home activity
    public void showStatistics()
    {
        createAverageTemperatureChart();
        createAverageDurationChart();
    }

    // Database tasks
    private void loadStatistics() {
        class LoadStatisticsTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                // Adding to database
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                allStatistics = db.statisticsDao().getAll();

                return null;
            }

            @Override
            protected void onPostExecute(Void results)
            {
                showStatistics();
            }
        }

        LoadStatisticsTask task = new LoadStatisticsTask();
        task.execute();
    }
    // Class with methods to populate database with example shower data
    private void populateStatisticsWithExampleData() {
        class populateStatisticsTask extends AsyncTask<Void, Void, Void> {
            Random random;
            List<Statistics> exampleStatistics;
            @Override
            protected Void doInBackground(Void... voids) {
                random = new Random();

                exampleStatistics = generateExampleStatistics();

                // Adding to database
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.statisticsDao().deleteAll();
                db.statisticsDao().insertList(exampleStatistics);
                return null;
            }

            @Override
            protected void onPostExecute(Void results)
            {

            }

            private List<Statistics> generateExampleStatistics()
            {
                List<Statistics> exampleStatistics = new ArrayList<>();

                // Parameters
                final int PROBABILITY_SHOWER = 80;

                GregorianCalendar gc = new GregorianCalendar();
                gc.set(Calendar.YEAR, 2023);
                int days = gc.getActualMaximum(Calendar.DAY_OF_YEAR);

                for(int i = 0; i < days; i++)
                {
                    int probabilityShower = PROBABILITY_SHOWER;
                    Date date = generateDateTime(i);
                    int month = date.getMonth();
                    if(month > 4 && month < 9)
                    {
                        probabilityShower += 20;
                    }
                    while(random.nextInt(100) < probabilityShower)
                    {
                        int temperature = generateTemperature(month);
                        int flow = 100;
                        int duration = generateShowerDuration(month);
                        int energy = generateEnergyUsed(duration, temperature, flow);
                        int waterUsage = generateWaterUsage(duration, flow);

                        Statistics showerStatistics = new Statistics(generatePresetId(), duration, temperature, energy, waterUsage, date.getTime());
                        exampleStatistics.add(showerStatistics);
                        probabilityShower = probabilityShower / 2;
                    }
                }
                return exampleStatistics;
            }

            private int generateTemperature(int month)
            {
                int[] monthAverages = new int[]{37, 37, 35, 33, 28, 24, 22, 22, 24, 30, 35, 37};
                return monthAverages[month] += randBetween(-5, 5);
            }

            private int generateShowerDuration(int month)
            {
                int[] monthAverages = new int[]{460, 420, 300, 297, 240, 412, 678, 614, 235, 259, 590, 780};
                return monthAverages[month] += randBetween(-100, 100);
            }

            private int generateEnergyUsed(int duration, int temperature, int flowrate)
            {
                return duration * temperature * flowrate / 100;
            }

            private int generateWaterUsage(int duration, int flowRate)
            {
                return duration * flowRate / 1200;
            }

            private Date generateDateTime(int dayNumber)
            {
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(gc.YEAR, 2022 - 1900);
                // int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
                gc.set(gc.DAY_OF_YEAR, dayNumber);

                int year = gc.get(gc.YEAR);
                int month = gc.get(gc.MONTH);
                int day = gc.get(gc.DAY_OF_MONTH);
                int hours = randBetween(5, 22);
                int min = random.nextInt(60);
                int sec = random.nextInt(60);
                Date date = new Date(year, month, day, hours, min, sec);
                return date;
            }

            private int generatePresetId()
            {
                return 0;
            }

            private int randBetween(int start, int end) {
                return start + (int)Math.round(Math.random() * (end - start));
            }
        }

        populateStatisticsTask task = new populateStatisticsTask();
        task.execute();
    }
}