package com.niilopoutanen.rss_feed.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.DiscoverResultAdapter;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.RSSPost;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.WebHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText searchField;
    RecyclerView searchRecyclerView;
    int activeType;

    //Discover type
    public static final int DISCOVER = 1;
    private DiscoverResultAdapter discoverResultAdapter;
    private List<FeedResult> discoverResults = new ArrayList<>();

    //Feed type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        searchRecyclerView = findViewById(R.id.search_recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            switch (extras.getInt("type")) {
                case DISCOVER:
                    discoverResultAdapter = new DiscoverResultAdapter(discoverResults);
                    searchRecyclerView.setAdapter(discoverResultAdapter);
                    activeType = DISCOVER;
                    break;
            }
        }
        else{
            finish();
        }

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

    private void search(String query){
        if(activeType == DISCOVER){
            if(query.length() > 0){
                WebHelper.fetchFeedQuery(query, result -> {
                    discoverResults = FeedResult.parseResult(result);

                    if(discoverResultAdapter != null){
                        runOnUiThread(() -> {
                            discoverResultAdapter.setResults(discoverResults);
                        });
                    }
                });
            }
            else{
                discoverResultAdapter.setResults(new ArrayList<>());
            }
        }
    }
}