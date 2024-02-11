package com.niilopoutanen.rss_feed.sourcemanager;

public interface StateListener {
    void setContinueAllowed(boolean continueAllowed);
    void allowFinish();
}
