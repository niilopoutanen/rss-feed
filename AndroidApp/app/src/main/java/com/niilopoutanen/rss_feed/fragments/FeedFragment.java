package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niilopoutanen.rss_feed.adapters.NewFeedAdapter;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.Item;
import com.niilopoutanen.rssparser.Parser;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.ArticleActivity;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FeedFragment extends Fragment implements RecyclerViewInterface {

    public static final int CARDMARGIN_DP = 10;
    public static final int CARDGAP_DP = 20;
    List<Source> sources = new ArrayList<>();
    List<Item> feed = new ArrayList<>();
    String viewTitle;
    RecyclerView recyclerView;
    NewFeedAdapter adapter;
    Context appContext;
    SwipeRefreshLayout recyclerviewRefresh;
    Preferences preferences;
    ExecutorService executor = null;

    //used to identify single source view
    private boolean singleView = false;
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
        this.singleView = true;
    }

    public FeedFragment() {
    }

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
        // Index out of bounds catch
        if (position >= feed.size()) {
            return;
        }
        Intent articleIntent = new Intent(appContext, ArticleActivity.class);
        articleIntent.putExtra("postUrl", feed.get(position).getLink());
        if (!preferences.s_feedcard_authorname) {
            articleIntent.putExtra("postPublisher", feed.get(position).getAuthor());
        } else {
            articleIntent.putExtra("postPublisher", feed.get(position).getAuthor());
        }
        articleIntent.putExtra("postPublishTime", feed.get(position).getPubDate());
        articleIntent.putExtra("title", feed.get(position).getTitle());
        articleIntent.putExtra("preferences", preferences);
        PreferencesManager.vibrate(recyclerView.getChildAt(0));
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
        if (sources.size() == 0) {
            showError(0, null);
            return false;
        }
        if (!isAdded()) {
            return false;
        }
        ConnectivityManager connectionManager = appContext.getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectionManager.getActiveNetworkInfo();
        if (currentNetwork == null || !currentNetwork.isConnected()) {
            showError(1, null);
            return false;
        } else {
            return true;
        }
    }

    private void updateFeed() {
        if (!checkValidity()) {
            recyclerviewRefresh.setRefreshing(false);
            Feed temp = new Feed();
            temp.setItems(feed);
            adapter.update(temp);
            return;
        }
        //if all sources are hidden, show the title
        if (sources.stream().noneMatch(Source::isVisibleInFeed)) {
            if (!singleView) {
                recyclerviewRefresh.setRefreshing(false);
                Feed temp = new Feed();
                temp.setItems(feed);
                adapter.update(temp);
            }

        }

        recyclerviewRefresh.setRefreshing(true);
        feed.clear();

        // Create a new Executor for running the feed updates on a background thread
        executor = Executors.newSingleThreadExecutor();

        // Submit each update to the executor
        executor.execute(() -> {
            for (Source source : sources) {
                if (!source.isVisibleInFeed() && !singleView) {
                    continue;
                }
                Parser parser = new Parser();
                try {
                    Feed loadedFeed = parser.load(source.getFeedUrl());
                    for (Item item : loadedFeed.getItems()) {
                        // Handle the case where the fragment is no longer active
                        if (!isAdded()) {
                            break;
                        }

                        requireActivity().runOnUiThread(() -> feed.add(item));

                    }
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> Collections.sort(feed));
                    }
                } catch (RSSException e) {
                    showError(e.getErrorType(), source);
                }


            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Feed temp = new Feed();
                    temp.setItems(feed);
                    adapter.update(temp);
                    recyclerView.scheduleLayoutAnimation();
                    recyclerviewRefresh.setRefreshing(false);
                });
            }
        });

        executor = null;
    }


    private void showError(int errorCode, Source errorCause) {
        boolean sourceAlertHidden = PreferencesManager.loadPreferences(appContext).s_hide_sourcealert;
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(appContext);
        dialog.setNegativeButton(appContext.getString(R.string.close), (dialog1, which) -> dialog1.dismiss());

        switch (errorCode) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                dialog.setTitle(appContext.getString(R.string.invalidfeed));
                dialog.setMessage(appContext.getString(R.string.invalidfeedmsg) + " " + errorCause.getFeedUrl());
                dialog.setPositiveButton(appContext.getString(R.string.tryagain), (dialog1, which) -> {
                    dialog1.dismiss();
                    updateFeed();
                });
                break;

            case 429:
                dialog.setTitle(appContext.getString(R.string.errro_toomanyrequests));
                dialog.setMessage(String.format(appContext.getString(R.string.toomanyrequestsmsg), errorCause.getFeedUrl()));
                break;
            case 0:
                dialog.setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());
                dialog.setTitle(appContext.getString(R.string.nosources));
                dialog.setMessage(appContext.getString(R.string.nosourcesmsg));
                if (sourceAlertHidden) {
                    return;
                }
                dialog.setNegativeButton(appContext.getString(R.string.donot_show_again), (dialog1, which) -> {
                    dialog1.dismiss();
                    PreferencesManager.saveBooleanPreference(Preferences.SP_HIDE_SOURCE_ALERT, Preferences.PREFS_FUNCTIONALITY, true, appContext);
                });
                break;

            case 1:
                dialog.setTitle(appContext.getString(R.string.nointernet));
                dialog.setMessage(appContext.getString(R.string.nointernetmsg));
                dialog.setPositiveButton(appContext.getString(R.string.tryagain), (dialog1, which) -> {
                    dialog1.dismiss();
                    updateFeed();
                });
                break;
        }
        Activity activity = (Activity) appContext;
        if (!activity.isFinishing()) {
            activity.runOnUiThread(dialog::show);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = rootView.findViewById(R.id.feed_container);

        if (viewTitle == null && appContext != null) {
            viewTitle = appContext.getString(R.string.feed_header);
        } else if (viewTitle == null) {
            viewTitle = "Feed";
        }
        if (viewTitle.length() > 20) {
            viewTitle = viewTitle.substring(0, 20) + "...";
        }
        Feed feedTemp = new Feed();
        feedTemp.setItems(feed);
        adapter = new NewFeedAdapter(feedTemp, appContext, preferences);
        recyclerView.setAdapter(adapter);
        final int columns = getResources().getInteger(R.integer.feed_columns);
        GridLayoutManager manager = new GridLayoutManager(rootView.getContext(), columns);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0) {
                    return columns;
                }
                else{
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);

        recyclerviewRefresh = rootView.findViewById(R.id.recyclerview_refresher);
        recyclerviewRefresh.setColorSchemeColors(colorAccent);
        recyclerviewRefresh.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));
        recyclerviewRefresh.setOnRefreshListener(this::updateFeed);

        updateFeed();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
        outState.putSerializable("sources", new ArrayList<>(sources));
    }

    @Override
    public void onPause() {
        //stop the thread loading when exiting the fragment
        super.onPause();
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    enum ERROR_TYPES {
        NOSOURCES, NOINTERNET, INVALIDTYPE
    }
}