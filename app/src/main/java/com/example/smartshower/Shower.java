package com.example.smartshower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shower);
        super.setupUIElements();

        preset = (UserPreset) getIntent().getSerializableExtra("preset");

        db = FirebaseFirestore.getInstance();

        getUserAccountFromDatabase();

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

                    // Update timer text
                    timerDisplay.setText(formatTime(timerSeconds));
                }
            }
        }, 0, 1000);
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