package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        TextInputLayout usernameInput = findViewById(R.id.su_username_lo);
        TextInputLayout emailInput = findViewById(R.id.su_email_lo);
        TextInputLayout passwordInput = findViewById(R.id.su_password_lo);


        usernameInput.setBoxStrokeColorStateList(editTextColorStateList);
        usernameInput.setHintTextColor(editTextColorStateList);

        emailInput.setBoxStrokeColorStateList(editTextColorStateList);
        emailInput.setHintTextColor(editTextColorStateList);

        passwordInput.setBoxStrokeColorStateList(editTextColorStateList);
        passwordInput.setHintTextColor(editTextColorStateList);

    }
}