package com.niilopoutanen.rss_feed.parser;

public interface Callback<T> {
    void onResult(T result);

    void onError(RSSException exception);
}
