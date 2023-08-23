package com.example.smartshower;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ActivityWithHeader {

    // Local variables
    ArrayList<UserPreset> userPresets;

    // Views
    RecyclerView presetListView;
    Button showStatsButton;
    Button addPresetButton;

    Button managePresetButton;
    PresetAdapter presetAdapter;

    // View pager setup
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.setupUIElements();

        // Setting preset recycler view adapter and layout manager
        presetListView = (RecyclerView) findViewById(R.id.rv_home_presets);

        // View pager setup
        viewPager = findViewById(R.id.sp_home_recommended);

        // Sets margins when scrolling between pages of recommended view pager items
        viewPager.setPageTransformer(new MarginPageTransformer(30));

        showStatsButton = findViewById(R.id.btn_home_viewStatistics);
        addPresetButton = findViewById(R.id.btn_home_add_preset);
        managePresetButton = findViewById(R.id.btn_home_manage_presets);

        // PopulateDatabase can be used to load some generic sample data in the preset table
        // populateDatabase();
        // deleteAllPresetsFromDatabase();

        loadUserPresets();
        loadRecommendedPresets();

        showStatsButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, StatisticsHome.class);
            MainActivity.this.startActivity(myIntent);
        });

        addPresetButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreatePreset.class);
            MainActivity.this.startActivity(myIntent);
        });

        managePresetButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Clicked on manage presets", Toast.LENGTH_SHORT).show();
        });
    }

    public void startPresetShower(UserPreset preset) {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        myIntent.putExtra("presetId", preset.uid); //Optional parameters
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

        // Code for reordering items
        ItemTouchHelper.SimpleCallback reorderCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(presets, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                presets.remove(viewHolder.getAdapterPosition());
                presetAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(reorderCallback);
        itemTouchHelper.attachToRecyclerView(presetListView);
    }

    public void updateRecommended(List<UserPreset> presets) {
        pagerAdapter = new MainActivity.ScreenSlidePagerAdapter(this, presets);
        viewPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        SmartRecommendationCreator recommendationCreator;
        List<UserPreset> presets;

        public ScreenSlidePagerAdapter(FragmentActivity fa, List<UserPreset> presets) {
            super(fa);
            this.presets = presets;
        }

        @Override
        public Fragment createFragment(int position) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPresetShower(presets.get(position));
                }
            };
            return new RecommendedSliderFragment(presets.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return presets.size();
        }
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
                presets = db.userPresetDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                updatePresets(presets);
            }
        }

        LoadUserPresetsTask task = new LoadUserPresetsTask();
        task.execute();
    }

    private void loadRecommendedPresets() {
        @SuppressLint("StaticFieldLeak")
        class LoadRecommendedPresetsTask extends AsyncTask<Void, Void, Void> {

            List<UserPreset> presets;

            @Override
            protected Void doInBackground(Void... voids) {

                // Adding to database
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                presets = db.userPresetDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                updateRecommended(presets);
            }
        }

        LoadRecommendedPresetsTask task = new LoadRecommendedPresetsTask();
        task.execute();
    }

    private void populateDatabase() {
        @SuppressLint("StaticFieldLeak")
        class populateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                UserPreset preset1 = new UserPreset("Relax", 38, 50, 100, 300,"green");
                UserPreset preset2 = new UserPreset("Good morning", 25, 50, 100, 300,"orange");
                UserPreset preset3 = new UserPreset("Cold", 12, 25, 100, 120,"blue");
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.userPresetDao().insertAll(preset1, preset2, preset3);
                return null;
            }
        }

        populateTask task = new populateTask();
        task.execute();
    }

    private void deleteAllPresetsFromDatabase()
    {
        @SuppressLint("StaticFieldLeak")
        class clearPresetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.userPresetDao().deleteAll();
                return null;
            }
        }

        clearPresetTask task = new clearPresetTask();
        task.execute();
    }
}