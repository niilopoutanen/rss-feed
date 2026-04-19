package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialFadeThrough;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.database.AppViewModel;
import com.niilopoutanen.rss_feed.rss.Source;

public class SourceFragment extends Fragment {

    private SourceAdapter adapter;
    private Context context;
    private Preferences preferences;
    private RecyclerView sourcesRecyclerView;


    public SourceFragment() {}

    public static SourceFragment newInstance(Preferences preferences) {
        Bundle args = new Bundle();
        args.putSerializable("preferences", preferences);
        SourceFragment fragment = new SourceFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            preferences = (Preferences) getArguments().getSerializable("preferences");
        }

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        postponeEnterTransition();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);

        AppViewModel appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.getSources().observe(getViewLifecycleOwner(), sources -> {
            if (adapter != null) {
                adapter.updateSources(sources);
            }
        });
        FloatingActionButton addBtn = rootView.findViewById(R.id.addNewButton);


        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.sources_recyclerview), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets cameraInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets gestureInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());

            int defaultPadding = PreferencesManager.dpToPx(10, context);
            int rightMax = maxOf(insets.right, cameraInsets.right, gestureInsets.right, defaultPadding);
            int bottomMax = maxOf(insets.bottom, cameraInsets.bottom, gestureInsets.bottom);

            v.setPadding(maxOf(insets.left, defaultPadding, cameraInsets.left), maxOf(insets.top, cameraInsets.top, gestureInsets.top), maxOf(insets.right, defaultPadding, cameraInsets.right), 0);


            ViewGroup.MarginLayoutParams fabMlb = (ViewGroup.MarginLayoutParams) addBtn.getLayoutParams();
            fabMlb.rightMargin = rightMax;
            fabMlb.bottomMargin = bottomMax;

            return WindowInsetsCompat.CONSUMED;
        });

        sourcesRecyclerView = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(null, sourcesRecyclerView, getParentFragmentManager());
        sourcesRecyclerView.setAdapter(adapter);
        sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        //sourcesRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));

        startPostponedEnterTransition();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.new SwipeToDeleteCallback(getContext()));
        itemTouchHelper.attachToRecyclerView(sourcesRecyclerView);


        addBtn.setOnClickListener(v -> openSourceDialog(null));
        return rootView;
    }
    private int maxOf(int... values) {
        int max = Integer.MIN_VALUE;
        for (int v : values) {
            if (v > max) max = v;
        }
        return max;
    }
    public void openSourceDialog(Source source) {
        AddSourceFragment addSourceFragment = AddSourceFragment.newInstance(source);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, addSourceFragment, "source_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}