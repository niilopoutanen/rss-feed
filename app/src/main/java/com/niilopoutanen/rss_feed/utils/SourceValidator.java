package com.niilopoutanen.rss_feed.utils;

import android.content.Context;

import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.Feed;
import com.niilopoutanen.rssparser.IconFinder;
import com.niilopoutanen.rssparser.Parser;
import com.niilopoutanen.rssparser.RSSException;
import com.niilopoutanen.rssparser.WebUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SourceValidator {
    private Source source;
    private Feed feed;
    /**
     * Validates user input when adding a source
     *
     * @param inputUrl       URL provided
     * @param inputName      Name provided. Autofill will be tried if empty
     * @param sourceCallback Returns the validated source
     */
    public static void validate(String inputUrl, String inputName, Callback<Source> sourceCallback, Context context) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CompletableFuture.supplyAsync(() -> WebUtils.findFeed(WebUtils.formatUrl(inputUrl)), executor).thenComposeAsync(finalUrl -> {
            if (finalUrl == null) {
                return CompletableFuture.completedFuture(null);
            } else {
                CompletableFuture<String> faviconUrlFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return IconFinder.get(finalUrl);
                    } catch (Exception e) {
                        return null;
                    }
                }, executor);

                CompletableFuture<String> contentNameFuture = CompletableFuture.supplyAsync(() -> {
                    if (inputName.isEmpty()) {
                        return getSiteTitle(finalUrl);
                    } else {
                        return inputName;
                    }
                }, executor);

                return faviconUrlFuture.thenCombineAsync(contentNameFuture, (validatedIcon, validatedName) -> {
                    if (validatedName.isEmpty()) {
                        return null;
                    } else {
                        return new Source(validatedName, finalUrl.toString(), validatedIcon);
                    }
                }, executor);
            }
        }, executor).whenCompleteAsync((validatedContent, throwable) -> {
            sourceCallback.onResult(validatedContent);
            executor.shutdown();
        }, executor);
    }

    public SourceValidator(Source source){
        this.source = source;
    }
    public Source validate() throws RSSException {
        Parser parser = new Parser();
        feed = parser.load(source.getFeedUrl());

        getFeed();
        getTitle();
        getIcon();
        return this.source;
    }

    private void getFeed(){
        URL feedUrl = WebUtils.findFeed(WebUtils.formatUrl(source.getFeedUrl()));
        if(feedUrl != null){
            source.setFeedUrl(feedUrl.toString());
        }
    }
    private void getIcon(){
        source.setImageUrl(IconFinder.get(source.getFeedUrl()));
    }
    private void getTitle(){
        if(source.getName().isEmpty() && !feed.getTitle().isEmpty()){
            source.setName(feed.getTitle());
        }
        else if(feed.getTitle().isEmpty()){
            try {
                Document doc = Jsoup.connect(WebUtils.getBaseUrl(source.getFeedUrl()).toString()).get();
                String title = doc.title();

                // Check if the title has a separator
                if (title.contains(" | ")) {
                    source.setName(title.split(" \\| ")[0]);
                    return;
                } else if (title.contains(" - ")) {
                    source.setName(title.split(" - ")[0]);
                    return;
                }

                source.setName(title);
            } catch (IOException ignored) { }
        }
    }
    /**
     * Finds the HTML site title from a URL
     *
     * @param siteUrl URL to load
     * @return Site title in String format
     */
    public static String getSiteTitle(URL siteUrl) {
        try {
            Document doc = Jsoup.connect(WebUtils.getBaseUrl(siteUrl).toString()).get();
            String title = doc.title();

            // Check if the title has a separator
            if (title.contains(" | ")) {
                return title.split(" \\| ")[0];
            } else if (title.contains(" - ")) {
                return title.split(" - ")[0];
            }

            return title;
        } catch (IOException e) {
            return siteUrl.toString();
        }
    }

}
