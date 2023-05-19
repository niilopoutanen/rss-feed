package com.niilopoutanen.rss_feed.sources;

import java.io.Serializable;

public class Source implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String feedUrl;
    private final String imageUrl;

    public Source(String name, String feedUrl, String imageUrl) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
