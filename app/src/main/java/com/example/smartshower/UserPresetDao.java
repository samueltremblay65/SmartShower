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

    @Query("SELECT * FROM userpreset WHERE userId = (:userId)")
    List<UserPreset> getAllForUser(int userId);

    @Update
    void update(UserPreset preset);

    @Insert
    void insertAll(UserPreset... users);

    @Query("DELETE FROM userpreset")
    void deleteAll();

    @Delete
    void delete(UserPreset user);
}