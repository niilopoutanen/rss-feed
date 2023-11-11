package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.fragments.FeedCard;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.Item;

import java.util.ArrayList;
import java.util.List;

public class NewFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_NOTICE = 2;
    private Feed feed;
    private final List<String> notices = new ArrayList<>();
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
    public void addNotification(String text){
        notices.add(text);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return FeedCard.create(parent, preferences);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FeedCard){
            ((FeedCard)holder).bindData(feed.getItemAt(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        else if (notices.size() > 0 && feed.getItemCount() == 0){
            return TYPE_NOTICE;
        }
        else {
            return TYPE_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        if(feed == null){
            return 0;
        }
        return feed.getItemCount();
    }
}