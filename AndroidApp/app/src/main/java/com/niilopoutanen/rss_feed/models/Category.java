package com.niilopoutanen.rss_feed.models;

public class Category {
    private String name;
    private String imageUrl;
    private String query;

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

