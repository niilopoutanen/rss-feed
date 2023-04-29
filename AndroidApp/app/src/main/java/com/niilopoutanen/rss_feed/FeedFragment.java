package com.niilopoutanen.rss_feed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.rss.FeedAdapter;
import com.niilopoutanen.rss_feed.rss.RSSParser;
import com.niilopoutanen.rss_feed.rss.RSSPost;
import com.niilopoutanen.rss_feed.sources.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.sources.Source;
import com.niilopoutanen.rss_feed.web.WebCallBack;
import com.niilopoutanen.rss_feed.web.WebHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class FeedFragment extends Fragment implements RecyclerViewInterface {

    List<Source> sources = new ArrayList<>();
    List<RSSPost> feed = new ArrayList<>();
    String viewTitle;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    Context appContext;
    SwipeRefreshLayout recyclerviewRefresh;
    Preferences preferences;

    @ColorInt
    int colorAccent;

    public FeedFragment(List<Source> sources, Preferences preferences) {
        this.sources = sources;
        this.preferences = preferences;
    }

    public FeedFragment(Source source, Preferences preferences) {
        //single source view
        sources.add(source);
        viewTitle = source.getName();
        this.preferences = preferences;
    }

    public FeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getContext();
        if (appContext == null) {
            return;
        }

        preferences = savedInstanceState != null ? (Preferences) savedInstanceState.getSerializable("preferences") : null;
        sources = savedInstanceState != null ? (List<Source>) savedInstanceState.getSerializable("sources") : null;

        colorAccent = PreferencesManager.getAccentColor(appContext);
    }


    @Override
    public void onItemClick(int position) {
        RSSPost item = feed.get(position);
        Intent articleIntent = new Intent(appContext, ArticleActivity.class)
                .putExtra("postUrl", item.getPostLink())
                .putExtra("postPublisher", preferences.s_feedcard_authorname ? item.getSourceName() : item.getAuthor())
                .putExtra("postPublishTime", item.getPublishTime())
                .putExtra("preferences", preferences);
        appContext.startActivity(articleIntent);
    }


    @Override
    public void onItemLongClick(int position) {

    }

    public void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    private boolean checkValidity() {
        if (sources.size() <= 0) {
            Toast.makeText(appContext, appContext.getString(R.string.nosources), Toast.LENGTH_LONG).show();
            return false;
        }

        ConnectivityManager connectionManager = appContext.getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectionManager.getActiveNetworkInfo();
        if (currentNetwork == null || !currentNetwork.isConnected()) {
            Toast.makeText(appContext, appContext.getString(R.string.nointernet), Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void updateFeed(final Callback callback) {
        if (recyclerviewRefresh != null) {
            recyclerviewRefresh.setRefreshing(true);
        }
        feed.clear();

        // Create a new Executor for running the feed updates on a background thread
        Executor executor = Executors.newSingleThreadExecutor();

        // Submit each update to the executor
        executor.execute(() -> {
            for (Source source : sources) {
                WebHelper.getFeedData(source.getFeedUrl(), new WebCallBack<String>() {
                    @Override
                    public void onResult(String result) {
                        List<RSSPost> posts = RSSParser.parseRssFeed(result);

                        for (RSSPost post : posts) {
                            post.setSourceName(source.getName());
                            feed.add(post);
                        }

                    }
                });

            }
            Collections.sort(feed);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = rootView.findViewById(R.id.feed_container);

        viewTitle = appContext != null ? appContext.getString(R.string.feed_header) : "Feed";
        viewTitle = viewTitle.length() > 20 ? viewTitle.substring(0, 20) + "..." : viewTitle;

        adapter = new FeedAdapter(preferences, feed, appContext, viewTitle, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        recyclerviewRefresh = rootView.findViewById(R.id.recyclerview_refresher);
        recyclerviewRefresh.setColorSchemeColors(colorAccent);
        recyclerviewRefresh.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));

        recyclerviewRefresh.setOnRefreshListener(() -> updateFeed(() -> {
            adapter.notifyDataSetChanged();
            recyclerviewRefresh.setRefreshing(false);
        }));

        if (checkValidity()) {
            updateFeed(() -> {
                adapter.notifyDataSetChanged();
                recyclerviewRefresh.setRefreshing(false);
            });
        }

        return rootView;
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FeedAdapter adapter = (FeedAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setImageWidth(appContext);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
        outState.putSerializable("sources", new ArrayList<>(sources));
    }

    interface Callback {
        void onSuccess();
    }
}