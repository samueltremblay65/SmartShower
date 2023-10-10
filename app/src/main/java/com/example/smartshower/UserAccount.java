package com.example.smartshower;

public class UserAccount {
    private String username;
    
    private int userId;
    private String password;

    public UserAccount(int userId, String username, String password, String email)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public int getId()
    {
        return userId;
    }
}
