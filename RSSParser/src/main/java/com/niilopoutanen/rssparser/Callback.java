package com.niilopoutanen.rssparser;

public interface Callback<T> {
    void onResult(T result);

    void onError(RSSException exception);
}
