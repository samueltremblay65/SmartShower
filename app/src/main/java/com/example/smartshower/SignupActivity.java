package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        signupButton = findViewById(R.id.su_btn_signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    public void signup()
    {
        Context context = getApplicationContext();
        FormValidationHelper validator = new FormValidationHelper(context);

        EditText usernameInput = findViewById(R.id.su_username);
        EditText emailInput = findViewById(R.id.su_email);
        EditText passwordInput = findViewById(R.id.su_password);

        // TODO: Email specific validation
        if(validator.validateEditTextInput_Text(usernameInput, 64, "username")
                && validator.validateEditTextInput_Email(emailInput, "email address")
                && validator.validateEditTextInput_Text(passwordInput, 64, "password"))
        {
            // Additional validation required
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String email = emailInput.getText().toString().trim();

            Random random = new Random();
            int userId = random.nextInt();
            createUserAccount(new UserAccount(userId, username, password, email));
            Toast.makeText(context, "Account successfully created", Toast.LENGTH_SHORT).show();

            Intent myIntent = new Intent(SignupActivity.this, MainActivity.class);
            SignupActivity.this.startActivity(myIntent);
        }
    }

    public void createUserAccount(UserAccount account)
    {
        db.collection("users").document(account.getUsername()).set(account);
        HashMap<String, Object> statsMap = new HashMap<>();
        statsMap.put("accountCreationDate", Calendar.getInstance().getTime().getTime());
        db.collection("statistics").document(Integer.toString(account.getUserId())).set(statsMap);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.accounts_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.keys_account_id), account.getUserId());
        editor.putString(getString(R.string.keys_account_username), account.getUsername());
        editor.commit();
    }
}