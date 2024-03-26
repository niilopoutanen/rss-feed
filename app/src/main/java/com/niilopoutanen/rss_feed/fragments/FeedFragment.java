package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.database.AppViewModel;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedFragment extends Fragment {
    private Context context;
    private AppRepository repository;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedAdapter adapter;
    private List<Source> sources = new ArrayList<>();
    private AppViewModel appViewModel;
    private FEED_TYPE type = FEED_TYPE.TYPE_MULTI;

    public FeedFragment() {}

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }
    public static FeedFragment newInstance(int sourceId, Source source) {
        Bundle args = new Bundle();
        args.putInt("id", sourceId);
        args.putSerializable("source", source);
        args.putSerializable("type", FEED_TYPE.TYPE_SINGLE);
        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        repository = new AppRepository(context);
        if (getArguments() != null) {
            type = (FEED_TYPE) getArguments().getSerializable("type");
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
            adapter.setHeader(source);
            this.sources.clear();
            this.sources.add(source);
            update();
        });
    }
    public void showSingle(Source source){
        if(source == null)return;
        adapter.setHeader(source);
        this.sources.clear();
        this.sources.add(source);
        update();
    }
    public void update() {
        if (!isValid(sources)) return;
        swipeRefreshLayout.setRefreshing(true);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if(appViewModel.getPostCache() != null){
                adapter.update(appViewModel.getPostCache());
            }
            List<Post> posts;
            if(type == FEED_TYPE.TYPE_SINGLE){
                Parser parser = new Parser();
                parser.load(sources.get(0).url);
                posts = new ArrayList<>(parser.posts);
            }
            else{
                posts = Parser.loadMultiple(sources);
            }
            if(appViewModel.isCacheOutdated(posts)){
                ((Activity) context).runOnUiThread(() -> {
                    appViewModel.setPostCache(posts);
                    adapter.update(posts);
                });
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        Bundle params = new Bundle();
        params.putString("source_count", String.valueOf(sources.size()));
        FirebaseAnalytics.getInstance(context).logEvent("load_feed", params);
    }

    private void init() {
        if(repository == null){
            repository = new AppRepository(context);
        }
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        if (type == FEED_TYPE.TYPE_MULTI) {
            appViewModel.getSources().observe(getViewLifecycleOwner(), sources -> {
                FeedFragment.this.sources = sources;
                update();
            });
            /*
            repository.getAllSources().observe(this.getViewLifecycleOwner(), sources -> {
                this.sources = sources;
                update();
            });*/
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type == FEED_TYPE.TYPE_SINGLE && getArguments() != null) {
            int sourceId = getArguments().getInt("id", 0);
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
        swipeRefreshLayout = rootView.findViewById(R.id.recyclerview_refresher);
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    insets.top,
                    recyclerView.getPaddingRight(),
                    recyclerView.getPaddingBottom());

            swipeRefreshLayout.setProgressViewOffset(false, 0,insets.top);
            return WindowInsetsCompat.CONSUMED;
        });


        adapter = new FeedAdapter(context);
        recyclerView.setAdapter(adapter);

        final int columns = getResources().getInteger(R.integer.feed_columns);
        GridLayoutManager manager = new GridLayoutManager(rootView.getContext(), columns);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? columns : 1;
            }
        });
        recyclerView.setLayoutManager(manager);

        swipeRefreshLayout.setColorSchemeColors(PreferencesManager.getAccentColor(context));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));
        swipeRefreshLayout.setOnRefreshListener(this::update);


        return rootView;
    }

    private boolean isValid(List<Source> sources) {
        if (sources.isEmpty()) {
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

    public enum FEED_TYPE {TYPE_SINGLE, TYPE_MULTI}
}
