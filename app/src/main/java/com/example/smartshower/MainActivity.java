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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActivityWithHeader {

    // Local variables
    UserAccount account;
    List<UserPreset> presets;

    // Views
    RecyclerView presetListView;
    Button showStatsButton;
    ImageView addPresetButton;

    PresetAdapter presetAdapter;

    // View pager setup
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private int userId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        int userId = preferences.getInt(getString(R.string.keys_account_id), 0);

        if(userId == 0)
        {
            Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        getUserAccountFromDatabase();

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

        showStatsButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, StatisticsHome.class);

            MainActivity.this.startActivity(myIntent);
        });
    }

    // Starts the shower activity passing in the preset information

    public void startPresetShower(UserPreset preset) {
        Intent intent = new Intent(MainActivity.this, Shower.class);
        intent.putExtra("preset", preset);
        MainActivity.this.startActivity(intent);
    }

    public void startEditablePresetShower(UserPreset preset) {
        Intent intent = new Intent(MainActivity.this, Shower.class);
        intent.putExtra("preset", preset);
        intent.putExtra("isEditable", true);
        MainActivity.this.startActivity(intent);
    }

    public void deletePreset(UserPreset preset) {
        deletePresetFromDatabase(preset);
    }

    public void editPreset(UserPreset preset) {
        Intent intent = new Intent(MainActivity.this, CreatePreset.class);
        intent.putExtra("preset", preset);
        MainActivity.this.startActivity(intent);
    }

    // Populates the User Presets section
    public void updatePresets(List<UserPreset> returnedPresets) {
        presets = returnedPresets;

        // Sort the list by orderIndex to display presets in order set by user
        presets.sort((p1, p2) -> p1.orderIndex - p2.orderIndex);

        presetAdapter = new PresetAdapter(getApplicationContext(), presets, this::startEditablePresetShower, this::editPreset, this::deletePreset);
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

                db.collection("users").document(account.getUsername()).set(account);

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
    public void updateRecommended(List<UserPreset> presets, List<String> informationStrings) {
        pagerAdapter = new MainActivity.ScreenSlidePagerAdapter(this, presets, informationStrings);
        viewPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        List<UserPreset> presets;
        List<String> infoStrings;

        public ScreenSlidePagerAdapter(FragmentActivity fa, List<UserPreset> presets, List<String> infoStrings) {
            super(fa);
            this.presets = presets;
            this.infoStrings = infoStrings;
        }

        @Override
        public Fragment createFragment(int position) {

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPresetShower(presets.get(position));
                }
            };
            return new RecommendedSliderFragment(presets.get(position), infoStrings.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return presets.size();
        }
    }

    private void deletePresetFromDatabase(UserPreset preset)
    {
        Log.i("Jirafi", "Delete method called");
        account.removePreset(preset);

        db.collection("users").document(account.getUsername()).set(account)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        presets = account.getPresets();
                        updatePresets(presets);
                        Toast.makeText(MainActivity.this, "Successfully deleted preset", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to delete preset, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void getUserAccountFromDatabase()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        String username = preferences.getString(getString(R.string.keys_account_username), "");

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                account = documentSnapshot.toObject(UserAccount.class);

                if(account == null)
                {
                    throw new IllegalStateException("Could not find user account");
                }

                loadPresets();
                loadStatistics();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void loadStatistics()
    {
        DocumentReference docRef = db.collection("statistics").document(Integer.toString(account.getUserId()));
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            HashMap<String, Object> data = (HashMap<String, Object>) documentSnapshot.getData();
            List<Statistics> allStatistics = new ArrayList<>();

            if(data.containsKey("accountCreationDate"))
                data.remove("accountCreationDate");

            for (Object value : data.values()) {
                HashMap<String, Object> statisticsObject = (HashMap<String, Object>) value;

                int presetId = (int)(long) statisticsObject.get("presetId");
                int duration = (int)(long) statisticsObject.get("duration");
                int averageTemperature = (int)(long) statisticsObject.get("averageTemperature");
                float waterUsage = (float)(double) statisticsObject.get("waterUsage");
                long dateTime = (long) statisticsObject.get("dateTime");

                Statistics statistic = new Statistics(presetId, duration, averageTemperature, 0, waterUsage, dateTime);
                allStatistics.add(statistic);
            }

            StatisticsCompiler statisticsCompiler = new StatisticsCompiler(allStatistics);
            SmartRecommendationCreator recommendationCreator = new SmartRecommendationCreator(getApplicationContext(), statisticsCompiler);
            updateRecommended(recommendationCreator.getRecommendations(), recommendationCreator.getInformationStrings());

        }).addOnFailureListener(e -> {

        });
    }

    private void loadPresets()
    {
        presets = account.getPresets();
        updatePresets(presets);

        addPresetButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreatePreset.class);
            myIntent.putExtra("presetOrder", presets.size());
            MainActivity.this.startActivity(myIntent);
        });
    }
}