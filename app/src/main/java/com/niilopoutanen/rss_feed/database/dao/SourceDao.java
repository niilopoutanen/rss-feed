package com.niilopoutanen.rss_feed.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.niilopoutanen.rss.Source;

import java.util.List;

@Dao
public interface SourceDao {
    @Query("SELECT * FROM source")
    List<Source> getAll();

    @Delete
    void delete(Source source);
}
