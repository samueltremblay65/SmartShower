package com.example.smartshower;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    TextView header;

    String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String preferencesFile = getString(R.string.accounts_file);
        SharedPreferences preferences = getSharedPreferences(preferencesFile, MODE_PRIVATE);
        displayName = preferences.getString(getString(R.string.keys_account_username), "");
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
                        case "My statistics":
                            intent = new Intent(ActivityWithHeader.this, StatisticsHome.class);
                            break;
                        case "Logout":
                            logout();
                            intent = new Intent(ActivityWithHeader.this, WelcomeActivity.class);
                            finish();
                            break;
                        default:
                            Toast.makeText(wrapper, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                            throw new IllegalStateException("Invalid menu item clicked");
                    }

                    if(intent != null) startActivity(intent);

                    return true;
                }
            });

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.account_dropdown, popupMenu.getMenu());

            // Showing the popup menu
            popupMenu.show();
        });

        header = findViewById(R.id.header);
        Date date = Calendar.getInstance().getTime();
        if(date.getHours() < 12)
        {
            String text = getResources().getString(R.string.greeting_good_morning, displayName);
            header.setText(text);
        }
        else if(date.getHours() < 18)
        {
            String text = getResources().getString(R.string.greeting_good_afternoon, displayName);
            header.setText(text);
        }
        else
        {
            String text = getResources().getString(R.string.greeting_good_evening, displayName);
            header.setText(text);
        }
    }

    public void setHeader(String title)
    {
        header.setText(title);
        header.setTextSize((float) getResources().getDimension(R.dimen.sectionName));
        header.setTypeface(header.getTypeface(), Typeface.BOLD);
    }

    public void removeBottomMargin()
    {
        LinearLayout mainLayout = findViewById(R.id.home_header);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Convert desired value in dp to pixels
        Resources r = getApplicationContext().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5, // Desired margin in dp
                r.getDisplayMetrics()
        );

        params.setMargins(0, 0, 0, px);
        mainLayout.setLayoutParams(params);
    }

    public void setSmallBottomMargin()
    {
        LinearLayout mainLayout = findViewById(R.id.home_header);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Convert desired value in dp to pixels
        Resources r = getApplicationContext().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20, // Desired margin in dp
                r.getDisplayMetrics()
        );

        params.setMargins(0, 0, 0, px);
        mainLayout.setLayoutParams(params);
    }

    public void setSmallVerticalMargins()
    {
        LinearLayout mainLayout = findViewById(R.id.home_header);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        // Convert desired value in dp to pixels
        Resources r = getApplicationContext().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20, // Desired margin in dp
                r.getDisplayMetrics()
        );

        params.setMargins(0, px, 0, px);
        mainLayout.setLayoutParams(params);
    }

    private void logout()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.accounts_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}