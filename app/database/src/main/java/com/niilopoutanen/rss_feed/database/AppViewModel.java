package com.niilopoutanen.rss_feed.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Preferences;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private final AppRepository appRepository;

    private final LiveData<List<Source>> sources;
    private Preferences preferences;
    private List<Post> posts;

    public AppViewModel(Application application){
        super(application);
        appRepository = new AppRepository(application);
        sources = appRepository.getAllSources();
    }


    public LiveData<List<Source>> getSources() {
        return sources;
    }
    public void updateSource(Source source){
        appRepository.insert(source);
    }
    public void setPreferences(Preferences preferences){
        this.preferences = preferences;
    }
    public Preferences getPreferences(){
        if(this.preferences == null){
            this.preferences = new Preferences();
        }
        return this.preferences;
    }
    public void setPostCache(List<Post> posts){
        if(posts == null || posts.isEmpty()) return;
        this.posts = posts;
    }

    public List<Post> getPostCache() {
        if(posts == null || posts.isEmpty()) return null;
        return posts;
    }

    public boolean isCacheOutdated(List<Post> newList) {
        if (newList == null || posts == null) return true;
        if(newList.isEmpty() || posts.isEmpty()) return true;
        
        List<Post> l1 = new ArrayList<>(newList);
        List<Post> l2 = new ArrayList<>(posts);

        Collections.sort(l1);
        Collections.sort(l2);

        for(int i = 0; i < l1.size(); i++){
            if(!l1.get(i).equals(l2.get(i))){
                return true; // If the post at same index is different, cache is outdated
            }
        }
        return false; // If all posts match, cache is not outdated.
    }


}
