package com.example.smartshower;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowerSession {
    ArrayList<ShowerInstant> showerData;
    Date currentTime;
    int presetId;
    Integer maxTemperature;

    int timeAbove40 = 0;

    public ShowerSession(int presetId)
    {
        showerData = new ArrayList<>();
        currentTime = Calendar.getInstance().getTime();
        this.presetId = presetId;
    }

    public void update(int temperature, int flowrate)
    {
        showerData.add(new ShowerInstant(temperature, flowrate));
        if(maxTemperature == null)
        {
            maxTemperature = temperature;
        }
        else if(temperature > maxTemperature)
        {
            maxTemperature = temperature;
        }

        if(temperature > 40)
        {
            timeAbove40++;
        }
    }

    public boolean showHealthWarning(int temperature)
    {
        if(temperature > 50)
        {
            return true;
        }
        return temperature > 40 && timeAbove40 > 30;
    }

    public int getWaterUsage()
    {
        return getDuration() * getAverageFlowrate() / 8 / 100;
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

    public int getMaximalTemperature()
    {
        return maxTemperature;
    }

    public int getAverageFlowrate() {
        int total = 0;
        for(ShowerInstant instant: showerData)
        {
            total += instant.flow;
        }
        return total / showerData.size();
    }

    public long getDateTime()
    {
        return this.currentTime.getTime();
    }
}
