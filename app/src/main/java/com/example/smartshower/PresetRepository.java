package com.example.smartshower;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class PresetRepository {

    private UserPresetDao presetDao;
    private List<UserPreset> allPresets;

    // creating a constructor for our variables
    // and passing the variables to it.
    public PresetRepository(Application application) {
        ShowerDatabase database= ShowerDatabase.getInstance(application);
        presetDao = database.presetDao();
        allPresets = presetDao.getAll();
    }

    // creating a method to insert the data to our database.
    public void insert(UserPreset preset) {
        new InsertPresetAsyncTask(presetDao).execute(preset);
    }

    // creating a method to delete the data in our database.
    public void delete(UserPreset preset) {
        new DeletePresetAsyncTask(presetDao).execute();
    }

    public void deleteAllPresets() {
        new DeleteAllPresetsAsyncTask(presetDao).execute();
    }

    public List<UserPreset> getAllPresets() {
        return allPresets;
    }

    private static class InsertPresetAsyncTask extends AsyncTask<UserPreset, Void, Void> {
        private UserPresetDao presetDao;

        private InsertPresetAsyncTask(UserPresetDao presetDao) {
            this.presetDao = presetDao;
        }

        @Override
        protected Void doInBackground(UserPreset... preset) {
            // below line is use to insert our modal in dao.
            presetDao.insertAll(preset);
            return null;
        }
    }

    private static class DeletePresetAsyncTask extends AsyncTask<UserPreset, Void, Void> {
        private UserPresetDao presetDao;

        private DeletePresetAsyncTask(UserPresetDao presetDao) {
            this.presetDao = presetDao;
        }

        @Override
        protected Void doInBackground(UserPreset... userPreset) {
            presetDao.delete(userPreset[0]);
            return null;
        }
    }

    private static class DeleteAllPresetsAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserPresetDao presetDao;
        private DeleteAllPresetsAsyncTask(UserPresetDao presetDao) {
            this.presetDao = presetDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            presetDao.deleteAll();
            return null;
        }
    }
}