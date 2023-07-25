package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ContactUs extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        super.setupUIElements();
    }
}