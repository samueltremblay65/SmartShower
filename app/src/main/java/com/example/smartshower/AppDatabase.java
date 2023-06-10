package com.example.smartshower;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class, UserPreset.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract UserPresetDao userPresetDao();
}