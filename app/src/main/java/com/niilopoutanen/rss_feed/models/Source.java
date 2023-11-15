package com.niilopoutanen.rss_feed.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class for saving user's sources to disk
 */
public class Source implements Serializable {
    //Saved data version
    private static final long serialVersionUID = 1L;
    private UUID id;
    private final String name;
    private final String feedUrl;
    private final String imageUrl;
    private final Boolean showInFeed;

    public Source(String name, String feedUrl, String imageUrl) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = true;
        this.id = UUID.randomUUID();
    }

    public Source(String name, String feedUrl, String imageUrl, boolean showInFeed) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = showInFeed;
        this.id = UUID.randomUUID();
    }

    public Source(String name, String feedUrl, String imageUrl, boolean showInFeed, UUID id) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = showInFeed;
        this.id = id;
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

    public boolean isVisibleInFeed() {
        return this.showInFeed == null || this.showInFeed;

    }

    public UUID getId() {
        return this.id;
    }

    public void generateId() {
        this.id = UUID.randomUUID();
    }
}
