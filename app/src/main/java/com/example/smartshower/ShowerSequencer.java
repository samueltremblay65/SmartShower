package com.example.smartshower;

public class ShowerSequencer {

    public static int getTemperatureInstant(String sequenceName, int instant)
    {
        int result;
        switch(sequenceName)
        {
            case "control":
                result = getControlSequence(instant);
                break;
            case "decreasing":
                result = getDecreasingSequence(instant);
                break;
            case "flow":
                result = getFlowRateControlSequence(instant);
                break;
            default:
                throw new IllegalStateException("Sequence name is invalid");
        }
        return result;
    }

    private static int getControlSequence(int instant)
    {
        if(instant <= 30)
        {
            return (int) Math.round(35 + 10 * Math.sin((double) instant * 2 * Math.PI / 20));
        }
        if(instant < 40)
        {
            return 25;
        }
        if(instant < 50)
        {
            return 50;
        }
        return 25;
    }

    private static int getDecreasingSequence(int instant)
    {
        return Math.max((int)(38 - (double) instant * 0.1), 18);
    }

    private static int getFlowRateControlSequence(int instant)
    {
        if(instant < 10)
        {
            return 100;
        }
        if(instant < 20)
        {
            return 50;
        }
        if(instant < 30)
        {
            return 100;
        }
        else {
            return 100 - ((instant - 20) / 10 * 10);
        }

    }
}
