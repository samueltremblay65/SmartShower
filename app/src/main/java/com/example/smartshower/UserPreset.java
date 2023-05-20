package com.example.smartshower;

import static java.sql.Types.ROWID;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Random;
import java.util.UUID;

@Entity
public class UserPreset {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
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
    }

    // Main constructor to specify custom preset
    @Ignore
    public UserPreset(int uid, String name, int temp, int tempLimit, int flowRate, String theme )
    {
        this.name = name;
        this.flowRate = flowRate;
        this.temp = temp;
        this.tempLimit = tempLimit;
        this.theme = theme;
    }

    @Ignore
    public UserPreset(String name, int temp, int tempLimit, int flowRate, String theme )
    {
        this.name = name;
        this.flowRate = flowRate;
        this.temp = temp;
        this.tempLimit = tempLimit;
        this.theme = theme;
    }
}
