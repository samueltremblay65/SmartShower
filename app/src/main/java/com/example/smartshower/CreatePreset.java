package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreatePreset extends AppCompatActivity {

    // TextEdits
    EditText nameInput;
    EditText temperatureInput;
    EditText flowrateInput;
    EditText themeInput;
    EditText temperatureLimitInput;
    EditText timerInput;

    // Buttons
    Button createPreset;
    Button discardChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preset);

        // Changing system bar color
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));

        nameInput = findViewById(R.id.et_preset_name);
        temperatureInput = findViewById(R.id.et_preset_temperature);
        flowrateInput = findViewById(R.id.et_preset_flow);

        createPreset = findViewById(R.id.btn_create_preset);

        createPreset.setOnClickListener(v -> {
            int temperature = Integer.parseInt(temperatureInput.getText().toString());
            int flowrate = Integer.parseInt(flowrateInput.getText().toString());
            String presetName = nameInput.getText().toString().trim();

            UserPreset preset = new UserPreset(presetName, temperature, flowrate, getResources().getInteger(R.integer.default_temperature_limit), getResources().getInteger(R.integer.null_timelimit_db_value), "default");

            addPresetToDatabase(preset);

            Intent myIntent = new Intent(CreatePreset.this, MainActivity.class);
            CreatePreset.this.startActivity(myIntent);
        });
    }

    private void addPresetToDatabase(UserPreset preset) {
        @SuppressLint("StaticFieldLeak")
        class addPresetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                db.userPresetDao().insertAll(preset);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Toast.makeText(CreatePreset.this, "Successfully created new preset", Toast.LENGTH_LONG).show();
            }
        }

        addPresetTask task = new addPresetTask();
        task.execute();
    }
}