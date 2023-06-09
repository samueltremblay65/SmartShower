package com.example.smartshower;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class PresetViewModel extends AndroidViewModel {

    private PresetRepository presetRepository;

    private List<UserPreset> allPresets;

    public PresetViewModel(@NonNull Application application) {
        super(application);
        presetRepository = new PresetRepository(application);
        allPresets = presetRepository.getAllPresets();
    }

    public void insert(UserPreset preset) {
        presetRepository.insert(preset);
    }

    public void delete(UserPreset preset) {
        presetRepository.delete(preset);
    }

    public void deleteAllPresets() {
        presetRepository.deleteAllPresets();
    }

    public List<UserPreset> getAllPresets() {
        return allPresets;
    }
}