package com.example.smartshower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PresetDatabase db = Room.databaseBuilder(getApplicationContext(),
                PresetDatabase.class, "database-name").build();
    }
}