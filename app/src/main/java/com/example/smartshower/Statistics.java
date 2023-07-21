package com.example.smartshower;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Statistics {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    public int uid;

    @ColumnInfo(name="preset_id")
    public int presetId;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "energy")
    public int energy;

    @ColumnInfo(name = "water_usage")
    public float waterUsage;

    @ColumnInfo(name = "average_temperature")
    public int averageTemperature;

    @ColumnInfo(name = "date_time")
    public String dateTime;

    public Statistics()
    {

    }

    @Ignore
    public Statistics(ShowerSession session)
    {
        this.presetId = session.presetId;
        this.duration = session.getDuration();
        this.waterUsage = session.getWaterUsage();
        this.energy = session.getEnergy();
        this.averageTemperature = session.getAverageTemperature();
        this.dateTime = "6/7/2023:10/24";
    }

    @Ignore
    public Statistics(int presetId, int duration, int averageTemperature, int energy, float waterUsage, String dateTime)
    {
        this.presetId = presetId;
        this.duration = duration;
        this.averageTemperature = averageTemperature;
        this.energy = energy;
        this.waterUsage = waterUsage;
        this.dateTime = dateTime;
    }
}
