package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.DiscoverCategoryAdapter;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.SaveSystem;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    Context appContext;
    Preferences preferences;
    List<Category> categories = new ArrayList<>();
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
        SaveSystem.loadCategories(result -> {
            categories = result;

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
        categoryRecyclerView.setLayoutManager(new CarouselLayoutManager());

        return rootView;
    }
}