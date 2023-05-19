package com.example.smartshower;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Random;

@Entity
public class UserPreset {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "temp")
    public int temp;

    @ColumnInfo(name = "tempLimit")
    public int tempLimit;

    @ColumnInfo(name = "flowRate")
    public int flowRate;

    @ColumnInfo(name = "theme")
    public String theme;

    // Default constructor : creates object with default values
    public UserPreset(){
        this.name = "New preset";
        this.flowRate = 100;
        this.temp = 38;
        this.tempLimit = 50;
        this.theme = "basic";
    }

    // Main constructor to specify custom preset
    public UserPreset(String name, int temp, int tempLimit, int flowRate, String theme )
    {
        this.name = name;
        this.flowRate = flowRate;
        this.temp = temp;
        this.tempLimit = tempLimit;
        this.theme = theme;
    }
}
