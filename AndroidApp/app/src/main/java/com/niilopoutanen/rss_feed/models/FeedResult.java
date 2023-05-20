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
    public static List<FeedResult> parseResult(String result) {
        List<FeedResult> results = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                FeedResult tempObj = new FeedResult();

                if (resultObject.has("lastUpdated")) {
                    tempObj.lastUpdated = resultObject.getLong("lastUpdated");
                }
                if (resultObject.has("score")) {
                    tempObj.score = resultObject.getDouble("score");
                }
                if (resultObject.has("language")) {
                    tempObj.language = resultObject.getString("language");
                }
                if (resultObject.has("description")) {
                    tempObj.description = resultObject.getString("description");
                }
                if (resultObject.has("id")) {
                    tempObj.id = resultObject.getString("id");
                }
                if (resultObject.has("title")) {
                    tempObj.title = resultObject.getString("title");
                }
                if (resultObject.has("feedId")) {
                    tempObj.feedId = resultObject.getString("feedId");
                }
                if (resultObject.has("website")) {
                    tempObj.website = resultObject.getString("website");
                }
                if (resultObject.has("subscribers")) {
                    tempObj.subscribers = resultObject.getInt("subscribers");
                }
                if (resultObject.has("velocity")) {
                    tempObj.velocity = resultObject.getDouble("velocity");
                }
                if (resultObject.has("updated")) {
                    tempObj.updated = resultObject.getLong("updated");
                }
                if (resultObject.has("iconUrl")) {
                    tempObj.iconUrl = resultObject.getString("iconUrl");
                }
                if (resultObject.has("partial")) {
                    tempObj.partial = resultObject.getBoolean("partial");
                }
                if (resultObject.has("coverUrl")) {
                    tempObj.coverUrl = resultObject.getString("coverUrl");
                }
                if (resultObject.has("visualUrl")) {
                    tempObj.visualUrl = resultObject.getString("visualUrl");
                }
                if (resultObject.has("estimatedEngagement")) {
                    tempObj.estimatedEngagement = resultObject.getInt("estimatedEngagement");
                }

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
