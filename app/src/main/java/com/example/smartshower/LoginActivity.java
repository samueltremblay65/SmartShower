package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Changing system bar color
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));


        int[][] states = new int[][] {
                new int[] { android.R.attr.state_active}, // Normal state
                new int[] { android.R.attr.state_enabled}, // Typing state
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.BLACK
        };

        ColorStateList editTextColorStateList = new ColorStateList(states, colors);

        TextInputLayout usernameInput = findViewById(R.id.li_username_lo);
        TextInputLayout passwordInput = findViewById(R.id.li_password_lo);

        usernameInput.setBoxStrokeColorStateList(editTextColorStateList);
        usernameInput.setHintTextColor(editTextColorStateList);

        passwordInput.setBoxStrokeColorStateList(editTextColorStateList);
        passwordInput.setHintTextColor(editTextColorStateList);

        loginButton = findViewById(R.id.li_btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login()
    {
        Context context = getApplicationContext();
        FormValidationHelper validator = new FormValidationHelper(context);

        EditText usernameInput = findViewById(R.id.li_username);
        EditText passwordInput = findViewById(R.id.li_password);

        if(!validator.validateEditTextInput_Text(usernameInput, 64, "username")
                || !validator.validateEditTextInput_Text(passwordInput, 64, "password")) {
            Log.i("Accounts", "Invalid input provided");
        }

        if(checkCredentials(usernameInput.getText().toString().trim(),
                passwordInput.getText().toString().trim())) {
            Toast.makeText(context, "Successfully logged in", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        else {
            Toast.makeText(context, "Unsuccessful login attempt", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkCredentials(String username, String password)
    {
        String preferencesFile = getString(R.string.accounts_file);
        SharedPreferences preferences = getSharedPreferences(preferencesFile, MODE_PRIVATE);
        String storedUsername = preferences.getString(getString(R.string.keys_account_username), "");
        int storedId = preferences.getInt(getString(R.string.keys_account_id), 0);
        String correctPassword = preferences.getString(getString(R.string.keys_account_password), "");

        if(username.equals(storedUsername) && password.equals(correctPassword)) {
            return true;
        }

        return false;
    }
}