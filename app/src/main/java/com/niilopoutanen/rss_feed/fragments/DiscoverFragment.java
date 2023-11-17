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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialFadeThrough;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.SearchActivity;
import com.niilopoutanen.rss_feed.adapters.DiscoverCategoryAdapter;
import com.niilopoutanen.rss_feed.adapters.DiscoverResultAdapter;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rssparser.WebUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());



        postponeEnterTransition();
        loadData();
    }

    private void loadData() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        SaveSystem.loadCategories(new Callback<List<Category>>() {
            @Override
            public void onResult(List<Category> result) {
                categories = result;

                if (categoryAdapter != null) {
                    ((Activity) appContext).runOnUiThread(() -> {
                        categoryAdapter.setCategories(categories);
                        progressBar.setVisibility(View.GONE);
                        startPostponedEnterTransition();
                    });
                }
            }

            @Override
            public void onError(RSSException e) {

            }
        });

    }

    private void search(String query) {
        progressBar.setVisibility(View.VISIBLE);
        fetchFeedQuery(query, new Callback<List<FeedResult>>() {
            @Override
            public void onResult(List<FeedResult> result) {
                results = result;
                if (resultAdapter != null) {
                    ((Activity) appContext).runOnUiThread(() -> {
                        resultAdapter.setResults(results);
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onError(RSSException e) {

            }
        });
    }


    /**
     * Search Feedly API with the provided query
     *
     * @param query    Query to search with
     * @param callBack Returns a list of FeedResult objects that were found
     */
    public static void fetchFeedQuery(String query, Callback<List<FeedResult>> callBack) {
        String FEEDLY_ENDPOINT = "https://cloud.feedly.com/v3/search/feeds?query=";
        int FEEDLY_ENDPOINT_FETCHCOUNT = 40;

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL queryUrl = new URL(FEEDLY_ENDPOINT + query + "&count=" + FEEDLY_ENDPOINT_FETCHCOUNT + "&locale=en");
                String result = WebUtils.connect(queryUrl, true);
                List<FeedResult> results = FeedResult.parseResult(new JSONObject(result));
                callBack.onResult(results);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.discover_container), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


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