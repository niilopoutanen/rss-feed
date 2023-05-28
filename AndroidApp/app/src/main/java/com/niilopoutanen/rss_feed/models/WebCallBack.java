package com.niilopoutanen.rss_feed.models;

/**
 * Callback to return data
 */
public interface WebCallBack<T> {
    void onResult(T result);
}
