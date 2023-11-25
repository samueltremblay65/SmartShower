package com.example.smartshower;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Shower extends ActivityWithHeader {

    public ShowerSession session;
    
    RequestQueue requestQueue;
    
    Timer timer;

    private int presetId;
    private int timerSeconds;
    private int currentTemperature;
    private int targetTemperature;
    private int currentFlow;
    private int targetFlow;
    private int maxTemp;

    private boolean isOn;

    private boolean inputSequenced;
    private boolean flowSequenced = false;
    private boolean isEditable = false;

    private int sequenceInstant = 0;
    private int cooldown = 0;
    private int maxTargetTemp;

    // DOM element declaration
    private TextView timerDisplay;
    private TextView tempDisplay;
    private FloatingActionButton decreaseTemp;
    private FloatingActionButton increaseTemp;

    private Slider flowRateSlider;

    private Button editPresetButton;
    private Button startShowerButton;

    private UserPreset preset;

    private FirebaseFirestore db;

    private UserAccount account;

    private LineChart chart;

    private String showerAddress;

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shower);
        super.setupUIElements();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        showerAddress = preferences.getString("showerAddress", "");

        if(showerAddress.isEmpty())
        {
            showerAddress = "https://smartshowermock.onrender.com";
        }
        else
        {
            showerAddress = String.format("http://%s", showerAddress);
            Log.i("AddressJiraf", showerAddress);
        }

        preset = (UserPreset) getIntent().getSerializableExtra("preset");

        inputSequenced = false;

        if(preset.inputSequenceName != null && !preset.inputSequenceName.isEmpty() && !preset.inputSequenceName.equals("none"))
        {
            inputSequenced = true;
            if(preset.inputSequenceName.equals("flow"))
            {
                flowSequenced = true;
                inputSequenced = false;
            }
        }

        isEditable = getIntent().getBooleanExtra("isEditable", false);

        db = FirebaseFirestore.getInstance();

        getUserAccountFromDatabase();
        
        createLiveChart();

        // Initializing settings variables
        presetId = preset.uid;
        timerSeconds = preset.secondsLimit;
        targetTemperature = preset.temp;
        maxTargetTemp = preset.temp;
        targetFlow = preset.flowRate;
        currentFlow = preset.flowRate;
        currentTemperature = 20;
        maxTemp = preset.tempLimit;

        // Initialize volley queue
        requestQueue = Volley.newRequestQueue(this);

        // Initializing layout elements
        timerDisplay = findViewById(R.id.shower_clock);
        tempDisplay = findViewById(R.id.tv_showerTemp);
        increaseTemp = findViewById(R.id.shower_increaseTemp);
        decreaseTemp = findViewById(R.id.shower_decreaseTemp);
        flowRateSlider = findViewById(R.id.slider_showerFlow);
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

        if(!isEditable)
        {
            editPresetButton.setVisibility(View.INVISIBLE);
        }

        // Add listeners
        decreaseTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(targetTemperature > 5)
                    targetTemperature--;

                cooldown = 3;

                setShowerTemperature();
                updateTempDisplay();
            }
        });

        increaseTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(targetTemperature < maxTemp)
                    targetTemperature++;
                
                cooldown = 3;
                
                setShowerTemperature();
                updateTempDisplay();
            }
        });

        flowRateSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                currentFlow = Math.round(value);

                cooldown = 3;

                setShowerFlow();
                updateFlowRateDisplay();
            }
        });

        flowRateSlider.setLabelFormatter(value -> String.format(Locale.CANADA, "%.0f%%", value));

        startShowerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                String url2 = String.format("%s/on", showerAddress);

                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                        response -> {
                            Log.i("ConnectionJiraf", "Got a response");
                            Log.i("HttpJiraf", response);
                            stopShower();
                        }, error -> {
                            Log.i("HttpJiraf", "Unable to connect to the shower. Check your internet connection and try again");
                            if(error.getMessage() != null)
                            {
                                Log.i("HttpJiraf", error.getMessage());
                            }
                        });
                
                requestQueue.add(stringRequest2);

                if(isOn)
                {
                    stopShower();
                }
                else
                {
                    String url = "https://smartshowermock.onrender.com/on";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            response -> {
                                Log.i("HttpJiraf", response);
                                startShower();

                            }, error -> Log.i("HttpJiraf", "Unable to connect to the shower. Check your internet connection and try again"));

                    requestQueue.add(stringRequest);
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

        setShowerTemperature();

        // Setting timer code
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                // Runs every 1000 ms
                String url = "https://smartshowermock.onrender.com/get";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        response -> {
                            handleGetStatusRequest(response);
                        }, error -> { Log.i("JirafError", "Error"); });

                requestQueue.add(jsonObjectRequest);

                if(cooldown > 0)
                    cooldown--;

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
                    
                    // Input sequencing
                    if(inputSequenced)
                    {
                        targetTemperature = ShowerSequencer.getTemperatureInstant(preset.inputSequenceName, sequenceInstant++);
                        setShowerTemperature();
                    }
                    if(flowSequenced)
                    {
                        targetFlow = ShowerSequencer.getTemperatureInstant(preset.inputSequenceName, sequenceInstant++);
                        setShowerFlow();
                    }

                    // Update statistics
                    session.update(currentTemperature, currentFlow);

                    runOnUiThread(() -> {
                        LineData data = chart.getData();
                        ILineDataSet targetSet = (LineDataSet)data.getDataSetByIndex(0);
                        ILineDataSet currentSet = (LineDataSet)data.getDataSetByIndex(1);

                        if(flowSequenced)
                        {
                            targetSet.addEntry(new Entry(targetSet.getEntryCount(), targetFlow));
                            currentSet.addEntry(new Entry(currentSet.getEntryCount(), currentFlow));
                            data.notifyDataChanged();
                        }
                        else
                        {
                            targetSet.addEntry(new Entry(targetSet.getEntryCount(), targetTemperature));
                            currentSet.addEntry(new Entry(currentSet.getEntryCount(), currentTemperature));
                            data.notifyDataChanged();

                            if(targetTemperature > maxTargetTemp)
                            {
                                maxTargetTemp = targetTemperature;
                            }
                            chart.getAxisLeft().setAxisMaximum(Math.max(40, Math.max(session.getMaximalTemperature(), maxTargetTemp )+ 5));
                        }

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

                        if(session.showHealthWarning(currentTemperature))
                        {
                            TextView healthWarning = findViewById(R.id.warning_textbox);
                            healthWarning.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            TextView healthWarning = findViewById(R.id.warning_textbox);
                            healthWarning.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }, 1000, 1000);
    }
    public void createLiveChart()
    {
        ArrayList<Entry> targetEntries = new ArrayList<Entry>();

        ArrayList<Entry> currentEntries = new ArrayList<Entry>();

        chart = findViewById(R.id.shower_livechart);

        // Main chart properties
        chart.getLegend().setTextSize(16);
        chart.getLegend().setWordWrapEnabled(true);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setTextSize(16);

        // Dataset and related properties
        LineData chartData = new LineData();

        LineDataSet targetDataset = new LineDataSet(targetEntries, "Target temperature (°C)");
        
        if(flowSequenced)
        {
            targetDataset.setLabel("Target flow (%)");
        }

        targetDataset.setCircleRadius(4);
        targetDataset.setValueTextSize(0);
        targetDataset.setColor(getResources().getColor(R.color.shower_blue300));
        targetDataset.setCircleColor(getResources().getColor(R.color.shower_blue300));

        LineDataSet currentDataset = new LineDataSet(currentEntries, "Current temperature (°C)");

        if(flowSequenced)
        {
            currentDataset.setLabel("Current flow (%)");
        }

        currentDataset.setCircleRadius(4);
        currentDataset.setValueTextSize(0);
        currentDataset.setColor(getResources().getColor(R.color.yellow));
        currentDataset.setCircleColor(getResources().getColor(R.color.yellow));

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
        leftAxis.setAxisMinimum(5);
        leftAxis.setAxisMaximum(40);
        leftAxis.setDrawGridLines(true);

        if(flowSequenced)
        {
            leftAxis.setAxisMinimum(0);
            leftAxis.setAxisMaximum(100);
        }

        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(30);

        chart.setData(chartData);
        chart.invalidate(); // refresh
    }

    public void setShowerTemperature()
    {
        String url = String.format("https://smartshowermock.onrender.com/set?temp=%d", targetTemperature);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("ShowerTemperature", String.format("Shower temperature set to %d", targetTemperature));
                }, error -> Log.i("HttpJiraf", "Error sending shower input signal"));

        requestQueue.add(stringRequest);
    }

    public void setShowerFlow()
    {
        String url = String.format("https://smartshowermock.onrender.com/set?flow=%d", targetFlow);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("ShowerTemperature", String.format("Shower temperature set to %d", targetFlow));
                }, error -> Log.i("HttpJiraf", "Error sending shower input signal"));

        requestQueue.add(stringRequest);
    }

    @SuppressLint("DefaultLocale")
    public String formatTime(int seconds)
    {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private void startShower()
    {
        cooldown = 3;
        isOn = true;
        session = new ShowerSession(presetId);

        startShowerButton.setBackgroundColor(getResources().getColor(R.color.red));
        startShowerButton.setText(getResources().getText(R.string.stop_shower));
    }

    private void stopShower()
    {
        String url = "https://smartshowermock.onrender.com/off";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("HttpJiraf", response);
                    if(isOn)
                    {
                        saveStatistics();
                    }

                    isOn = false;
                    sequenceInstant = 0;

                    // Should save shower session to database
                    startShowerButton.setBackgroundColor(getResources().getColor(R.color.shower_blue300));
                    startShowerButton.setText(getResources().getText(R.string.start_shower));
                }, error -> Log.i("HttpJiraf", "Unable to connect to the shower. Check your internet connection and try again"));

        requestQueue.add(stringRequest);
    }

    @SuppressLint("DefaultLocale")
    private void updateTempDisplay()
    {
        tempDisplay.setText(String.format("%d°C", targetTemperature));
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
    private void updateFlowRateDisplay() {
        flowRateSlider.setValue(targetFlow);
    }

    private void handleGetStatusRequest(JSONObject response)
    {
        try{
            String responseStatus = response.getString("status");

            if(isOn && cooldown == 0) {
                targetTemperature = response.getInt("targetTemperature");
                targetFlow = response.getInt("targetFlow");
            }

            if(isOn)
            {
                currentTemperature = response.getInt("currentTemperature");
                currentFlow = response.getInt("currentFlow");

                updateTempDisplay();
                updateFlowRateDisplay();
            }

            if(!isOn && responseStatus.equals("on"))
            {
                startShower();
            }

            if(isOn && responseStatus.equals("off"))
            {
                stopShower();
            }

        } catch(JSONException e)
        {
            Log.i("JSON Parsing", e.getMessage());
        }
    }

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