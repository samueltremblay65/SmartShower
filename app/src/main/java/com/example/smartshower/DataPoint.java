package com.example.smartshower;

public class DataPoint<T>
{
    public boolean isValid;
    public T value;

    public DataPoint(T data)
    {
        this.isValid = true;
        value = data;
    }

    public DataPoint()
    {
        this.isValid = false;
    }

    public void invalidate()
    {
        this.isValid = false;
    }
}