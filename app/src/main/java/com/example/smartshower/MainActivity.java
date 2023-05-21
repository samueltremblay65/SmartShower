package com.example.smartshower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PresetViewModel presetViewModel;

    // Views
    RecyclerView presetListView;
    Button btn_showStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Changing system bar color
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.shower_blue300));

        // Getting view model object. DO NOT create new instance directly
        // Lifecycle of view model should extend past activity lifecycle
        presetViewModel = new ViewModelProvider(this).get(PresetViewModel.class);

        // Create the observer which updates the UI.
        final Observer<List<UserPreset>> presetObserver = new Observer<List<UserPreset>>() {
            @Override
            public void onChanged(@Nullable final List<UserPreset> newPresets) {
                Log.i("OBSERVER","Detected change in database");
            }
        };

        // FIX ME: temporary preset list for development
        UserPreset preset1 = new UserPreset("Good morning", 25, 50, 100, "cold");
        UserPreset preset2 = new UserPreset("Good evening", 38, 60, 80, "cold");
        UserPreset preset3 = new UserPreset("Ice cold", 10, 25, 100, "cold");
        UserPreset preset4 = new UserPreset("Soothe", 38, 60, 100, "cold");
        UserPreset preset5 = new UserPreset("Kids", 35, 37, 90, "cold");

        ArrayList<UserPreset> presetList = new ArrayList<UserPreset>();
        presetList.add(preset1);
        presetList.add(preset2);
        presetList.add(preset3);
        presetList.add(preset4);
        presetList.add(preset5);

        // Setting preset recycler view adapter and layout manager
        presetListView = (RecyclerView) findViewById(R.id.rv_home_presets);
        PresetAdapter presetAdapter = new PresetAdapter(presetList, new PresetClickListener() {
            @Override
            public void onItemClick(UserPreset preset) {
                startPresetShower(preset);
            }
        });
        presetListView.setAdapter(presetAdapter);
        presetListView.setLayoutManager(new LinearLayoutManager(this));

        btn_showStats = (Button) findViewById(R.id.btn_home_viewStatistics);

    }

    public void startPresetShower(UserPreset preset)
    {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        myIntent.putExtra("name", preset.name); //Optional parameters
        myIntent.putExtra("temperature", preset.temp); //Optional parameters
        myIntent.putExtra("tempLimit", preset.tempLimit); //Optional parameters
        myIntent.putExtra("flowRate", preset.flowRate); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
}