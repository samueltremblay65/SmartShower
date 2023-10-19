package com.example.smartshower;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class UserAccount {
    private String username;
    private int userId;
    private String password;
    private Dictionary<String, Boolean> settings;

    private List<UserPreset> presets;

    public UserAccount()
    {

    }

    public UserAccount(int userId, String username, String password, String email)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.presets = new ArrayList<>();
    }
    public UserAccount(int userId, String username, String password, String email, List<UserPreset> presets)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.presets = presets;
    }

    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    public int getUserId()
    {
        return userId;
    }
}
