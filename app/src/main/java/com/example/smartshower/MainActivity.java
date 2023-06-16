package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Local variables
    ArrayList<UserPreset> userPresets;

    // Views
    RecyclerView presetListView;
    RecyclerView recommendedListView;
    Button btn_showStats;

    PresetAdapter presetAdapter;

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
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));

        // FIX ME: temporary preset list for development
        UserPreset preset1 = new UserPreset("Good morning", 25, 50, 100, 300, "cold");

        // Setting preset recycler view adapter and layout manager
        presetListView = (RecyclerView) findViewById(R.id.rv_home_presets);
        recommendedListView = (RecyclerView) findViewById(R.id.rv_home_recommended);


        List<UserPreset> recommendedPresets = new ArrayList<>();
        recommendedPresets.add(preset1);

        PresetAdapter recommendedAdapter = new PresetAdapter(recommendedPresets, new PresetClickListener() {
            @Override
            public void onItemClick(UserPreset preset) {
                startPresetShower(preset);
            }
        });

        recommendedListView.setAdapter(recommendedAdapter);
        recommendedListView.setLayoutManager(new LinearLayoutManager(this));

        btn_showStats = findViewById(R.id.btn_home_viewStatistics);

        loadUserPresets();

        btn_showStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void startPresetShower(UserPreset preset) {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        myIntent.putExtra("name", preset.name); //Optional parameters
        myIntent.putExtra("temperature", preset.temp); //Optional parameters
        myIntent.putExtra("tempLimit", preset.tempLimit); //Optional parameters
        myIntent.putExtra("flowRate", preset.flowRate); //Optional parameters
        myIntent.putExtra("timeLimit", preset.secondsLimit);
        MainActivity.this.startActivity(myIntent);
    }

    public void updatePresets(List<UserPreset> presets) {
        presetAdapter = new PresetAdapter(presets, this::startPresetShower);
        presetListView.setAdapter(presetAdapter);
        presetListView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Database tasks
    private void loadUserPresets() {
        @SuppressLint("StaticFieldLeak")
        class LoadUserPresetsTask extends AsyncTask<Void, Void, Void> {

            List<UserPreset> presets;

            @Override
            protected Void doInBackground(Void... voids) {

                // Adding to database
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                Log.i("JIRAF", "Hello");
                presets = db.userPresetDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                updatePresets(presets);
                Toast.makeText(getApplicationContext(), "Got presets", Toast.LENGTH_LONG).show();
            }
        }

        LoadUserPresetsTask task = new LoadUserPresetsTask();
        task.execute();
    }
}