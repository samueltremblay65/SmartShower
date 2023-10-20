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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        checkCredentials(usernameInput.getText().toString().trim(),
                passwordInput.getText().toString().trim());
    }

    public void checkCredentials(String username, String password)
    {
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount account = documentSnapshot.toObject(UserAccount.class);

                if(username.equals(account.getUsername()) && password.equals(account.getPassword()))
                {
                    loginSuccess(account);
                }
                else
                {
                    loginFailure();
                }
            }
        });
    }

    private void loginSuccess(UserAccount account)
    {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        // Add credentials to sharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.accounts_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.keys_account_id), account.getUserId());
        editor.putString(getString(R.string.keys_account_username), account.getUsername());
        editor.putString(getString(R.string.keys_account_password), account.getPassword());
        editor.apply();

        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    private void loginFailure()
    {
        Toast.makeText(this, "The credentials provided are incorrect", Toast.LENGTH_SHORT).show();
    }

}