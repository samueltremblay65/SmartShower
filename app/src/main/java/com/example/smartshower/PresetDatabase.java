package com.example.smartshower;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserPreset.class}, version = 5)
public abstract class PresetDatabase extends RoomDatabase {
    public abstract UserPresetDao userPresetDao();

    // Singleton pattern
    private static volatile PresetDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Creates new database object if database has not yet been initialized
    static PresetDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PresetDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PresetDatabase.class, "preset_database").addCallback(sRoomDatabaseCallback).
                                    fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                UserPresetDao dao = INSTANCE.userPresetDao();

                UserPreset basicPreset = new UserPreset();
                dao.insertAll(basicPreset);
            });
        }
    };
}