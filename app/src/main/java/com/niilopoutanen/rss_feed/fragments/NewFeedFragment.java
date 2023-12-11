package com.niilopoutanen.rss_feed.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.niilopoutanen.rss_feed.activities.ArticleActivity;
import com.niilopoutanen.rss_feed.adapters.FeedAdapter;
import com.niilopoutanen.rss_feed.database.AppDatabase;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.Parser;
import com.niilopoutanen.rssparser.RSSException;

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


    }


    public void update(){
        adapter.update(posts);
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        AppDatabase database = AppDatabase.getInstance(context);
        database.sourceDao().getAll().observe(this.getViewLifecycleOwner(), sources -> {
            Parser parser = new Parser();
            swipeRefreshLayout.setRefreshing(true);
            parser.get(sources, new Callback<List<Post>>() {
                @Override
                public void onResult(List<Post> result) {
                    posts = result;
                    ((Activity)context).runOnUiThread(() -> update());
                }

                @Override
                public void onError(RSSException exception) {
                    exception.printStackTrace();
                }
            });
        });

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

}
