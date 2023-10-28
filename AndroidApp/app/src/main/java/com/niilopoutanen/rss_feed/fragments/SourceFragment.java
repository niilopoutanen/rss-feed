package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.SourceAdapter;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.models.WebCallBack;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.SourceValidator;

import java.util.List;
import java.util.Objects;

public class SourceFragment extends Fragment implements View.OnLongClickListener {

    private List<Source> sources;
    private SourceAdapter adapter;
    private Context appContext;
    private Preferences preferences;
    private RecyclerView sourcesRecyclerView;

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

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        postponeEnterTransition();

        update();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }
    }

    public void update(){
        sources = SaveSystem.loadContent(appContext);
        if (adapter != null) {
            adapter.updateSources(sources);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sources, container, false);
        sourcesRecyclerView = rootView.findViewById(R.id.sources_recyclerview);

        adapter = new SourceAdapter(sources, preferences, sourcesRecyclerView, this);
        sourcesRecyclerView.setAdapter(adapter);
        sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        startPostponedEnterTransition();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.new SwipeToDeleteCallback(getContext()));
        itemTouchHelper.attachToRecyclerView(sourcesRecyclerView);


        RelativeLayout addBtn = rootView.findViewById(R.id.addNewButton);
        addBtn.setOnClickListener(v -> openSourceDialog(null));
        return rootView;
    }

    public void openSourceDialog(Source source){
        AddSourceFragment addSourceFragment = new AddSourceFragment(source, appContext);
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
    public boolean onLongClick(View v) {
        int position = sourcesRecyclerView.getChildAdapterPosition(v);
        Source clickedSource = sources.get(position);
        openSourceDialog(clickedSource);
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }
}