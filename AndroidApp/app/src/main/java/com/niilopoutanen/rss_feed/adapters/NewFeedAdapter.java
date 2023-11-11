package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.fragments.FeedCard;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.Item;

public class NewFeedAdapter extends RecyclerView.Adapter<FeedCard> {

    private Feed feed;
    private final Context context;
    private final Preferences preferences;
    public NewFeedAdapter(Feed feed, Context context, Preferences preferences){
        this.feed = feed;
        this.context = context;
        this.preferences = preferences;
    }
    public void update(Feed feed){
        this.feed = feed;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FeedCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return FeedCard.create(parent, preferences);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedCard holder, int position) {
        holder.bindData(feed.getItemAt(position));
    }

    @Override
    public int getItemCount() {
        if(feed == null){
            return 0;
        }
        return feed.getItems().size();
    }
}
