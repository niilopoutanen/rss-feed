package com.niilopoutanen.rss_feed.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.database.dao.SourceDao;

@Database(entities = {Source.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SourceDao sourceDao();
}
