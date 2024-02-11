package com.niilopoutanen.rss_feed.parser;

public interface StateCallback {
    public void onStatusUpdate(StateManager.StatusMessage statusMessage);
}
