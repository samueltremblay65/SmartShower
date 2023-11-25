package com.example.smartshower;

import java.util.ArrayList;
import java.util.Comparator;
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

    public void addPreset(UserPreset preset)
    {
        if(presets == null)
        {
            presets = new ArrayList<>();
        }
        presets.add(preset);
    }

    public void removePreset(UserPreset preset)
    {
        presets.remove(preset);

        presets.sort(Comparator.comparingInt(p -> p.orderIndex));
        int i = 0;
        for(UserPreset userPreset: presets)
        {
            userPreset.orderIndex = i++;
        }

    }

    public List<UserPreset> getPresets()
    {
        if(presets == null)
        {
            presets = new ArrayList<>();
        }
        return presets;
    }

    public void updatePreset(UserPreset updatedPreset)
    {
        for(int i = 0; i < presets.size(); i++)
        {
            if(presets.get(i).uid == updatedPreset.uid)
            {
                presets.remove(i);
                presets.add(i, updatedPreset);
                return;
            }
        }
    }
}
