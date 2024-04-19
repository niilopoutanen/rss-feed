package com.niilopoutanen.rss_feed.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.niilopoutanen.rss_feed.rss.Source;

import java.util.List;

@Dao
public interface SourceDao {
    @Query("SELECT * FROM source")
    LiveData<List<Source>> getAll();

    @Query("SELECT * FROM source WHERE id=:id")
    LiveData<Source> getById(int id);

    @Delete
    void delete(Source source);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Source source);

    @Update
    void update(Source source);

    @Query("DELETE FROM source")
    void deleteAll();
}
