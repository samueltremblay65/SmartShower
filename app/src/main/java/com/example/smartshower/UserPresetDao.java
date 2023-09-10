package com.example.smartshower;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserPresetDao {
    @Query("SELECT * FROM userpreset")
    List<UserPreset> getAll();

    @Query("SELECT * FROM userpreset WHERE uid IN (:userIds)")
    List<UserPreset> loadAllByIds(int[] userIds);

    @Update
    void update(UserPreset preset);

    @Insert
    void insertAll(UserPreset... users);

    @Query("DELETE FROM userpreset")
    void deleteAll();

    @Delete
    void delete(UserPreset user);
}