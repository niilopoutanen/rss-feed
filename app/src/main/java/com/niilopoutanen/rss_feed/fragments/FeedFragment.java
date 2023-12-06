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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.ArticleActivity;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.Parser;
import com.niilopoutanen.rssparser.RSSException;

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
    List<Post> posts = new ArrayList<>();
    String viewTitle;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    Context context;
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
        context = getContext();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
            sources = (List<Source>) savedInstanceState.getSerializable("sources");
        }

        colorAccent = PreferencesManager.getAccentColor(context);

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public void onItemClick(int position) {
        // Index out of bounds catch
        if (position >= posts.size()) {
            return;
        }
        Post clicked = posts.get(position);
        if(clicked.link != null){
            Intent articleIntent = new Intent(context, ArticleActivity.class);
            articleIntent.putExtra("preferences", preferences);
            articleIntent.putExtra("post", posts.get(position));

            PreferencesManager.vibrate(recyclerView.getChildAt(0));
            context.startActivity(articleIntent);
        }
        else{
            Toast.makeText(context, R.string.error_post_no_url, Toast.LENGTH_LONG).show();
        }

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
        ConnectivityManager connectionManager = context.getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectionManager.getActiveNetworkInfo();
        if (currentNetwork == null || !currentNetwork.isConnected()) {
            showError(1, null);
            return false;
        } else {
            return true;
        }
    }

    private void updateFeed() {
        List<Post> loadedItems = new ArrayList<>();
        if (!checkValidity()) {
            recyclerviewRefresh.setRefreshing(false);
            return;
        }

        recyclerviewRefresh.setRefreshing(true);

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
                    Feed feed = parser.load(source.getFeedUrl());
                    loadedItems.addAll(feed.getPosts());
                } catch (RSSException e) {
                    Activity activity = getActivity();
                    if(activity != null && isAdded()){
                        activity.runOnUiThread(() -> showError(e.getErrorType(), source));
                        return;
                    }

                }


            }
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    Collections.sort(loadedItems);
                    posts.clear();
                    posts.addAll(loadedItems);
                    adapter.update();
                    recyclerView.scheduleLayoutAnimation();
                    recyclerviewRefresh.setRefreshing(false);
                });
            }
        });

        executor = null;
    }


    private void showError(int errorCode, Source errorCause) {
        switch (errorCode) {
            case 429:
                adapter.addNotification(context.getString(R.string.error_too_many_requests), String.format(context.getString(R.string.error_too_many_requests_msg), errorCause.getFeedUrl()));
                break;
            case 0:
                adapter.addNotification(context.getString(R.string.error_no_sources), context.getString(R.string.error_no_sources_msg));
                break;
            case 1:
                adapter.addNotification(context.getString(R.string.error_no_internet), context.getString(R.string.error_no_internet_msg));
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
            default:
                adapter.addNotification(context.getString(R.string.error_invalid_feed), context.getString(R.string.error_invalid_feed_msg));
                break;
        }
        if(recyclerviewRefresh != null) recyclerviewRefresh.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = rootView.findViewById(R.id.feed_container);

        if (viewTitle == null && context != null) {
            viewTitle = context.getString(R.string.feed_header);
        } else if (viewTitle == null) {
            viewTitle = "Feed";
        }
        TextView toolBarTitle = rootView.findViewById(R.id.feed_header);
        toolBarTitle.setText(viewTitle);

        ViewCompat.setOnApplyWindowInsetsListener(toolBarTitle, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


        adapter = new FeedAdapter(posts, context, preferences, this);
        recyclerView.setAdapter(adapter);
        
        final int columns = getResources().getInteger(R.integer.feed_columns);
        GridLayoutManager manager = new GridLayoutManager(rootView.getContext(), columns);
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
}