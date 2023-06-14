package com.example.bel_touchless.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {

    @Query("SELECT * FROM data")
    List<Data> getAllData();

    @Insert
    void insertData(Data data);

    @Delete
    void delete(Data data);

    @Query("SELECT * FROM data ORDER BY uid DESC LIMIT 10")
    List<Data> getLastData();
}
