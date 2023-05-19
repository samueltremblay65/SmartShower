package com.example.smartshower;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class PresetRepository {

    private UserPresetDao presetDao;
    private LiveData<List<UserPreset>> allPresets;

    PresetRepository(Application application) {
        PresetDatabase db = PresetDatabase.getDatabase(application);
        presetDao = db.userPresetDao();
        allPresets = presetDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<UserPreset>> getAllPresets() {
        return allPresets;
    }

    // Can only be called by non UI
    void insert(UserPreset preset) {
        PresetDatabase.databaseWriteExecutor.execute(() -> {
            presetDao.insertAll(preset);
        });
    }
}