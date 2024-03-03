package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.SearchBar;
import com.niilopoutanen.rss_feed.fragments.components.feed.ExtendedHeader;
import com.niilopoutanen.rss_feed.fragments.components.feed.FeedCard;
import com.niilopoutanen.rss_feed.fragments.components.feed.FeedItem;
import com.niilopoutanen.rss_feed.fragments.components.feed.Header;
import com.niilopoutanen.rss_feed.fragments.components.feed.MessageBridge;
import com.niilopoutanen.rss_feed.fragments.components.feed.Notice;
import com.niilopoutanen.rss_feed.fragments.components.feed.FeedData;
import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedItem.ViewHolder> implements MessageBridge {
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
                Header header = new Header(context);
                header.setMessageBridge(FeedAdapter.this);
                return new FeedItem.ViewHolder(header);

            case FeedData.Types.HEADER_EXTENDED:
                ExtendedHeader extendedHeader = new ExtendedHeader(context);
                extendedHeader.setMessageBridge(FeedAdapter.this);
                return new FeedItem.ViewHolder(extendedHeader);

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

    @Override
    public void onQueryChanged(String query) {
        data.filter(query);
        notifyDataSetChanged();
    }

    @Override
    public void onSortingChanged() {
        data.changeDirection();
        notifyDataSetChanged();
    }
}
