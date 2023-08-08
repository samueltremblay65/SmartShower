package com.example.smartshower;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

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
    public long dateTime;

    public int getMonth()
    {
        Date date = new Date(this.dateTime);
        return date.getMonth();
    }

    public Date parseDate()
    {
        return new Date(this.dateTime);
    }

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
        this.dateTime = session.getDateTime();
    }

    @Ignore
    public Statistics(int presetId, int duration, int averageTemperature, int energy, float waterUsage, long dateTime)
    {
        this.presetId = presetId;
        this.duration = duration;
        this.averageTemperature = averageTemperature;
        this.energy = energy;
        this.waterUsage = waterUsage;
        this.dateTime = dateTime;
    }
}
