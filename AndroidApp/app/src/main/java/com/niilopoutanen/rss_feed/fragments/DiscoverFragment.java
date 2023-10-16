package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.SearchActivity;
import com.niilopoutanen.rss_feed.adapters.DiscoverCategoryAdapter;
import com.niilopoutanen.rss_feed.adapters.DiscoverResultAdapter;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.WebHelper;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    Context appContext;
    Preferences preferences;
    List<Category> categories = new ArrayList<>();
    List<FeedResult> results = new ArrayList<>();
    DiscoverCategoryAdapter categoryAdapter;
    DiscoverResultAdapter resultAdapter;
    RecyclerView categoryRecyclerView;
    View progressBar;

    public DiscoverFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }

    public DiscoverFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
        if (appContext == null) {
            appContext = getContext();
        }
        loadData();
    }

    private void loadData() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        SaveSystem.loadCategories(result -> {
            categories = result;

            if (categoryAdapter != null) {
                ((Activity) appContext).runOnUiThread(() -> {
                    categoryAdapter.setCategories(categories);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });

    }

    private void search(String query) {
        progressBar.setVisibility(View.VISIBLE);
        WebHelper.fetchFeedQuery(query, result -> {
            results = result;
            if (resultAdapter != null) {
                ((Activity) appContext).runOnUiThread(() -> {
                    resultAdapter.setResults(results);
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        progressBar = rootView.findViewById(R.id.discover_progress);

        categoryRecyclerView = rootView.findViewById(R.id.discover_categories_recyclerview);
        categoryAdapter = new DiscoverCategoryAdapter(categories, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        RecyclerView resultsRecyclerView = rootView.findViewById(R.id.discover_results_recyclerview);
        resultAdapter = new DiscoverResultAdapter(results);
        resultsRecyclerView.setAdapter(resultAdapter);

        View searchBtn = rootView.findViewById(R.id.discover_search);
        searchBtn.setOnClickListener(v -> {
            Intent searchIntent = new Intent(appContext, SearchActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) appContext, searchBtn, "search");
            appContext.startActivity(searchIntent, options.toBundle());
        });

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int position = categoryRecyclerView.getChildAdapterPosition(v);
        Category categoryClicked = categories.get(position);
        search(categoryClicked.getQuery());
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }
}