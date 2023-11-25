package com.example.smartshower;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class Settings extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.setupUIElements();

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_active}, // Normal state
                new int[] { android.R.attr.state_enabled}, // Typing state
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.BLACK
        };

        ColorStateList editTextColorStateList = new ColorStateList(states, colors);

        TextInputLayout showerAddressInput = findViewById(R.id.settings_shower_address);

        showerAddressInput.setBoxStrokeColorStateList(editTextColorStateList);
        showerAddressInput.setHintTextColor(editTextColorStateList);

        setHeader(getResources().getString(R.string.settings_header));

        EditText showerAddressEditText = findViewById(R.id.settings_shower_address_et);

        Button saveAddressButton = findViewById(R.id.btn_save_address);

        saveAddressButton.setOnClickListener(view -> {
            String address = showerAddressEditText.getText().toString().trim();

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.accounts_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("showerAddress", address);
            editor.commit();

            Toast.makeText(this, "Successfully updated shower IP", Toast.LENGTH_SHORT).show();
        });
    }
}