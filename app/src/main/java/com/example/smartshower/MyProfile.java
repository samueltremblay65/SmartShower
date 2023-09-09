package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MyProfile extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        super.setupUIElements();
        
        setHeader(getResources().getString(R.string.myprofile_header));
    }
}