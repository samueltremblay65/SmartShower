        package com.example.smartshower;

import androidx.annotation.InspectableProperty;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CreatePreset extends ActivityWithHeader {

    // TextEdits
    EditText nameInput;

    EditText themeInput;
    EditText temperatureInput;
    EditText flowrateInput;
    EditText temperatureLimitInput;
    EditText timerInput;

    Switch timerEnable;
    Switch temperatureLimitEnable;

    // Buttons
    Button createPreset;
    Button discardChanges;

    int presetOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preset);
        super.setupUIElements();

        presetOrder = getIntent().getIntExtra("presetOrder", -1);

        if(presetOrder == -1)
        {
            throw new IllegalStateException("No valid presetOrder provided for preset creation");
        }

        // Initializing the form elements
        nameInput = findViewById(R.id.et_preset_name);
        themeInput = findViewById(R.id.et_preset_theme);
        temperatureInput = findViewById(R.id.et_preset_temperature);
        flowrateInput = findViewById(R.id.et_preset_flow);

        temperatureLimitInput = findViewById(R.id.et_preset_temperature_limit);
        timerInput = findViewById(R.id.et_preset_time_limit);

        temperatureLimitEnable = findViewById(R.id.sw_cp_temp_limit);
        timerEnable = findViewById(R.id.sw_cp_timer);

        createPreset = findViewById(R.id.btn_create_preset);
        discardChanges = findViewById(R.id.btn_discard_preset);

        createPreset.setOnClickListener(v -> {
            // Mandatory field checks
            if(!validateEditTextInput_Text(nameInput,"Please enter a valid name") ||
                    !validateEditTextInput_Number(temperatureInput, getResources().getInteger(R.integer.min_temperature_c), getResources().getInteger(R.integer.max_temperature_c), "temperature") ||
                    !validateEditTextInput_Number(flowrateInput, 0, 100, "flow rate")) return;

            // Optional field checks
            if((timerEnable.isChecked() && !validateEditTextInput_Number(timerInput, 0, 3600, "time limit")) ||
                    (temperatureLimitEnable.isChecked() && !validateEditTextInput_Number(temperatureLimitInput, getResources().getInteger(R.integer.min_temperature_c),
                            getResources().getInteger(R.integer.max_temperature_c), "safe temperature limit"))) return;


            // Obtain values if all validations pass
            String presetName = nameInput.getText().toString().trim();
            int temperature = getIntegerFromEditText(temperatureInput, "temperature");
            int flowrate = getIntegerFromEditText(flowrateInput, "flow rate");

            int timerSeconds = getResources().getInteger(R.integer.null_timelimit_db_value);
            int temperatureLimit = getResources().getInteger(R.integer.max_temperature_c);

            if(timerEnable.isChecked())
            {
                timerSeconds = getIntegerFromEditText(timerInput, "time limit");
            }

            if(temperatureLimitEnable.isChecked())
            {
                temperatureLimit = getIntegerFromEditText(temperatureLimitInput, "safe temperature limit");
            }

            String theme = themeInput.getText().toString();

            UserPreset preset = new UserPreset(presetName, temperature, temperatureLimit,
                    flowrate, timerSeconds, theme, presetOrder);

            addPresetToDatabase(preset);

            Intent intent= new Intent(CreatePreset.this, MainActivity.class);
            CreatePreset.this.startActivity(intent);
        });

        discardChanges.setOnClickListener(v -> {
            Intent intent= new Intent(CreatePreset.this, MainActivity.class);
            CreatePreset.this.startActivity(intent);
        });
    }

    private void showErrorDialog(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean validateEditTextInput_Text(EditText editText, String fieldName)
    {
        return validateEditTextInput_Text(editText, 64, fieldName);
    }

    private boolean validateEditTextInput_Text(EditText editText, int charLimit, String fieldName)
    {
        String value = editText.getText().toString().trim();
        if(value.equals(""))
        {
            // TODO: add error message element
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        if(value.length() > charLimit)
        {
            showErrorDialog("Error: input is too long");
            // TODO: add error message
        }

        return true;
    }

    private boolean validateEditTextInput_Number(EditText editText, String fieldName)
    {
        String input = editText.getText().toString().trim();

        if(input.length() == 0)
        {
            // TODO: add error message
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        try {
            Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            // TODO: add error message
            showErrorDialog("Error: could not read integer from input");
            return false;
        }
        return true;
    }

    private boolean validateEditTextInput_Number(EditText editText, int lowerBound, int upperBound, String fieldName)
    {
        String input = editText.getText().toString().trim();
        int value;

        if(input.length() == 0)
        {
            // TODO: add error message
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        try {
            value = Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            // TODO: add error message
            showErrorDialog("Error: could not read integer from input");

            return false;
        }

        return (value >= lowerBound && value <= upperBound);
    }

    // Should always be validated before call
    private int getIntegerFromEditText(EditText editText, String fieldName)
    {
        String input = editText.getText().toString().trim();
        return Integer.parseInt(input);
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