package com.example.smartshower;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserPreset.class}, version = 1)
public abstract class PresetDatabase extends RoomDatabase {
    public abstract UserPresetDao userPresetDao();
}