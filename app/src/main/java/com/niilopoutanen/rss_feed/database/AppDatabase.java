package com.niilopoutanen.rss_feed.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.database.dao.SourceDao;

@Database(entities = {Source.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static String DB_NAME = "rss_feed_db";

    public abstract SourceDao sourceDao();

    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME
                    )
                    .build();
        }
        return instance;
    }
}
