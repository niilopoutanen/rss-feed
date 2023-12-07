package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
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
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;
import com.niilopoutanen.rss_feed.database.AppDatabase;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.utils.SaveSystem;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SourceFragment extends Fragment {

    private List<Source> sources;
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

        update();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }

    public void update() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(context);
            sources = database.sourceDao().getAll();
            ((Activity)context).runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.updateSources(sources);
                }
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.sources_container), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        sourcesRecyclerView = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(sources, sourcesRecyclerView, getParentFragmentManager());
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



    @Override
    public void onResume() {
        super.onResume();
        update();
    }
}