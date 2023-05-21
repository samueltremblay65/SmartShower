package com.example.smartshower;

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

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class Shower extends AppCompatActivity {

    private int timerSeconds;
    private int currentTemp;
    private int currentFlow;
    private int maxTemp;

    // DOM element declaration
    private TextView timerDisplay;
    private TextView tempDisplay;
    private FloatingActionButton decreaseTemp;
    private FloatingActionButton increaseTemp;


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

        // Initializing settings variables
        timerSeconds = 500;
        currentFlow = flowRate;
        currentTemp = temp;
        maxTemp = tempLimit;

        // Initializing layout elements
        timerDisplay = (TextView) findViewById(R.id.shower_clock);
        tempDisplay = (TextView) findViewById(R.id.tv_showerTemp);
        increaseTemp = (FloatingActionButton) findViewById(R.id.shower_increaseTemp);
        decreaseTemp = (FloatingActionButton) findViewById(R.id.shower_decreaseTemp);

        // Add listeners
        decreaseTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTemp > 10)
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

        // Setting timer code
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                // Runs every 1000 ms
                timerSeconds--;

                // Update timer text
                timerDisplay.setText(formatTime(timerSeconds));
            }
        }, 0, 1000);
    }

    @SuppressLint("DefaultLocale")
    public String formatTime(int seconds)
    {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    @SuppressLint("DefaultLocale")
    private void updateTempDisplay()
    {
        tempDisplay.setText(String.format("%d°C", currentTemp));
    }
}