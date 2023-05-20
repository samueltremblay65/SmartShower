package com.example.smartshower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        PresetAdapter presetAdapter = new PresetAdapter(presetList);
        presetListView.setAdapter(presetAdapter);
        presetListView.setLayoutManager(new LinearLayoutManager(this));

        btn_showStats = (Button) findViewById(R.id.btn_home_viewStatistics);

    }

    public void showPreset_onClick(View view)
    {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        // myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
}