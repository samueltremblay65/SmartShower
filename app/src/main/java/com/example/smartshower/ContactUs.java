package com.example.smartshower;

import android.os.Bundle;

public class ContactUs extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        super.setupUIElements();

        setHeader(getResources().getString(R.string.contact_us_header));

        removeBottomMargin();
    }
}