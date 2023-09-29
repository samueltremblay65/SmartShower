package com.example.smartshower;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ActivityWithHeader {

    // Local variables
    List<UserPreset> presets;

    // Views
    RecyclerView presetListView;
    Button showStatsButton;
    ImageView addPresetButton;

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

        // PopulateDatabase can be used to load some generic sample data in the preset table
        // populateDatabase();
        //deleteAllPresetsFromDatabase();

        // Load user profile from file
        List<String> files = Arrays.asList(getApplicationContext().fileList());

        String filename = "users.txt";
        File directory = getApplicationContext().getFilesDir();

        if(files.contains(filename))
        {
            File file = new File(directory, filename);
        }
        else
        {
            // Create file with user info
        }

        String displayName = "Sam";

        loadUserPresets();
        loadRecommendedPresets();

        showStatsButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, StatisticsHome.class);
            MainActivity.this.startActivity(myIntent);
        });

        addPresetButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreatePreset.class);
            myIntent.putExtra("presetOrder", presets.size());
            MainActivity.this.startActivity(myIntent);
        });
    }

    // Starts the shower activity passing in the preset information
    // TODO: Should we get the other info from the preset ID?
    public void startPresetShower(UserPreset preset) {
        Intent myIntent = new Intent(MainActivity.this, Shower.class);
        myIntent.putExtra("presetId", preset.uid); //Optional parameters
        myIntent.putExtra("temperature", preset.temp); //Optional parameters
        myIntent.putExtra("tempLimit", preset.tempLimit); //Optional parameters
        myIntent.putExtra("flowRate", preset.flowRate); //Optional parameters
        myIntent.putExtra("timeLimit", preset.secondsLimit);
        MainActivity.this.startActivity(myIntent);
    }

    public void deletePreset(UserPreset preset) {
        deletePresetFromDatabase(preset);
    }

    // Populates the User Presets section
    public void updatePresets(List<UserPreset> returnedPresets) {
        presets = returnedPresets;

        // Sort the list by orderIndex to display presets in order set by user
        presets.sort((p1, p2) -> p1.orderIndex - p2.orderIndex);

        presetAdapter = new PresetAdapter(getApplicationContext(), presets, this::startPresetShower, this::deletePreset, this::deletePreset);
        presetListView.setAdapter(presetAdapter);
        presetListView.setLayoutManager(new LinearLayoutManager(this));

        // Code for reordering items
        ItemTouchHelper.SimpleCallback reorderCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                int index = presets.get(fromPosition).orderIndex;
                presets.get(fromPosition).orderIndex = presets.get(toPosition).orderIndex;
                presets.get(toPosition).orderIndex = index;

                Collections.swap(presets, fromPosition, toPosition);

                updatePreset(presets.get(fromPosition));
                updatePreset(presets.get(toPosition));

                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(reorderCallback);
        itemTouchHelper.attachToRecyclerView(presetListView);
    }

    // Populates the recommended bar
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
                if(presets == null)
                {
                    presets = new ArrayList<UserPreset>();
                }
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
                UserPreset preset1 = new UserPreset("Relax", 38, 50, 100, 300,"zigzag", 0);
                UserPreset preset2 = new UserPreset("Good morning", 25, 50, 100, 300,"pink", 0);
                UserPreset preset3 = new UserPreset("Cold", 12, 25, 100, 120,"multicolored", 0);
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

    private void deletePresetFromDatabase(UserPreset preset)
    {
        @SuppressLint("StaticFieldLeak")
        class deletePresetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.userPresetDao().delete(preset);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    presets.remove(preset);
                    presetListView.getAdapter().notifyDataSetChanged();
                }
                catch(Exception e)
                {
                    Log.i("DeletePresets", "Caught exception while trying to remove preset from display list (UI only)");
                }

                // Can improve by using notifyItemRemoved if we can obtain the position
                Toast.makeText(MainActivity.this, "Successfully deleted preset", Toast.LENGTH_SHORT).show();
            }
        }

        deletePresetTask task = new deletePresetTask();
        task.execute();
    }

    private void updatePreset(UserPreset preset)
    {
        @SuppressLint("StaticFieldLeak")
        class updatePresetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.userPresetDao().update(preset);
                return null;
            }
        }

        updatePresetTask task = new updatePresetTask();
        task.execute();
    }
}