package com.niilopoutanen.rss_feed.common.models;

import com.niilopoutanen.rss_feed.parser.WebUtils;
import com.niilopoutanen.rss_feed.rss.Source;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
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
     * @param jsonObject JSONObject data of the URL fetch result
     * @return List with parsed FeedResult objects
     */
    public static List<FeedResult> parseResult(JSONObject jsonObject) {
        List<FeedResult> results = new ArrayList<>();
        try {
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


    public Source getSource(){
        Source source = new Source();
        source.title = title;
        URL url = WebUtils.formatUrl(feedId);
        if(url != null){
            source.url = url.toString();
        }
        source.home = website;
        source.description = description;
        source.image = visualUrl;
        source.language = language;

        source.trim();
        return source;
    }

}
