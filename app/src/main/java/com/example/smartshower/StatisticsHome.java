package com.example.smartshower;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StatisticsHome extends ActivityWithHeader {

    private LinearLayout statisticsLayout;

    private ViewPager2 gaugePager;
    private FragmentStateAdapter gaugePagerAdapter;

    private UserAccount account;
    private FirebaseFirestore db;
    Context context;

    StatisticsCompiler statisticsCompiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_home);
        super.setupUIElements();

        removeBottomMargin();

        context = getApplicationContext();

        db = FirebaseFirestore.getInstance();
        getDataFromDatabase();

        // Set the header text in the parent class
        this.setHeader("Statistics");

        statisticsLayout = findViewById(R.id.statistics_home_ll);

        // View pager setup
        gaugePager = findViewById(R.id.vp_stats_gauges);

        // Use following line to generate a year's worth of example shower data in the database
        // populateStatisticsWithExampleData();
    }

    private void getDataFromDatabase() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        String username = preferences.getString(getString(R.string.keys_account_username), "");

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                account = documentSnapshot.toObject(UserAccount.class);

                if (account == null) {
                    throw new IllegalStateException("Could not find user account");
                }

                //populateStatisticsWithExampleData();

                loadStatistics();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
        DataPoint<Integer>[] dataset = statisticsCompiler.calculateAverageTemperaturePerMonth();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int currentMonth = calendar.get(Calendar.MONTH);
        String[] currentMonthLabels = new String[12];
        ArrayList<DataPoint<Integer>> currentMonthSortedData = new ArrayList<>();

        int minTemperature = 16;

        for(int i = 0; i < 12; i++)
        {
            currentMonthLabels[i] = monthLabels[(i + currentMonth + 1) % 12];
            currentMonthSortedData.add(dataset[(i + currentMonth + 1) % 12]);
            if(dataset[i].isValid && dataset[i].value < minTemperature)
                minTemperature = dataset[i].value;
        }

        int i = 1;
        for (DataPoint<Integer> data: currentMonthSortedData) {
            int currentX = i;
            if(data.isValid)
            {
                entries.add(new Entry(currentX, data.value));
            }
            i++;
        }

        // Main chart properties
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setTextSize(16);

        // Dataset and related properties
        LineDataSet dataSet = new LineDataSet(entries, "Average temperature (°C)");
        dataSet.setCircleRadius(6);
        dataSet.setValueTextSize(0);

        // Left axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(13f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value == 0 || value > 12)
                {
                    return "";
                }
                return currentMonthLabels[(int) value - 1];
            }
        });

        // Right axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(minTemperature);
        leftAxis.setTextSize(12);
        leftAxis.setDrawGridLines(true);

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

            switch (position) {
                case 0:
                    title = "Water usage";
                    currentValue = statisticsCompiler.todayWaterUsage;
                    unit = "L";
                    maxValue = 100;
                    message = String.format("You have used %.1f litres of water today. This is %d %% less than your average usage.", currentValue, 30);
                    break;
                case 1:
                    title = "Total shower time";
                    currentValue = statisticsCompiler.todayTotalDuration / 60;
                    maxValue = 60;
                    unit = " mins";
                    color = Color.GREEN;
                    message = String.format("You have spent %.0f minutes in the shower today. This is %d%% less than your average usage.", currentValue, 30);
                    break;
                case 2:
                    title = "Average shower duration";
                    currentValue = statisticsCompiler.todayAverageDuration / 60;
                    maxValue = 60;
                    unit = " mins";
                    color = Color.GREEN;
                    message = String.format("Your average shower duration today was %.0f minutes. This is %d%% less than your average usage.", currentValue, 30);
                    break;
                case 3:
                    title = "Average water temperature";
                    currentValue = statisticsCompiler.todayAverageTemperature;
                    maxValue = 60;
                    unit = "°C";
                    color = context.getColor(R.color.light_red);
                    message = String.format("Your average shower water temperature was %.0f°C today. This is %d%% less than your average usage.", currentValue, 30);
                    break;
            }
            return new GaugeFragment(title, currentValue, maxValue, unit, color, message);
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    // This method calls the creation of all graphs in the home activity
    public void showStatistics()
    {
        // Showing gauges
        gaugePagerAdapter = new StatisticsHome.ScreenSlidePagerAdapter(this);
        gaugePager.setAdapter(gaugePagerAdapter);

        createAverageTemperatureChart();
        // createAverageDurationChart();
    }

    // Database tasks
    private void loadStatistics() {
        class LoadStatisticsTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                // AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                // allStatistics = db.statisticsDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void results)
            {
                // ShowStatistics
            }
        }

        DocumentReference docRef = db.collection("statistics").document(Integer.toString(account.getUserId()));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String, Object> data = (HashMap<String, Object>) documentSnapshot.getData();
                List<Statistics> allStatistics = new ArrayList<Statistics>();

                if(data.containsKey("accountCreationDate"))
                    data.remove("accountCreationDate");

                for (Object value : data.values()) {
                    HashMap<String, Object> statisticsObject = (HashMap<String, Object>) value;

                    int presetId = (int)(long) statisticsObject.get("presetId");
                    int duration = (int)(long) statisticsObject.get("duration");
                    int averageTemperature = (int)(long) statisticsObject.get("averageTemperature");
                    float waterUsage = (float)(double) statisticsObject.get("waterUsage");
                    long dateTime = (long) statisticsObject.get("dateTime");

                    Statistics statistic = new Statistics(presetId, duration, averageTemperature, 0, waterUsage, dateTime);
                    allStatistics.add(statistic);
                }

                statisticsCompiler = new StatisticsCompiler(allStatistics);
                setupHistoryButton();
                showStatistics();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        
        // LoadStatisticsTask task = new LoadStatisticsTask();
        // task.execute();
    }

    private void setupHistoryButton()
    {
        Button historyButton = findViewById(R.id.btn_goto_history);

        ArrayList<Statistics> history = statisticsCompiler.getLatestShowerStatistics(10);

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsHome.this, ShowerHistory.class);
            intent.putExtra("history", history);
            StatisticsHome.this.startActivity(intent);
        });

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

                DocumentReference docRef = db.collection("statistics").document(Integer.toString(account.getUserId()));

                HashMap<String, Object> statsMap = new HashMap<String, Object>();
                
                for(Statistics statistic: exampleStatistics)
                {
                    statsMap.put(Long.toString(statistic.dateTime), statistic);
                }
                
                docRef.update(statsMap);

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

                int dayNumber = 56; // Day number of first day of data
                int year = 2022;

                // End date
                Date currentDate = generateDateTime(getCurrentDayNumber(), getCurrentYear());

                // Find days in current year
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(Calendar.YEAR, year);
                int daysInYear = gc.getActualMaximum(Calendar.DAY_OF_YEAR);

                Date date;

                do
                {
                    if(++dayNumber == daysInYear - 1)
                    {
                        year++;
                        dayNumber = 1;
                    }

                    date = generateDateTime(dayNumber, year);

                    int probabilityShower = PROBABILITY_SHOWER;
                    int month = date.getMonth();
                    if(month > 4 && month < 9)
                    {
                        probabilityShower += 20;
                    }

                    while(random.nextInt(100) < probabilityShower)
                    {
                        int temperature = generateTemperature(month);
                        int flow = generateAverageFlowrate(month);
                        int duration = generateShowerDuration(month);
                        int energy = generateEnergyUsed(duration, temperature, flow);
                        int waterUsage = generateWaterUsage(duration, flow);

                        Statistics showerStatistics = new Statistics(generatePresetId(), duration, temperature, flow, waterUsage, date.getTime());
                        exampleStatistics.add(showerStatistics);
                        probabilityShower = probabilityShower / 2;
                    }

                } while(!isSameDay(date, currentDate));

                return exampleStatistics;
            }

            private boolean isSameDay(Date date1, Date date2)
            {
                boolean result = true;
                if(date1.getYear() != date2.getYear())
                {
                    result = false;
                }

                if(date1.getMonth() != date2.getMonth())
                {
                    result = false;
                }

                if(date1.getDate() != date2.getDate())
                {
                    result = false;
                }

                return result;
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

            private int generateAverageFlowrate(int month)
            {
                return randBetween(25, 100);
            }

            private int generateEnergyUsed(int duration, int temperature, int flowrate)
            {
                return duration * temperature * flowrate / 100;
            }

            private int generateWaterUsage(int duration, int flowRate)
            {
                return duration * flowRate / 1200;
            }

            private Date generateDateTime(int dayNumber, int year)
            {
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(gc.YEAR, year - 1900);

                // int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

                gc.set(gc.DAY_OF_YEAR, dayNumber);

                int month = gc.get(gc.MONTH);
                int day = gc.get(gc.DAY_OF_MONTH);
                int hours = randBetween(5, 22);
                int min = random.nextInt(60);
                int sec = random.nextInt(60);
                Date date = new Date(year - 1900, month, day, hours, min, sec);
                return date;
            }

            private int getCurrentDayNumber()
            {
                GregorianCalendar gc = new GregorianCalendar();
                return gc.get(gc.DAY_OF_YEAR);
            }

            private int getCurrentYear()
            {
                GregorianCalendar gc = new GregorianCalendar();
                return gc.get(Calendar.YEAR);
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