package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.DiscoverCategoryAdapter;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.Publisher;
import com.niilopoutanen.rss_feed.models.WebCallBack;
import com.niilopoutanen.rss_feed.utils.SaveSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscoverFragment extends Fragment {

    Context appContext;
    Preferences preferences;
    List<Category> categories = new ArrayList<>();
    List<Publisher> publishers;
    DiscoverCategoryAdapter adapter;
    public DiscoverFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }
    public DiscoverFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
    }
    private void loadData(){
        CompletableFuture<List<Category>> categoriesFuture = new CompletableFuture<>();
        CompletableFuture<List<Publisher>> publishersFuture = new CompletableFuture<>();

        SaveSystem.loadCategories(categoriesFuture::complete);
        SaveSystem.loadPublishers(publishersFuture::complete);

        CompletableFuture.allOf(categoriesFuture, publishersFuture).thenRun(() -> {
            categories = categoriesFuture.join();
            publishers = publishersFuture.join();
            if(adapter != null){
                ((Activity)appContext).runOnUiThread(() -> adapter.setCategories(categories));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        RecyclerView categoryRecyclerView = rootView.findViewById(R.id.discover_recyclerview);
        adapter = new DiscoverCategoryAdapter(categories);
        categoryRecyclerView.setAdapter(adapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        return rootView;
    }
}