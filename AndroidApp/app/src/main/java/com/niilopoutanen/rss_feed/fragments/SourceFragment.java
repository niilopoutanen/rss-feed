package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;

import java.util.List;

public class SourceFragment extends Fragment {

    private List<Source> sources;
    private SourceAdapter adapter;
    private Context appContext;
    private Preferences preferences;

    public SourceFragment(Context context, Preferences preferences) {
        this.appContext = context;
        this.preferences = preferences;
    }

    public SourceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (appContext == null) {
            appContext = getContext();
        }
        sources = SaveSystem.loadContent(appContext);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
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