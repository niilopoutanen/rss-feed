package com.niilopoutanen.rss_feed.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.niilopoutanen.rss.Source;

import java.util.List;

@Dao
public interface SourceDao {
    @Query("SELECT * FROM source")
    LiveData<List<Source>> getAll();

    @Delete
    void delete(Source source);

    @Insert
    void insert(Source source);
    @Update
    void update(Source source);
}
