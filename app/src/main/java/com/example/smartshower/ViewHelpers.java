package com.example.smartshower;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ViewHelpers {

    // This class contains static helper methods to populate UI elements
    // Reduces code duplication in areas that should behave the same way but are in different classes
    public static void setBackgroundTheme(ImageView backgroundView, UserPreset preset) {
        String theme = preset.theme;
        int drawableResource;
        switch (theme) {
            case "bg1":
                drawableResource = R.drawable.bg1;
                break;
            case "bg2":
                drawableResource = R.drawable.bg2;
                break;
            case "bg3":
                drawableResource = R.drawable.bg3;
                break;
            case "bg4":
                drawableResource = R.drawable.bg4;
                break;
            case "bg5":
                drawableResource = R.drawable.bg5;
                break;
            case "bg6":
                drawableResource = R.drawable.bg6;
                break;
            case "bg7":
                drawableResource = R.drawable.bg7;
                break;
            case "bg8":
                drawableResource = R.drawable.bg8;
                break;
            case "bg9":
                drawableResource = R.drawable.bg9;
                break;
            default:
                drawableResource = R.drawable.bg1;
                break;
        }

        backgroundView.setImageResource(drawableResource);
    }

    public static void setTranslucentBox(Context context, LinearLayout translucentBox, String theme)
    {
        // List of the themes that require a more opaque background
        ArrayList<String> darkThemes = new ArrayList();

        darkThemes.add("bg2");
        darkThemes.add("bg6");
        darkThemes.add("bg7");
        darkThemes.add("bg8");

        if(darkThemes.contains(theme))
        {
            translucentBox.setBackground(context.getResources().getDrawable(R.drawable.semi_opaque_box));
        }
    }

    public static String formatSeconds(Context context, int seconds){
        if(seconds == context.getResources().getInteger(R.integer.null_timelimit_db_value))
        {
            return "unlimited duration";
        }

        if(seconds == 60)
        {
            return "1 minute";
        }
        
        if(seconds / 60 == 1)
        {
            return String.format("1 minute and %02d seconds", seconds % 60);
        }

        if(seconds % 60 == 0)
        {
            return String.format("%d minutes", seconds / 60);
        }
        return String.format("%d minutes and %02d seconds", seconds / 60, seconds % 60);
    }
}

