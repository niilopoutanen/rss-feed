package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialFadeThrough;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.database.AppRepository;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.rss.Source;
import com.niilopoutanen.rss_feed.common.PreferencesManager;

public class SourceFragment extends Fragment {

    private SourceAdapter adapter;
    private Context context;
    private Preferences preferences;
    private RecyclerView sourcesRecyclerView;

    public SourceFragment(Context context, Preferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    public SourceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            context = getContext();
        }

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        postponeEnterTransition();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);

        AppRepository repository = new AppRepository(context);
        repository.getAllSources().observe(getViewLifecycleOwner(), sources -> {
            if (adapter != null) {
                adapter.updateSources(sources);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.sources_container), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        PreferencesManager.setHeader(context, rootView.findViewById(R.id.sources_header));
        sourcesRecyclerView = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(null, sourcesRecyclerView, getParentFragmentManager());
        sourcesRecyclerView.setAdapter(adapter);
        sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        sourcesRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));

        startPostponedEnterTransition();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.new SwipeToDeleteCallback(getContext()));
        itemTouchHelper.attachToRecyclerView(sourcesRecyclerView);


        RelativeLayout addBtn = rootView.findViewById(R.id.addNewButton);
        addBtn.setOnClickListener(v -> openSourceDialog(null));
        return rootView;
    }

    public void openSourceDialog(Source source) {
        AddSourceFragment addSourceFragment = new AddSourceFragment(source, context);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, addSourceFragment, "source_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("preferences", preferences);
    }
}