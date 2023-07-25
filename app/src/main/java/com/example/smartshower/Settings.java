package com.example.smartshower;

import android.os.Bundle;

public class Settings extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.setupUIElements();
    }
}