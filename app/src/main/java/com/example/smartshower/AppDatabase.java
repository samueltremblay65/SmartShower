package com.example.smartshower;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserPreset.class, Statistics.class}, version = 14)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserPresetDao userPresetDao();
    public abstract StatisticsDao statisticsDao();
}