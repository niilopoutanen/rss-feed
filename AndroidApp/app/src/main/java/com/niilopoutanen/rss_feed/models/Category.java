package com.niilopoutanen.rss_feed.models;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private String name;
    private String imageUrl;

    public Category(String name) {
        this.name = name;
        this.imageUrl = null;
    }
    public Category(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("img", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

