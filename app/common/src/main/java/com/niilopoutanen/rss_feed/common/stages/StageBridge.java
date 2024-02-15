package com.niilopoutanen.rss_feed.common.stages;

public interface StageBridge {
    void onProgressLocked(boolean progressAllowed);
    void onLoadingStateChange(boolean isLoading);
}
