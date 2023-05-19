package com.example.smartshower;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class PresetRepository {

    private UserPresetDao presetDao;
    private LiveData<List<UserPreset>> allPresets;

    // Interface used to interact with database (through DAO)
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

    // Deletes all records from table
    void deleteAll() {
       PresetDatabase.databaseWriteExecutor.execute(() -> {
            presetDao.deleteAll();
       });
    }
}