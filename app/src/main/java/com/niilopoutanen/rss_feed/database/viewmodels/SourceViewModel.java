package com.niilopoutanen.rss_feed.database.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.database.AppRepository;

import java.util.List;

public class SourceViewModel extends AndroidViewModel {
    private AppRepository appRepository;
    private final LiveData<List<Source>> sources;
    public SourceViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        sources = appRepository.getAll();
    }

    LiveData<List<Source>> getAll() { return sources; }

    public void insert(Source source) { appRepository.insert(source); }
}
