package com.example.smartshower;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ShowerHistory extends ActivityWithHeader {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_history);
        super.setupUIElements();
        setHeader("History");
        removeBottomMargin();

        ArrayList<Statistics> history = (ArrayList<Statistics>) getIntent().getSerializableExtra("history");

        RecyclerView recyclerView = findViewById(R.id.history_recyclerview);
        recyclerView.setAdapter(new HistoryAdapter(history));
        recyclerView.addItemDecoration(new
                DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}