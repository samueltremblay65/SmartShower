package com.example.smartshower;

import android.widget.ImageView;

public class ViewHelpers {

    // This class contains static helper methods to populate UI elements
    // Reduces code duplication in areas that should behave the same way but are in different classes
    public static void setBackgroundTheme(ImageView backgroundView, UserPreset preset) {
        String theme = preset.theme;
        int drawableResource;
        switch (theme) {
            case "pink":
                drawableResource = R.drawable.bg1;
                break;
            case "plant":
                // drawable = ContextCompat.getDrawable(backgroundView.getContext(), R.drawable.bg7);
                drawableResource = R.drawable.bg7;
                break;
            case "multicolored":
                drawableResource = R.drawable.bg4;
                break;
            case "dark":
                drawableResource = R.drawable.bg3;
                break;
            case "zigzag":
                drawableResource = R.drawable.bg5;
                break;
            case "yellow":
                drawableResource = R.drawable.bg2;
                break;
            default:
                drawableResource = R.drawable.bg1;
        }

        backgroundView.setImageResource(drawableResource);
    }
}
