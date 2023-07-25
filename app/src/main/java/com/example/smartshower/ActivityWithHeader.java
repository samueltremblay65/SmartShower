package com.example.smartshower;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;

// This class is the subclass for all activities in the app
// It sets the system bar to the correct color and applies all
// the common event listeners e.g. account button
public class ActivityWithHeader extends AppCompatActivity {
    ImageView accountButton;
    TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Should be called in the constructor of the child class
    protected void setupUIElements()
    {
        // Changing system bar color
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shower_blue300));

        // Setting the menu when user clicks on the account button
        accountButton = findViewById(R.id.header_account_button);
        accountButton.setOnClickListener(v -> {
            // Initializing the popup menu and giving the reference as current context
            Context wrapper = new ContextThemeWrapper(ActivityWithHeader.this, R.style.PopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, accountButton);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(ActivityWithHeader.this, "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    Intent intent;
                    switch (item.getTitle().toString())
                    {
                        case "My profile":
                            intent = new Intent(ActivityWithHeader.this, MyProfile.class);
                            break;
                        case "Settings":
                            intent = new Intent(ActivityWithHeader.this, Settings.class);
                            break;
                            case "Contact us":
                            intent = new Intent(ActivityWithHeader.this, ContactUs.class);
                            break;
                        default:
                            Toast.makeText(wrapper, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                            throw new IllegalStateException("Invalid menu item clicked");
                    }

                    startActivity(intent);
                    return true;
                }
            });

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.account_dropdown, popupMenu.getMenu());

            // Showing the popup menu
            popupMenu.show();
        });

        greeting = findViewById(R.id.header);
        Date date = Calendar.getInstance().getTime();
        if(date.getHours() < 12)
        {
            greeting.setText("Good morning");
        }
        else if(date.getHours() < 18)
        {
            greeting.setText("Good afternoon");
        }
        else
        {
            greeting.setText("Good evening");
        }
    }
}