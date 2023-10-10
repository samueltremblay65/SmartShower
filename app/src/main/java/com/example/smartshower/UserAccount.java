package com.example.smartshower;

public class UserAccount {
    private String username;

    private String password;

    public UserAccount(String username, String password, String email)
    {
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
}
