package com.niilopoutanen.rss_feed.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.niilopoutanen.rss_feed.rss.Source;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private AppRepository appRepository;

    private final LiveData<List<Source>> sources;

    public AppViewModel(Application application){
        super(application);
        appRepository = new AppRepository(application);
        sources = appRepository.getAllSources();
    }


    LiveData<List<Source>> getSources() {
        return sources;
    }
}
