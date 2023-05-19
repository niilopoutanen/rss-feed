package com.niilopoutanen.rss_feed.models;

public class Publisher {
    private String name;
    private String url;
    private Category category;

    public Publisher(String name, String url, Category category) {
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
