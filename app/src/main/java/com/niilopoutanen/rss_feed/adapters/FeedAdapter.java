package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.fragments.components.FeedItem;
import com.niilopoutanen.rss_feed.fragments.components.Header;
import com.niilopoutanen.rss_feed.fragments.components.FeedCard;
import com.niilopoutanen.rss_feed.fragments.components.Notice;
import com.niilopoutanen.rss_feed.fragments.components.ExtendedHeader;
import com.niilopoutanen.rss_feed.models.FeedData;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedItem.ViewHolder> {
    private final Context context;
    private final FeedData data = new FeedData();
    public FeedAdapter(Context context){
        this.context = context;
        data.setHeader(context.getString(R.string.feed_header));
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
    public void setHeader(Source header){
        data.setHeader(header);
    }
    @NonNull
    @Override
    public FeedItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case FeedData.Types.HEADER:
                return new FeedItem.ViewHolder(new Header(context));

            case FeedData.Types.HEADER_SOURCE:
                return new FeedItem.ViewHolder(new ExtendedHeader(context));

            case FeedData.Types.POST:
                return new FeedItem.ViewHolder(new FeedCard(context));

            case FeedData.Types.NOTICE:
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
