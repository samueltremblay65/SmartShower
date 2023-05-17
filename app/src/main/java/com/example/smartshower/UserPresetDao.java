package com.example.smartshower;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserPresetDao {
    @Query("SELECT * FROM userpreset")
    List<UserPreset> getAll();

    @Query("SELECT * FROM userpreset WHERE uid IN (:userIds)")
    List<UserPreset> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(UserPreset... users);

    @Delete
    void delete(UserPreset user);
}