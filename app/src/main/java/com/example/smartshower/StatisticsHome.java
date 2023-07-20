package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class StatisticsHome extends AppCompatActivity {

    List<Statistics> allStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_home);

        // Changing system bar color
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));

        loadStatistics();

        createAverageTemperatureChart();
    }

    private void createAverageTemperatureChart()
    {
        // Generate graph
        LineChart chart = (LineChart) findViewById(R.id.chart);
        ArrayList<Entry> entries = new ArrayList<>();

        String[] monthLabels = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sept", "oct", "nov", "dec"};
        int[] dataset = new int[]{38, 39, 38, 37, 32, 26, 24, 26, 28, 33, 37, 37};

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
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setCircleRadius(6);
        dataSet.setValueTextSize(0);
        dataSet.setLabel("Average temperature");

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

            }
        }

        LoadStatisticsTask task = new LoadStatisticsTask();
        task.execute();
    }
}