package com.niilopoutanen.rss_feed.models;

import java.io.Serializable;
/**
 * Class for saving user's sources to disk
 */
public class Source implements Serializable {
    //Saved data version
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
