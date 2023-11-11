package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.fragments.FeedCard;
import com.niilopoutanen.rss_feed.fragments.FeedCard.FeedCardViewHolder;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.Item;

public class NewFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Feed feed;
    private final Context context;
    private final Preferences preferences;
    public NewFeedAdapter(Feed feed, Context context, Preferences preferences){
        this.feed = feed;
        this.context = context;
        this.preferences = preferences;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FeedCard feedCard = new FeedCard(preferences.s_feedcardstyle, context);

        return new FeedCardViewHolder(feedCard);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedCardViewHolder) {
            FeedCardViewHolder feedCardViewHolder = (FeedCardViewHolder) holder;
            Item item = feed.getItemAt(position);
            feedCardViewHolder.bindData(item);
        }
    }

    @Override
    public int getItemCount() {
        return feed != null ? feed.getItems().size() : 0;
    }
}
