package com.niilopoutanen.rss_feed.parser;

import androidx.annotation.StringRes;

public interface StatusBridge {
    public void onProgress(String msg);
    public void onProgress(@StringRes int stringRes);
    public void onSuccess(String msg);
    public void onSuccess(@StringRes int stringRes);
    public void onFailure(String msg);
    public void onFailure(@StringRes int stringRes);
}
