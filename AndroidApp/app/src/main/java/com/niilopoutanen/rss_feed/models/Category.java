package com.niilopoutanen.rss_feed.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    public static final String c_TECH = "Tech";
    public static final String c_SPORTS = "Sports";
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

