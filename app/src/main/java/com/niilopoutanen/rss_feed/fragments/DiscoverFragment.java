package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.niilopoutanen.rss_feed.activities.SearchActivity;
import com.niilopoutanen.rss_feed.adapters.DiscoverCategoryAdapter;
import com.niilopoutanen.rss_feed.adapters.DiscoverResultAdapter;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.models.Category;
import com.niilopoutanen.rss_feed.common.models.FeedResult;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.parser.Callback;
import com.niilopoutanen.rss_feed.parser.RSSException;
import com.niilopoutanen.rss_feed.parser.WebUtils;
import com.niilopoutanen.rss_feed.common.PreferencesManager;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    Context context;
    Preferences preferences;
    List<Category> categories = new ArrayList<>();
    List<FeedResult> results = new ArrayList<>();
    DiscoverCategoryAdapter categoryAdapter;
    DiscoverResultAdapter resultAdapter;
    RecyclerView categoryRecyclerView, resultsRecyclerView;

    NestedScrollView scrollView;
    View progressBar;

    public DiscoverFragment(Context context, Preferences preferences) {
        this.context = context;
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
        if (context == null) {
            context = getContext();
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
        categories = Category.getCategories(PreferencesManager.getUserLocale());

        if (categoryAdapter != null) {
            categoryAdapter.setCategories(categories);
            progressBar.setVisibility(View.GONE);
        }


    }

    private void search(String query) {
        progressBar.setVisibility(View.VISIBLE);
        fetchFeedQuery(query, new Callback<List<FeedResult>>() {
            @Override
            public void onResult(List<FeedResult> result) {
                results = result;
                if (resultAdapter != null) {
                    ((Activity) context).runOnUiThread(() -> {
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
            if (query.equals(Category.CATEGORY_RECOMMENDED)) {
                callBack.onResult(loadRecommendations());
                return;
            }

            try {
                URL queryUrl = new URL(FEEDLY_ENDPOINT + query + "&count=" + FEEDLY_ENDPOINT_FETCHCOUNT + "&locale=en");
                String result = WebUtils.connectRaw(queryUrl);
                List<FeedResult> results = FeedResult.parseResult(new JSONObject(result));
                callBack.onResult(results);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

        });
    }

    private static List<FeedResult> loadRecommendations() {
        Category.Country locale = PreferencesManager.getUserLocale();
        String baseURL = "https://raw.githubusercontent.com/niilopoutanen/RSS-Feed/app-resources/";
        List<FeedResult> results = new ArrayList<>();

        try {
            URL url;
            String fileName;

            switch (locale) {
                case FI:
                    fileName = "recommended-fi.json";
                    break;
                case EN:
                default:
                    fileName = "recommended.json";
                    break;
            }

            url = new URL(baseURL + fileName);
            String result = WebUtils.connectRaw(url);
            results = FeedResult.parseResult(new JSONObject(result));

        } catch (Exception ignored) {
        }

        return results;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        scrollView = rootView.findViewById(R.id.discover_nestedscrollview);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.discover_container), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        PreferencesManager.setHeader(context, rootView.findViewById(R.id.discover_header));

        progressBar = rootView.findViewById(R.id.discover_progress);

        categoryRecyclerView = rootView.findViewById(R.id.discover_categories_recyclerview);
        categoryAdapter = new DiscoverCategoryAdapter(categories, context, this);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.post(this::startPostponedEnterTransition);

        resultsRecyclerView = rootView.findViewById(R.id.discover_results_recyclerview);
        resultAdapter = new DiscoverResultAdapter(results);
        resultsRecyclerView.setAdapter(resultAdapter);
        resultsRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));

        View searchBtn = rootView.findViewById(R.id.discover_search);
        searchBtn.setOnClickListener(v -> {
            Intent searchIntent = new Intent(context, SearchActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, searchBtn, "search");
            context.startActivity(searchIntent, options.toBundle());
        });

        Category activeCategory = categories.stream()
                  .filter(Category::isActive)
                  .findFirst()
                  .orElseGet(() -> categories.get(0));

        activeCategory.setActive(true);
        search(activeCategory.getQuery());

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int position = categoryRecyclerView.getChildAdapterPosition(v);
        for (Category category : categories) {
            category.setActive(false);
        }

        Category categoryClicked = categories.get(position);
        categories.get(position).setActive(true);
        categoryAdapter.notifyDataSetChanged();
        search(categoryClicked.getQuery());
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }
}