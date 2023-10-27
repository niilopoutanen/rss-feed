package com.niilopoutanen.rss_feed.utils;

import com.niilopoutanen.RSSParser.WebUtils;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.WebCallBack;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebHelper {

    public static final String FEEDLY_ENDPOINT = "https://cloud.feedly.com/v3/search/feeds?query=";
    public static final int FEEDLY_ENDPOINT_FETCHCOUNT = 40;

    /**
     * Format's a URL to valid format. (HTTP to HTTPS, extra chars removed)
     *
     * @param url URL to format
     * @return formatted URL object
     */
    public static URL formatUrl(String url) {
        try {
            String regex = "https?://\\S+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                url = matcher.group();
            }
            // Check if the URL already includes a protocol
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                // Add the http protocol to the URL
                url = "https://" + url;
            }

            // Upgrade http to https
            URL finalUrl = new URL(url);
            if (finalUrl.getProtocol().equalsIgnoreCase("http")) {
                String upgradedUrlString = "https" + finalUrl.toString().substring(4);
                finalUrl = new URL(upgradedUrlString);
            }

            // Remove trailing "/" if it exists
            String urlString = finalUrl.toString();
            if (urlString.endsWith("/")) {
                urlString = urlString.substring(0, urlString.length() - 1);
                finalUrl = new URL(urlString);
            }

            return finalUrl;
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    /**
     * Search Feedly API with the provided query
     *
     * @param query    Query to search with
     * @param callBack Returns a list of FeedResult objects that were found
     */
    public static void fetchFeedQuery(String query, WebCallBack<List<FeedResult>> callBack) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL queryUrl = new URL(FEEDLY_ENDPOINT + query + "&count=" + FEEDLY_ENDPOINT_FETCHCOUNT + "&locale=en");
                String result = WebUtils.connect(queryUrl).toString();
                List<FeedResult> results = FeedResult.parseResult(result);
                callBack.onResult(results);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
