package com.example.smartshower;

import java.util.Dictionary;

public class UserProfile {
    private int userId;

    private String name;

    private Dictionary<String, Boolean> settings;

    public UserProfile(int userId, String name, Dictionary<String, Boolean> settings)
    {
        this.userId = userId;
        this.name = name;
        this.settings = settings;
    }

    public String getName()
    {
        return name;
    }
}
