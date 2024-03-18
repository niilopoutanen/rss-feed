package com.niilopoutanen.rss_feed.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private AppRepository appRepository;

    private final LiveData<List<Source>> sources;
    private List<Post> posts;

    public AppViewModel(Application application){
        super(application);
        appRepository = new AppRepository(application);
        sources = appRepository.getAllSources();
    }


    public LiveData<List<Source>> getSources() {
        return sources;
    }
    public void setPosts(List<Post> posts){
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
