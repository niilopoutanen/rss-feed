package com.niilopoutanen.rss_feed.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.DiscoverResultAdapter;
import com.niilopoutanen.rss_feed.fragments.DiscoverFragment;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText searchField;
    RecyclerView searchRecyclerView;
    ProgressBar loader;

    private DiscoverResultAdapter discoverResultAdapter;
    private List<FeedResult> discoverResults = new ArrayList<>();

    //Feed type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_search);
        searchRecyclerView = findViewById(R.id.search_recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loader = findViewById(R.id.search_progress);

        discoverResultAdapter = new DiscoverResultAdapter(discoverResults);
        searchRecyclerView.setAdapter(discoverResultAdapter);

        searchField = findViewById(R.id.search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
    }

    private void search(String query) {
        loader.setVisibility(View.VISIBLE);
        if (query.length() > 0) {
            DiscoverFragment.fetchFeedQuery(query, new Callback<List<FeedResult>>() {
                @Override
                public void onResult(List<FeedResult> result) {
                    discoverResults = result;

                    if (discoverResultAdapter != null) {
                        runOnUiThread(() -> {
                            discoverResultAdapter.setResults(discoverResults);
                            loader.setVisibility(View.GONE);
                        });
                    }
                }

                @Override
                public void onError(RSSException e) {

                }
            });
        } else {
            discoverResultAdapter.setResults(new ArrayList<>());
        }
    }
}