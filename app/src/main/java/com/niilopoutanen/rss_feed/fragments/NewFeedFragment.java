package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.database.AppDatabase;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class NewFeedFragment extends Fragment implements RecyclerViewInterface {
    private Context context;
    private Preferences preferences;
    private int colorAccent;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    public NewFeedFragment(){}
    public NewFeedFragment(Preferences preferences){
        this.preferences = preferences;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        if (savedInstanceState != null) {
            preferences = (Preferences) savedInstanceState.getSerializable("preferences");
        }

        colorAccent = PreferencesManager.getAccentColor(context);

        setEnterTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));

        AppDatabase database = AppDatabase.getInstance(context);
        database.sourceDao().getAll().observe(this.getViewLifecycleOwner(), sources -> {
            // Load here and set posts
        });
    }

    public void update(){
        adapter.update();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = rootView.findViewById(R.id.feed_container);

        TextView toolBarTitle = rootView.findViewById(R.id.feed_header);
        //toolBarTitle.setText(viewTitle);

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

        swipeRefreshLayout = rootView.findViewById(R.id.recyclerview_refresher);
        swipeRefreshLayout.setColorSchemeColors(colorAccent);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(rootView.getContext().getColor(R.color.element));
        swipeRefreshLayout.setOnRefreshListener(this::update);

        update();
        return rootView;
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}
