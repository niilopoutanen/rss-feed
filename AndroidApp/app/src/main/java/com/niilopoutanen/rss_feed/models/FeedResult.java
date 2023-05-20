package com.niilopoutanen.rss_feed.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedResult {
    public long lastUpdated;
    public double score;
    public String language;
    public String description;
    public String id;
    public String title;
    public String feedId;
    public String website;
    public int subscribers;
    public double velocity;
    public long updated;
    public String iconUrl;
    public boolean partial;
    public String coverUrl;
    public String visualUrl;
    public int estimatedEngagement;
    public ArrayList<String> topics;
    public String twitterScreenName;
    public int twitterFollowers;
    public static List<FeedResult> parseResult(String result){
        List<FeedResult> results = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                FeedResult tempObj = new FeedResult();
                tempObj.lastUpdated = resultObject.getLong("lastUpdated");
                tempObj.score = resultObject.getDouble("score");
                tempObj.language = resultObject.getString("language");
                tempObj.description = resultObject.getString("description");
                tempObj.id = resultObject.getString("id");
                tempObj.title = resultObject.getString("title");
                tempObj.feedId = resultObject.getString("feedId");
                tempObj.website = resultObject.getString("website");
                tempObj.subscribers = resultObject.getInt("subscribers");
                tempObj.velocity = resultObject.getDouble("velocity");
                tempObj.updated = resultObject.getLong("updated");
                tempObj.iconUrl = resultObject.getString("iconUrl");
                tempObj.partial = resultObject.getBoolean("partial");
                tempObj.coverUrl = resultObject.optString("coverUrl", null);
                tempObj.visualUrl = resultObject.getString("visualUrl");
                tempObj.estimatedEngagement = resultObject.getInt("estimatedEngagement");

                if (resultObject.has("topics")) {
                    JSONArray topicsArray = resultObject.getJSONArray("topics");
                    tempObj.topics = new ArrayList<>();
                    for (int j = 0; j < topicsArray.length(); j++) {
                        String topic = topicsArray.getString(j);
                        tempObj.topics.add(topic);
                    }
                }
                results.add(tempObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
