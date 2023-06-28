package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Local variables
    ArrayList<UserPreset> userPresets;

    // Views
    RecyclerView presetListView;
    Button showStatsButton;
    PresetAdapter presetAdapter;

    ImageView accountButton;

    // View pager setup
    private static final int NUM_PAGES = 5;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;


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

        // Setting preset recycler view adapter and layout manager
        presetListView = (RecyclerView) findViewById(R.id.rv_home_presets);

        // View pager setup
        viewPager = findViewById(R.id.sp_home_recommended);

        // Sets margins when scrolling between pages of recommended view pager items
        viewPager.setPageTransformer(new MarginPageTransformer(30));

        showStatsButton = findViewById(R.id.btn_home_viewStatistics);

        accountButton = findViewById(R.id.header_account_button);

        // PopulateDatabase can be used to load some generic sample data in the preset table
        // populateDatabase();

        loadUserPresets();
        loadRecommendedPresets();

        showStatsButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, StatisticsHome.class);
            MainActivity.this.startActivity(myIntent);
        });

        accountButton.setOnClickListener(v -> {
            // Initializing the popup menu and giving the reference as current context

            Context wrapper = new ContextThemeWrapper(MainActivity.this, R.style.PopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, accountButton);

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.account_dropdown, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                // Toast message on menu item clicked
                Toast.makeText(MainActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });
            // Showing the popup menu
            popupMenu.show();
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
    }

    public void updateRecommended(List<UserPreset> presets){
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
}