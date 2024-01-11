package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.adapters.NewFeedAdapter;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Parser;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedFragment extends Fragment {
    private Context context;
    private Preferences preferences;
    private AppRepository repository;
    TextView title;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewFeedAdapter adapter;
    private List<Source> sources = new ArrayList<>();
    private FEED_TYPE type = FEED_TYPE.TYPE_MULTI;

    public FeedFragment() {
    }

    public FeedFragment(Preferences preferences) {
        this.preferences = preferences;
    }

    public FeedFragment(Preferences preferences, FEED_TYPE type){
        this.preferences = preferences;
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        repository = new AppRepository(context);
        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }


    public void showSingle(int id){
        if(repository == null){
            repository = new AppRepository(context);
        }

        repository.getSourceById(id).observe(this.getViewLifecycleOwner(), source -> {
            if(source == null)return;

            if (source.title != null && title != null){
                title.setText(source.title);
            }

            this.sources.clear();
            this.sources.add(source);
            update();
        });
    }
    public void showSingle(Source source){
        if(source == null)return;

        if (source.title != null && title != null){
            title.setText(source.title);
        }

        this.sources.clear();
        this.sources.add(source);
        update();
    }
    public void update() {
        if (!isValid(sources)) return;
        swipeRefreshLayout.setRefreshing(true);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Post> posts = Parser.loadMultiple(sources);
            ((Activity) context).runOnUiThread(() -> {
                adapter.update(posts);
                swipeRefreshLayout.setRefreshing(false);
            });
        });
    }

    private void init() {
        if(repository == null){
            repository = new AppRepository(context);
        }

        if (type == FEED_TYPE.TYPE_MULTI) {
            repository.getAllSources().observe(this.getViewLifecycleOwner(), sources -> {
                this.sources = sources;
                update();
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type == FEED_TYPE.TYPE_SINGLE && getArguments() != null) {
            int sourceId = getArguments().getInt("sourceId", 0);
            if(sourceId != 0){
                showSingle(sourceId);
                return;
            }
            Source source = (Source)getArguments().getSerializable("source");
            if(source != null){
                showSingle(source);
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        init();

        recyclerView = rootView.findViewById(R.id.feed_container);

        title = rootView.findViewById(R.id.feed_header);
        PreferencesManager.setHeader(context, title);

        ViewCompat.setOnApplyWindowInsetsListener(title, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });


        adapter = new NewFeedAdapter(context);
        recyclerView.setAdapter(adapter);

        final int columns = getResources().getInteger(R.integer.feed_columns);
        GridLayoutManager manager = new GridLayoutManager(rootView.getContext(), columns);
        recyclerView.setLayoutManager(manager);

        swipeRefreshLayout = rootView.findViewById(R.id.recyclerview_refresher);
        swipeRefreshLayout.setColorSchemeColors(PreferencesManager.getAccentColor(context));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));
        swipeRefreshLayout.setOnRefreshListener(this::update);

        return rootView;
    }

    private boolean isValid(List<Source> sources) {
        if (sources.size() == 0) {
            showError(0);
            return false;
        }
        if (!isAdded()) {
            return false;
        }
        ConnectivityManager connectionManager = context.getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectionManager.getActiveNetworkInfo();
        if (currentNetwork == null || !currentNetwork.isConnected()) {
            showError(1);
            return false;
        } else {
            return true;
        }
    }

    private void showError(int errorCode) {

        switch (errorCode) {
            case 429:
                adapter.notify(context.getString(R.string.error_too_many_requests), context.getString(R.string.error_too_many_requests_msg));
                break;
            case 0:
                adapter.notify(context.getString(R.string.error_no_sources), context.getString(R.string.error_no_sources_msg));
                break;
            case 1:
                adapter.notify(context.getString(R.string.error_no_internet), context.getString(R.string.error_no_internet_msg));
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
            default:
                adapter.notify(context.getString(R.string.error_invalid_feed), context.getString(R.string.error_invalid_feed_msg));
                break;
        }

        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
    }

    public void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }

    public enum FEED_TYPE {TYPE_SINGLE, TYPE_MULTI}
}
