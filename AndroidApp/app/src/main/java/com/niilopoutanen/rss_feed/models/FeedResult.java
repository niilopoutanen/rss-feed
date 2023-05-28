package com.niilopoutanen.rss_feed.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing Feedly result data to
 */
public class FeedResult {
    public String language;
    public String description;
    public String title;
    public String feedId;
    public String website;
    public String iconUrl;
    public String coverUrl;
    public String visualUrl;
    public boolean alreadyAdded;

    /**
     * Parses a search result to object format
     *
     * @param result String data of the URL fetch result
     * @return List with parsed FeedResult objects
     */
    public static List<FeedResult> parseResult(String result) {
        List<FeedResult> results = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                FeedResult tempObj = new FeedResult();

                if (resultObject.has("language")) {
                    tempObj.language = resultObject.getString("language");
                }
                if (resultObject.has("description")) {
                    tempObj.description = resultObject.getString("description");
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
                if (resultObject.has("iconUrl")) {
                    tempObj.iconUrl = resultObject.getString("iconUrl");
                }
                if (resultObject.has("coverUrl")) {
                    tempObj.coverUrl = resultObject.getString("coverUrl");
                }
                if (resultObject.has("visualUrl")) {
                    tempObj.visualUrl = resultObject.getString("visualUrl");
                }

                results.add(tempObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

}
