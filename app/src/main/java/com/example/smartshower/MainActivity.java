package com.example.smartshower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PresetViewModel presetViewModel;

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

        presetViewModel.deleteAll();
    }
}