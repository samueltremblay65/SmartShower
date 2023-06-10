package com.example.smartshower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // Views
    RecyclerView presetListView;
    RecyclerView recommendedListView;
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
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));

        // Create the observer which updates the UI.
        final Observer<List<UserPreset>> presetObserver = new Observer<List<UserPreset>>() {
            @Override
            public void onChanged(@Nullable final List<UserPreset> newPresets) {
                Log.i("OBSERVER", "Detected change in database");
            }
        };

        // FIX ME: temporary preset list for development
        UserPreset preset1 = new UserPreset("Good morning", 25, 50, 100, 300, "cold");
        UserPreset preset2 = new UserPreset("Good evening", 38, 60, 80, 120, "cold");
        UserPreset preset3 = new UserPreset("Ice cold", 10, 25, 100, 300, "cold");
        UserPreset preset4 = new UserPreset("Soothe", 38, 60, 100, 60, "cold");
        UserPreset preset5 = new UserPreset("Kids", 35, 37, 90, 900, "cold");

        ArrayList<UserPreset> presetList = new ArrayList<UserPreset>();
        presetList.add(preset1);
        presetList.add(preset2);
        presetList.add(preset3);
        presetList.add(preset4);
        presetList.add(preset5);

        // Setting preset recycler view adapter and layout manager
        presetListView = (RecyclerView) findViewById(R.id.rv_home_presets);
        recommendedListView = (RecyclerView) findViewById(R.id.rv_home_recommended);
        PresetAdapter presetAdapter = new PresetAdapter(presetList, new PresetClickListener() {
            @Override
            public void onItemClick(UserPreset preset) {
                startPresetShower(preset);
            }
        });

        List<UserPreset> recommendedPresets = new ArrayList<>();
        recommendedPresets.add(preset1);

        PresetAdapter recommendedAdapter = new PresetAdapter(recommendedPresets, new PresetClickListener() {
            @Override
            public void onItemClick(UserPreset preset) {
                startPresetShower(preset);
            }
        });

        presetListView.setAdapter(presetAdapter);
        presetListView.setLayoutManager(new LinearLayoutManager(this));

        recommendedListView.setAdapter(recommendedAdapter);
        recommendedListView.setLayoutManager(new LinearLayoutManager(this));

        btn_showStats = findViewById(R.id.btn_home_viewStatistics);

        btn_showStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

    }

    public void startPresetShower(UserPreset preset)
    {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        myIntent.putExtra("name", preset.name); //Optional parameters
        myIntent.putExtra("temperature", preset.temp); //Optional parameters
        myIntent.putExtra("tempLimit", preset.tempLimit); //Optional parameters
        myIntent.putExtra("flowRate", preset.flowRate); //Optional parameters
        myIntent.putExtra("timeLimit", preset.secondsLimit);
        MainActivity.this.startActivity(myIntent);
    }

    private void saveTask() {
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                Task task = new Task();
                task.setTask("New task");
                task.setDesc("Random thing to do");
                task.setFinishBy("Tomorrow");
                task.setFinished(false);

                // Creating presets
                UserPreset preset1 = new UserPreset("Good morning", 25, 50, 100, 300, "cold");
                UserPreset preset2 = new UserPreset("Good evening", 38, 60, 80, 120, "cold");
                UserPreset preset3 = new UserPreset("Ice cold", 10, 25, 100, 300, "cold");
                UserPreset preset4 = new UserPreset("Soothe", 38, 60, 100, 60, "cold");
                UserPreset preset5 = new UserPreset("Kids", 35, 37, 90, 900, "cold");

                // Adding to database
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.taskDao().insert(task);
                db.userPresetDao().insertAll(preset1, preset2, preset3, preset4, preset5);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }
}