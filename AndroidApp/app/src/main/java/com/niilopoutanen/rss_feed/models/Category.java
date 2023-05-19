package com.niilopoutanen.rss_feed.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private String name;
    private int id;

    public Category(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

