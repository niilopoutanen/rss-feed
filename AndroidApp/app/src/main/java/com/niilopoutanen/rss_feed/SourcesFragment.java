package com.niilopoutanen.rss_feed;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.SaveSystem;
import com.niilopoutanen.rss_feed.sources.Source;
import com.niilopoutanen.rss_feed.sources.SourceAdapter;

public class SourcesFragment extends Fragment {

    private List<Source> sources;
    private SourceAdapter adapter;
    private Context appContext;
    private Preferences preferences;
    public SourcesFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }
    public SourcesFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(appContext == null){
            appContext = getContext();
        }
        sources = SaveSystem.loadSources(appContext);
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);
        RecyclerView sourcesContainer = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(sources, preferences, sourcesContainer);
        sourcesContainer.setAdapter(adapter);
        sourcesContainer.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.new SwipeToDeleteCallback(getContext()));
        itemTouchHelper.attachToRecyclerView(sourcesContainer);

        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }
}