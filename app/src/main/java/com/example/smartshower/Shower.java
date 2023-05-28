package com.example.smartshower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class Shower extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shower);

        // Changing system bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.shower_blue300));

        // Get extra variables
        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        int temp = intent.getIntExtra("temperature", 30);
        int tempLimit = intent.getIntExtra("tempLimit", 50);
        int flowRate = intent.getIntExtra("flowRate", 100);
        int timeLimit = intent.getIntExtra("timeLimit", 10);

        // Initializing settings variables
        timerSeconds = timeLimit;
        currentFlow = flowRate;
        currentTemp = temp;
        maxTemp = tempLimit;

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
        flowRateSlider.setValue(flowRate);

        // Initialize indicators to preset settings
        updateTempDisplay();
        updateFlowRateDisplay();
        timerDisplay.setText(formatTime(timerSeconds));

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

        // Setting timer code
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                // Runs every 1000 ms
                if(isOn)
                {
                    timerSeconds--;

                    // Update timer text
                    timerDisplay.setText(formatTime(timerSeconds));
                }

                if(timerSeconds <= 0)
                {
                    stopShower();
                    this.cancel();
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
        startShowerButton.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void stopShower()
    {
        isOn = false;
        startShowerButton.setBackgroundColor(getResources().getColor(R.color.shower_blue300));
    }

    @SuppressLint("DefaultLocale")
    private void updateTempDisplay()
    {
        tempDisplay.setText(String.format("%d°C", currentTemp));
    }

    @SuppressLint("DefaultLocale")
    private void updateFlowRateDisplay() { flowRateDisplay.setText(String.format("water flow: %d%%", currentFlow));}
}