package com.example.smartshower;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "secondsLimit")
    public int secondsLimit;

    @ColumnInfo(name = "theme")
    public String theme;

    @ColumnInfo(name = "order")
    public int orderIndex;

    // Default constructor : creates object with default values
    public UserPreset(){
        orderIndex = -1;
    }

    // Main constructor to specify custom preset
    @Ignore
    public UserPreset(int uid, String name, int temp, int tempLimit, int flowRate, int secondsLimit, String theme, int orderIndex)
    {
        this.name = name;
        this.flowRate = flowRate;
        this.temp = temp;
        this.tempLimit = tempLimit;
        this.theme = theme;
        this.secondsLimit = secondsLimit;
        this.orderIndex = orderIndex;
    }

    @Ignore
    public UserPreset(String name, int temp, int tempLimit, int flowRate, int secondsLimit, String theme, int orderIndex )
    {
        this.name = name;
        this.flowRate = flowRate;
        this.temp = temp;
        this.tempLimit = tempLimit;
        this.theme = theme;
        this.secondsLimit = secondsLimit;
        this.orderIndex = orderIndex;
    }

    public void updateOrder(int newOrder){
        this.orderIndex = newOrder;
    }
}
