package com.niilopoutanen.rss_feed.database;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.database.dao.SourceDao;

import java.util.List;

public class AppRepository {
    private SourceDao sourceDao;
    private LiveData<List<Source>> sources;

    public AppRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        sourceDao = appDatabase.sourceDao();
        sources = sourceDao.getAll();
    }
    public AppRepository(Context context){
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        sourceDao = appDatabase.sourceDao();
        sources = sourceDao.getAll();
    }

    public LiveData<List<Source>> getAllSources() {
        return sources;
    }

    public LiveData<Source> getSourceById(int id){
        return sourceDao.getById(id);
    }
    public void insert(Source source) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            sourceDao.insert(source);
        });
    }
}
