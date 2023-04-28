package com.niilopoutanen.rss_feed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.niilopoutanen.rss_feed.rss.RSSPost;
import com.niilopoutanen.rss_feed.rss.RSSParser;
import com.niilopoutanen.rss_feed.rss.FeedAdapter;
import com.niilopoutanen.rss_feed.sources.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.sources.Source;
import com.niilopoutanen.rss_feed.web.WebCallBack;
import com.niilopoutanen.rss_feed.web.WebHelper;


public class FeedFragment extends Fragment implements RecyclerViewInterface {

    List<Source> sources = new ArrayList<>();
    List<RSSPost> feed = new ArrayList<>();
    String viewTitle;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    Context appContext;
    SwipeRefreshLayout recyclerviewRefresh;
    Preferences preferences;

    @ColorInt int colorAccent;
    public FeedFragment(List<Source> sources, Preferences preferences) {
        this.sources = sources;
        this.preferences = preferences;
    }
    public FeedFragment(Source source, Preferences preferences){
        //single source view
        sources.add(source);
        viewTitle = source.getName();
        this.preferences = preferences;
    }
    public FeedFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getContext();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
            sources = (List<Source>) savedInstanceState.getSerializable("sources");
        }

        assert appContext != null;
        colorAccent = PreferencesManager.getAccentColor(appContext);

    }

    @Override
    public void onItemClick(int position) {
        Intent articleIntent = new Intent(appContext, ArticleActivity.class);
        articleIntent.putExtra("postUrl", feed.get(position).getPostLink());
        if(!preferences.s_feedcard_authorname){
            articleIntent.putExtra("postPublisher", feed.get(position).getAuthor());
        }
        else{
            articleIntent.putExtra("postPublisher", feed.get(position).getSourceName());
        }
        articleIntent.putExtra("postPublishTime", feed.get(position).getPublishTime());

        articleIntent.putExtra("preferences", preferences);
        appContext.startActivity(articleIntent);
    }

    @Override
    public void onItemLongClick(int position) {

    }
    public void scrollToTop(){
        if(recyclerView != null){
            recyclerView.smoothScrollToPosition(0);
        }
    }
    interface Callback {
        void onSuccess();
    }
    private boolean checkValidity(){
        if(sources.size() <= 0){
            Toast.makeText(appContext, appContext.getString(R.string.nosources), Toast.LENGTH_LONG).show();
            return false;
        }

        ConnectivityManager connectionManager = appContext.getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectionManager.getActiveNetworkInfo();
        if(currentNetwork == null || !currentNetwork.isConnected()){
            Toast.makeText(appContext, appContext.getString(R.string.nointernet), Toast.LENGTH_LONG).show();
            return false;
        }

        else {
            return true;
        }
    }
    private void updateFeed(final Callback callback) {
        if(recyclerviewRefresh != null){
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

                        Collections.sort(feed);
                    }
                });

            }
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

        if(viewTitle == null && appContext != null){
            viewTitle = appContext.getString(R.string.feed_header);
        }
        else if(viewTitle == null){
            viewTitle = "Feed";
        }
        if (viewTitle.length() > 20) {
            viewTitle = viewTitle.substring(0, 20) + "...";
        }
        adapter = new FeedAdapter(preferences ,feed, appContext, viewTitle, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));



        recyclerviewRefresh = rootView.findViewById(R.id.recyclerview_refresher);
        recyclerviewRefresh.setColorSchemeColors(colorAccent);
        recyclerviewRefresh.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));
        recyclerviewRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed(new Callback() {
                    @Override
                    public void onSuccess() {
                        if(adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                        recyclerviewRefresh.setRefreshing(false);
                    }
                });

            }
        });


        boolean canLoad = checkValidity();
        if(canLoad){
            updateFeed(new Callback() {
                @Override
                public void onSuccess() {
                    adapter.notifyDataSetChanged();
                    recyclerviewRefresh.setRefreshing(false);

                }
            });
        }


        return rootView;
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FeedAdapter adapter = (FeedAdapter)recyclerView.getAdapter();
        if(adapter != null){
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
}