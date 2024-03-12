package com.niilopoutanen.rss_feed.fragments.components.feed;

public interface MessageBridge {
    void onQueryChanged(String query);
    void onSortingChanged(boolean newestFirst);
}
