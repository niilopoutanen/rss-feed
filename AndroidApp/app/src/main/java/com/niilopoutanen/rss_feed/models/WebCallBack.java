package com.niilopoutanen.rss_feed.models;

public interface WebCallBack<T> {
    void onResult(T result);
}
