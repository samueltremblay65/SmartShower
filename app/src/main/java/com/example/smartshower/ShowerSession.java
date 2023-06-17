package com.example.smartshower;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowerSession {
    ArrayList<ShowerInstant> showerData;
    Date currentTime;
    int presetId;

    public ShowerSession(int presetId)
    {
        showerData = new ArrayList<>();
        currentTime = Calendar.getInstance().getTime();
        this.presetId = presetId;
    }

    public void update(int temperature, int flowrate)
    {
        showerData.add(new ShowerInstant(temperature, flowrate));
    }

    public int getWaterUsage()
    {
        return getDuration() / 8;
    }

    public int getEnergy()
    {
        return getAverageTemperature() * getDuration() * 10;
    }

    public int getDuration()
    {
        return showerData.size();
    }

    public int getAverageTemperature() {
        int total = 0;
        for(ShowerInstant instant: showerData)
        {
            total += instant.temperature;
        }
        return total / showerData.size();
    }
}
