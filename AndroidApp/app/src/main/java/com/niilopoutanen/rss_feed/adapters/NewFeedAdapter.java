package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.FeedCard;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_NOTICE = 2;
    private Feed feed;
    private final Map<String, String> notices = new HashMap<>();
    private final Context context;
    private final Preferences preferences;
    private final RecyclerViewInterface recyclerViewInterface;
    public NewFeedAdapter(Feed feed, Context context, Preferences preferences, RecyclerViewInterface recyclerViewInterface){
        this.feed = feed;
        this.context = context;
        this.preferences = preferences;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    public void update(Feed feed){
        this.feed = feed;
        notifyDataSetChanged();
    }
    public void addNotification(String text, String desc){
        feed = null;
        notices.clear();
        notices.put(text, desc);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_NOTICE){
            return createNotice();
        }
        else{
            return FeedCard.create(parent, preferences, recyclerViewInterface);
        }

    }

    private RecyclerView.ViewHolder createNotice(){
        LinearLayout container = new LinearLayout(context);

        TextView textView = new TextView(context);
        textView.setTextColor(context.getColor(R.color.textSecondary));
        textView.setTextSize(18);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_semibold));

        TextView textDesc = new TextView(context);
        textDesc.setTextColor(context.getColor(R.color.textSecondary));
        textDesc.setTextSize(15);
        textDesc.setTypeface(ResourcesCompat.getFont(context, R.font.inter));

        container.addView(textView);
        container.addView(textDesc);
        return new RecyclerView.ViewHolder(container) {};
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FeedCard){
            ((FeedCard)holder).bindData(feed.getItemAt(position));
        }
        else{
            LinearLayout container = (LinearLayout) holder.itemView;
            container.setOrientation(LinearLayout.VERTICAL);
            Map.Entry<String,String> entry = notices.entrySet().iterator().next();

            TextView textView = (TextView)container.getChildAt(0);
            textView.setText(entry.getKey());

            TextView textDesc = (TextView)container.getChildAt(1);
            textDesc.setText(entry.getValue());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notices.size() > 0 && feed == null){
            return TYPE_NOTICE;
        }
        else {
            return 1;
        }
    }
    @Override
    public int getItemCount() {
        if (notices.size() > 0) {
            return notices.size();
        }
        else if(feed == null){
            return 0;
        }
        return feed.getItemCount();
    }
}
