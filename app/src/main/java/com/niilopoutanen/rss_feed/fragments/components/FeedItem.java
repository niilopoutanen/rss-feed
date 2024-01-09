package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.io.InvalidObjectException;

public abstract class FeedItem {
    private ViewGroup content;
    protected Context context;
    protected Preferences preferences;
    public FeedItem( Context context) {
        this.context = context;
        this.preferences = PreferencesManager.loadPreferences(context);
        inflate();
    }
    public ViewGroup getContent(){
        return this.content;
    }
    public abstract @LayoutRes int getLayoutResource();


    private void inflate(){
        if(getLayoutResource() == -1) throw new IllegalArgumentException("No layout resource set");

        LayoutInflater inflater = LayoutInflater.from(context);
        content = (ViewGroup) inflater.inflate(getLayoutResource(), null, false);
    }

    public abstract void bind(Post post);
}
