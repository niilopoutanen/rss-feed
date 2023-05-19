package com.niilopoutanen.rss_feed.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Publisher {
    private String name;
    private String url;
    private int categoryId;

    public Publisher(String name, String url, int categoryId) {
        this.name = name;
        this.url = url;
        this.categoryId = categoryId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("url", url);
            json.put("categoryId", categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
