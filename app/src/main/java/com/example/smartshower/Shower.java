package com.example.smartshower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Shower extends ActivityWithHeader {

    public ShowerSession session;

    private int presetId;
    private int timerSeconds;
    private int currentTemp;
    private int currentFlow;
    private int maxTemp;

    private boolean isOn;

    // DOM element declaration
    private TextView timerDisplay;
    private TextView tempDisplay;
    private FloatingActionButton decreaseTemp;
    private FloatingActionButton increaseTemp;

    private TextView flowRateDisplay;

    private Slider flowRateSlider;

    private Button editPresetButton;
    private Button startShowerButton;

    private UserPreset preset;

    private FirebaseFirestore db;

    private UserAccount account;

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shower);
        super.setupUIElements();

        preset = (UserPreset) getIntent().getSerializableExtra("preset");

        db = FirebaseFirestore.getInstance();

        getUserAccountFromDatabase();
        
        createLiveChart();

        // Initializing settings variables
        presetId = preset.uid;
        timerSeconds = preset.secondsLimit;
        currentFlow = preset.flowRate;
        currentTemp = preset.temp;
        maxTemp = preset.tempLimit;

        // Initializing layout elements
        timerDisplay = findViewById(R.id.shower_clock);
        tempDisplay = findViewById(R.id.tv_showerTemp);
        increaseTemp = findViewById(R.id.shower_increaseTemp);
        decreaseTemp = findViewById(R.id.shower_decreaseTemp);
        flowRateSlider = findViewById(R.id.slider_showerFlow);
        flowRateDisplay = findViewById(R.id.tv_showerFlow);
        editPresetButton = findViewById(R.id.btn_shower_editPresets);
        startShowerButton = findViewById(R.id.btn_shower_start);

        // Layout element set up
        flowRateSlider.setValueFrom(0);
        flowRateSlider.setValueTo(100);
        flowRateSlider.setValue(currentFlow);

        // Initialize indicators to preset settings
        updateTempDisplay();
        updateFlowRateDisplay();

        if(timerSeconds == -1)
        {
            timerDisplay.setVisibility(View.INVISIBLE);
        }
        else
        {
            timerDisplay.setText(formatTime(timerSeconds));
        }

        // Setting system state
        isOn = false;

        // Add listeners
        decreaseTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTemp > 5)
                    currentTemp--;
                updateTempDisplay();
            }
        });

        increaseTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTemp < maxTemp)
                    currentTemp++;
                updateTempDisplay();
            }
        });

        flowRateSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                currentFlow = Math.round(value);
                updateFlowRateDisplay();
            }
        });

        startShowerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(isOn)
                {
                    stopShower();
                }
                else
                {
                    startShower();
                }
            }
        });

        editPresetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                stopShower();

                Intent intent = new Intent(Shower.this, CreatePreset.class);
                intent.putExtra("preset", preset);
                Shower.this.startActivity(intent);
            }
        });

        // Setting timer code
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                // Runs every 1000 ms
                if(isOn)
                {
                    if(timerSeconds != -1)
                    {
                        timerSeconds--;
                        if(timerSeconds == 0)
                        {
                            stopShower();
                            this.cancel();
                        }
                    }

                    // Update statistics
                    session.update(currentTemp, currentFlow);

                    LineData data = chart.getData();
                    Log.i("JirafiGraphs", "Size = " + data.getDataSets().size());
                    ILineDataSet targetSet = (LineDataSet)data.getDataSetByIndex(1);
                    ILineDataSet currentSet = (LineDataSet)data.getDataSetByIndex(0 );

                    targetSet.addEntry(new Entry(targetSet.getEntryCount(), currentTemp));
                    currentSet.addEntry(new Entry(currentSet.getEntryCount(), currentTemp - 2));
                    data.notifyDataChanged();

                    chart.getXAxis().setAxisMinimum(Math.max(0, targetSet.getEntryCount() - 25));
                    if(targetSet.getEntryCount() < 25)
                    {
                        chart.getXAxis().setAxisMaximum(30);
                    }
                    else
                    {
                        chart.getXAxis().setAxisMaximum(targetSet.getEntryCount() + 5);
                    }

                    // let the chart know it's data has changed
                    chart.notifyDataSetChanged();
                    chart.moveViewToX(targetSet.getEntryCount());

                    // Update timer text
                    timerDisplay.setText(formatTime(timerSeconds));
                }
            }
        }, 0, 1000);
    }
    public void createLiveChart()
    {
        ArrayList<Entry> targetEntries = new ArrayList<Entry>();

        ArrayList<Entry> currentEntries = new ArrayList<Entry>();

        chart = findViewById(R.id.shower_livechart);

        // Main chart properties
        chart.getLegend().setTextSize(16);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setTextSize(16);

        // Dataset and related properties
        LineData chartData = new LineData();

        LineDataSet targetDataset = new LineDataSet(targetEntries, "Target temperature (°C)");
        targetDataset.setCircleRadius(4);
        targetDataset.setValueTextSize(0);
        targetDataset.setColor(getResources().getColor(R.color.shower_blue300));
        targetDataset.setCircleColor(getResources().getColor(R.color.shower_blue300));

        LineDataSet currentDataset = new LineDataSet(currentEntries, "Current temperature (°C)");
        currentDataset.setCircleRadius(4);
        currentDataset.setValueTextSize(0);
        currentDataset.setColor(getResources().getColor(R.color.light_red));
        currentDataset.setCircleColor(getResources().getColor(R.color.light_red));

        chartData.addDataSet(targetDataset);
        chartData.addDataSet(currentDataset);

        // Left axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(15f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);

        // Right axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(12);
        leftAxis.setAxisMinimum(currentTemp - 5);
        leftAxis.setAxisMaximum(40);
        leftAxis.setDrawGridLines(true);

        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(30);

        chart.setData(chartData);
        chart.invalidate(); // refresh
    }

    @SuppressLint("DefaultLocale")
    public String formatTime(int seconds)
    {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private void startShower()
    {
        isOn = true;
        session = new ShowerSession(presetId);

        startShowerButton.setBackgroundColor(getResources().getColor(R.color.red));
        startShowerButton.setText(getResources().getText(R.string.stop_shower));
    }

    private void stopShower()
    {
        if(isOn)
        {
            saveStatistics();
        }
        
        isOn = false;
        
        // Should save shower session to database
        startShowerButton.setBackgroundColor(getResources().getColor(R.color.shower_blue300));
        startShowerButton.setText(getResources().getText(R.string.start_shower));
    }

    @SuppressLint("DefaultLocale")
    private void updateTempDisplay()
    {
        tempDisplay.setText(String.format("%d°C", currentTemp));
    }

    private void updateTimerDisplay()
    {
        if(timerSeconds == getResources().getInteger(R.integer.null_timelimit_db_value))
        {
            return;
        }

        timerDisplay.setText(formatTime(timerSeconds));
    }

    @SuppressLint("DefaultLocale")
    private void updateFlowRateDisplay() { flowRateDisplay.setText(String.format("water flow: %d%%", currentFlow));}


    // Database tasks
    private void saveStatistics() {
        @SuppressLint("StaticFieldLeak")
        class SaveStatisticsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                if(session.showerData.isEmpty())
                {
                    return null;
                }

                // Adding to database
                AppDatabase appDb = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                Statistics statistics = new Statistics(session);

                appDb.statisticsDao().insertAll(statistics);

                DocumentReference docRef = db.collection("statistics").document(Integer.toString(account.getUserId()));

                HashMap<String, Object> statsMap = new HashMap<String, Object>();
                statsMap.put(Long.toString(session.getDateTime()), statistics);
                docRef.update(statsMap);
                return null;
            }
        }

        SaveStatisticsTask task = new SaveStatisticsTask();
        task.execute();
    }

    private void getUserAccountFromDatabase()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        String username = preferences.getString(getString(R.string.keys_account_username), "");

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                account = documentSnapshot.toObject(UserAccount.class);

                if(account == null)
                {
                    throw new IllegalStateException("Could not find user account");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}