package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.io.InvalidObjectException;
import java.util.Map;

public abstract class FeedItem {
    private ViewGroup content;
    protected Context context;
    protected Preferences preferences;
    protected Object data;
    public FeedItem( Context context) {
        this.context = context;
        this.preferences = PreferencesManager.loadPreferences(context);
        inflate();
    }
    public ViewGroup getContent(){
        return this.content;
    }
    public abstract @LayoutRes int getLayoutResource();

    public abstract void onClick();
    private void inflate(){
        if(getLayoutResource() == -1) throw new IllegalArgumentException("No layout resource set");

        LayoutInflater inflater = LayoutInflater.from(context);
        content = (ViewGroup) inflater.inflate(getLayoutResource(), null, false);
        content.setOnClickListener(v -> FeedItem.this.onClick());
        setSpacing();
    }

    public void setData(Object data){
        this.data = data;
        bind(data);
    }
    public abstract void bind(Object data);

    protected void setSpacing(){
        int margin = PreferencesManager.dpToPx(10, context);
        int gap = PreferencesManager.dpToPx(20, context);

        boolean hasSideGap = context.getResources().getInteger(R.integer.feed_columns) > 1;

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!hasSideGap) {
            layoutParams.setMargins(0, 0, 0, gap);
        } else {
            layoutParams.setMargins(0, 0, margin, margin);
        }

        content.setLayoutParams(layoutParams);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final FeedItem feedItem;
        public ViewHolder(@NonNull FeedItem feedItem) {
            super(feedItem.getContent());
            this.feedItem = feedItem;
        }
        public void bind(Object data){
            feedItem.setData(data);
        }
    }

}
