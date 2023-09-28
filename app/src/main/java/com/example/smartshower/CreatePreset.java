package com.example.smartshower;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
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

    RecyclerView themePicker;

    int presetOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preset);
        super.setupUIElements();

        setHeader("Create preset");
        setSmallVerticalMargins();

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

            Intent intent = new Intent(CreatePreset.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            CreatePreset.this.startActivity(intent);
            finish();
        });

        discardChanges.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePreset.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            CreatePreset.this.startActivity(intent);
        });

        int[][] states = new int[][] {
            new int[] { android.R.attr.state_active}, // Normal state
            new int[] { android.R.attr.state_enabled}, // Typing state
        };

        int[] colors = new int[] {
            Color.BLACK,
            Color.BLACK
        };

        ColorStateList editTextColorStateList = new ColorStateList(states, colors);

        TextInputLayout presetNameInput = findViewById(R.id.ti_create_preset_preset_name);
        TextInputLayout presetThemeInput = findViewById(R.id.ti_create_preset_preset_theme);

        presetNameInput.setBoxStrokeColorStateList(editTextColorStateList);
        presetNameInput.setHintTextColor(editTextColorStateList);

        presetThemeInput.setBoxStrokeColorStateList(editTextColorStateList);
        presetThemeInput.setHintTextColor(editTextColorStateList);

        // Theme picker initialization
        themePicker = findViewById(R.id.theme_picker_recyclerview);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(CreatePreset.this, LinearLayoutManager.HORIZONTAL, false);
        themePicker.setLayoutManager(horizontalLayoutManager);

        class SpacesItemDecoration extends RecyclerView.ItemDecoration {
            private int space;

            public SpacesItemDecoration(int space) {
                this.space = space;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 0;
                outRect.right = 0;
                outRect.bottom = 0;

                // Add top margin only for the first item to avoid double space between items
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.left = 0;
                } else {
                    outRect.left = space;
                }
            }
        }

        // Add horizontal spacing between cardview items
        float marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10, // value in dp
                getResources().getDisplayMetrics()
        );

        SpacesItemDecoration horizontalSpacingDecoration = new SpacesItemDecoration((int) marginPx);
        themePicker.addItemDecoration(horizontalSpacingDecoration);

        // List of theme sources
        List<String> themeSources = new ArrayList<String>();
        
        themeSources.add("1");
        themeSources.add("2");
        themeSources.add("3");
        themeSources.add("4");
        themeSources.add("5");

        ThemePickerAdapter adapter = new ThemePickerAdapter(this, themeSources);

        adapter.setClickListener(this::selectTheme);
        themePicker.setAdapter(adapter);
    }

    public void selectTheme(String themeSource)
    {

    }

    private void showErrorDialog(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean validateEditTextInput_Text(EditText editText, String fieldName)
    {
        return validateEditTextInput_Text(editText, 64, fieldName);
    }

    private boolean validateEditTextInput_Text(TextInputEditText editText, String fieldName)
    {
        return validateEditTextInput_Text(editText, 64, fieldName);
    }

    private boolean validateEditTextInput_Text(TextInputEditText editText, int charLimit, String fieldName)
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