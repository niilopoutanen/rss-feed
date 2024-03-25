package com.niilopoutanen.rss_feed.database;

public interface DatabaseThread<T> {
    void complete(T result);
}
