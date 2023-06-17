package com.example.smartshower;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatisticsDao {
    @Query("SELECT * FROM statistics")
    List<Statistics> getAll();

    @Query("SELECT * FROM statistics WHERE uid IN (:statIds)")
    List<Statistics> loadAllByIds(int[] statIds);

    @Insert
    void insertAll(Statistics... statistics);

    @Query("DELETE FROM statistics")
    void deleteAll();

    @Delete
    void delete(Statistics statistics);
}
