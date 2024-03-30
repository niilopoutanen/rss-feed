package com.niilopoutanen.rss_feed.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private final AppRepository appRepository;

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
    public void updateSource(Source source){
        appRepository.insert(source);
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
            Post post1 = l1.get(i);
            Post post2 = l2.get(i);
            if(post1 == null || post2 == null) return true;
            if(!post1.equals(post2)){
                return true;
            }
        }
        return false; // If all posts match, cache is not outdated.
    }


}
