package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

                // Display statistics
                ((TextView) findViewById(R.id.tv_showers_monthly)).setText(Integer.toString(allStatistics.size()));
                ((TextView) findViewById(R.id.tv_average_duration)).setText(Integer.toString(calculateAverageDuration()));
                ((TextView) findViewById(R.id.tv_average_temperature)).setText(Integer.toString(calculateAverageTemperature()));
            }
        }

        LoadStatisticsTask task = new LoadStatisticsTask();
        task.execute();
    }
}