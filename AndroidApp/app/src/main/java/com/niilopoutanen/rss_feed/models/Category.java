package com.niilopoutanen.rss_feed.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
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

