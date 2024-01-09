package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.fragments.components.FeedItem;
import com.niilopoutanen.rss_feed.fragments.components.NewFeedCard;

import java.util.ArrayList;
import java.util.List;

public class NewFeedAdapter extends RecyclerView.Adapter<FeedItem.ViewHolder> {
    private final Context context;
    List<Post> posts = new ArrayList<>();
    public NewFeedAdapter(Context context){
        this.context = context;
    }
    public void update(List<Post> posts){
        this.posts = new ArrayList<>(posts);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FeedItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedItem.ViewHolder(new NewFeedCard(context));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedItem.ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
