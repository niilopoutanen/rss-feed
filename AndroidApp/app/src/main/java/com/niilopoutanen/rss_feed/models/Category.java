package com.niilopoutanen.rss_feed.models;

/**
 * Category class for parsing RSS categories from GitHub
 */
public class Category {
    private final String name;
    private final String imageUrl;
    private final String query;

    public Category(String name, String imageUrl, String query) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

