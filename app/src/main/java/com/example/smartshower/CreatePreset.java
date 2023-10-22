package com.example.smartshower;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
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

    String selectedTheme;

    int presetOrder;

    boolean editMode = false;

    UserAccount account;
    
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FormValidationHelper validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preset);
        super.setupUIElements();

        setHeader("Create preset");
        setSmallVerticalMargins();

        getUserAccountFromDatabase();

        validator = new FormValidationHelper(getApplicationContext());

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

        UserPreset existingPreset = (UserPreset) getIntent().getSerializableExtra("preset");

        // Edit preset code path
        if(existingPreset != null)
        {
            editMode = true;

            // Insert values into inputs
            nameInput.setText(existingPreset.name);
            temperatureInput.setText(Integer.toString(existingPreset.temp));
            flowrateInput.setText(Integer.toString(existingPreset.temp));
            presetOrder = existingPreset.orderIndex;
        }
        else
        {
            presetOrder = getIntent().getIntExtra("presetOrder", -1);

            if(presetOrder == -1)
            {
                throw new IllegalStateException("No valid presetOrder provided for preset creation");
            }
        }

        createPreset.setOnClickListener(v -> {
            if(validatePresetForm())
            {
                // Obtain values if all validations pass
                String presetName = nameInput.getText().toString().trim();
                int temperature = validator.getIntegerFromEditText(temperatureInput, "temperature");
                int flowrate = validator.getIntegerFromEditText(flowrateInput, "flow rate");

                int timerSeconds = getResources().getInteger(R.integer.null_timelimit_db_value);
                int temperatureLimit = getResources().getInteger(R.integer.max_temperature_c);

                if(timerEnable.isChecked())
                {
                    timerSeconds = validator.getIntegerFromEditText(timerInput, "time limit");
                }

                if(temperatureLimitEnable.isChecked())
                {
                    temperatureLimit = validator.getIntegerFromEditText(temperatureLimitInput, "safe temperature limit");
                }

                if(editMode)
                {
                    // Update firebase document
                    account.updatePreset(existingPreset);
                    db.collection("users").document(account.getUsername()).update("presets", account.getPresets());
                }
                else
                {
                    UserPreset newPreset = new UserPreset(presetName, temperature, temperatureLimit,
                            flowrate, timerSeconds, selectedTheme, presetOrder, 0);
                    addPresetToDatabase(newPreset);
                }
            }
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
        
        themeSources.add("bg1");
        themeSources.add("bg2");
        themeSources.add("bg3");
        themeSources.add("bg4");
        themeSources.add("bg5");
        themeSources.add("bg6");
        themeSources.add("bg7");
        themeSources.add("bg8");
        themeSources.add("bg9");

        ThemePickerAdapter adapter = new ThemePickerAdapter(this, themeSources);

        adapter.setClickListener(this::selectTheme);
        themePicker.setAdapter(adapter);
    }

    public boolean validatePresetForm()
    {
        // Mandatory field checks
        if(!validator.validateEditTextInput_Text(nameInput,"preset name") ||
                !validator.validateEditTextInput_Number(temperatureInput, getResources().getInteger(R.integer.min_temperature_c), getResources().getInteger(R.integer.max_temperature_c), "temperature") ||
                !validator.validateEditTextInput_Number(flowrateInput, 0, 100, "flow rate")) return false;

        // Theme selection check
        if(this.selectedTheme == null || this.selectedTheme.isEmpty())
        {
            Toast.makeText(this, "Please select a theme", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Optional field checks
        if((timerEnable.isChecked() && !validator.validateEditTextInput_Number(timerInput, 0, 3600, "time limit")) ||
                (temperatureLimitEnable.isChecked() && !validator.validateEditTextInput_Number(temperatureLimitInput, getResources().getInteger(R.integer.min_temperature_c),
                        getResources().getInteger(R.integer.max_temperature_c), "safe temperature limit"))) return false;
        return true;
    }

    public void selectTheme(String themeSource)
    {
        selectedTheme = themeSource;
        
        int themePosition = ((ThemePickerAdapter) themePicker.getAdapter()).getPositionForTheme(themeSource);
        if(themePosition != -1)
        {
            themePicker.findViewHolderForAdapterPosition(themePosition).itemView.performClick();
        }
    }

    private void addPresetToDatabase(UserPreset preset) {
        account.addPreset(preset);

        db.collection("users").document(account.getUsername()).set(account).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreatePreset.this, "Successfully created new preset", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreatePreset.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CreatePreset.this.startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePreset.this, "Could not create preset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserAccountFromDatabase()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.accounts_file), MODE_PRIVATE);
        String username = preferences.getString(getString(R.string.keys_account_username), "");

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                account = documentSnapshot.toObject(UserAccount.class);

                if(account == null)
                {
                    throw new IllegalStateException("Could not find user account");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}