package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class CreatePreset extends AppCompatActivity {

    // List of elements

    // Labels
    TextView nameLabel;
    TextView temperatureLabel;
    TextView flowrateLabel;
    TextView themeLabel;
    TextView temperatureLimitLabel;
    TextView timerLabel;

    // TextEdits
    EditText nameInput;
    EditText temperatureInput;
    EditText flowrateInput;
    EditText themeInput;
    EditText temperatureLimitInput;
    EditText timerInput;

    // Other input elements

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

    }


}