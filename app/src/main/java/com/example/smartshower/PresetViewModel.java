package com.example.smartshower;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PresetViewModel extends AndroidViewModel {
    private PresetRepository presetRepository;

    private final LiveData<List<UserPreset>> allPresets;

    public PresetViewModel (Application application) {
        super(application);
        presetRepository= new PresetRepository(application);
        allPresets = presetRepository.getAllPresets();
    }

    LiveData<List<UserPreset>> getAllPresets() { return allPresets; }

    public void insert(UserPreset preset) { presetRepository.insert(preset); }
}

