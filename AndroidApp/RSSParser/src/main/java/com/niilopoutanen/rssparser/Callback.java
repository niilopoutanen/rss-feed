package com.niilopoutanen.rssparser;

import com.niilopoutanen.rssparser.RSSException;

public interface Callback<T> {
    void onResult(T result);

    void onError(RSSException exception);
}
