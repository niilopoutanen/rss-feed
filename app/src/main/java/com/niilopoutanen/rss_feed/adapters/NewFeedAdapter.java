package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.components.FeedItem;
import com.niilopoutanen.rss_feed.fragments.components.Header;
import com.niilopoutanen.rss_feed.fragments.components.NewFeedCard;
import com.niilopoutanen.rss_feed.fragments.components.Notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewFeedAdapter extends RecyclerView.Adapter<FeedItem.ViewHolder> {
    private final Context context;
    List<Post> posts = new ArrayList<>();
    List<String[]> notices = new ArrayList<>();
    public NewFeedAdapter(Context context){
        this.context = context;
    }
    public void update(List<Post> posts){
        this.posts = new ArrayList<>(posts);
        notifyDataSetChanged();
    }
    public void notify(String title, String desc){
        notices.add(new String[]{title, desc});
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FeedItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case Type.HEADER:
                return new FeedItem.ViewHolder(new Header(context)) ;

            case Type.POST:
                return new FeedItem.ViewHolder(new NewFeedCard(context));

            case Type.NOTICE:
                return new FeedItem.ViewHolder(new Notice(context));

            case Type.NULL:
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull FeedItem.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case Type.POST:
                holder.bind(posts.get(position - 1));
                break;
            case Type.NOTICE:
                holder.bind(notices.get(position - 1));
                break;
            case Type.HEADER:
                holder.bind(context.getString(R.string.feed_header));
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(notices.size() == 0){
            return posts.size() + 1;
        }
        return notices.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return Type.HEADER;
        }
        else if(notices.size() > 0){
            return Type.NOTICE;
        }
        else if(posts.size() > 0 && position < posts.size()){
            return Type.POST;
        }
        return Type.NULL;
    }


    public static class Type{
        final static int NULL = -1;
        final static int HEADER = 0;
        final static int POST = 1;
        final static int NOTICE = 2;

    }
}
