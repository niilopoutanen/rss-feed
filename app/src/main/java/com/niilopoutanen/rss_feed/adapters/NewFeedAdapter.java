package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.components.FeedItem;
import com.niilopoutanen.rss_feed.fragments.components.Header;
import com.niilopoutanen.rss_feed.fragments.components.NewFeedCard;
import com.niilopoutanen.rss_feed.fragments.components.Notice;
import com.niilopoutanen.rss_feed.models.FeedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewFeedAdapter extends RecyclerView.Adapter<FeedItem.ViewHolder> {
    private final Context context;
    private final FeedData data = new FeedData();
    public NewFeedAdapter(Context context){
        this.context = context;
        data.setHeader(context.getString(R.string.feed_header));
    }
    public NewFeedAdapter(Context context, Source header){
        this.context = context;
        data.setHeader(header);
    }
    public void update(List<Post> newPosts) {
        data.setPosts(newPosts);
        notifyDataSetChanged();
    }

    public void update() {
        data.clearNotices();
        notifyDataSetChanged();
    }

    public void notify(String title, String desc) {
        data.addNotice(title, desc);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FeedItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case FeedData.Types.HEADER:
                return new FeedItem.ViewHolder(new Header(context)) ;

            case FeedData.Types.POST:
                return new FeedItem.ViewHolder(new NewFeedCard(context));

            case FeedData.Types.NOTICE:
                return new FeedItem.ViewHolder(new Notice(context));

            default:
                return new FeedItem.ViewHolder(new Notice(context));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FeedItem.ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.count();
    }

    @Override
    public int getItemViewType(int position) {
        return data.getItemType(position);
    }

}
