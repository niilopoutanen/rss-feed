package com.niilopoutanen.rss_feed.utils;


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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SourceValidator {
    private final Source source;
    private Feed feed;


    public SourceValidator(Source source){
        this.source = source;
    }
    public void validate(Callback<Source> callback) {
        if(source.getFeedUrl() == null){
            callback.onError(new RSSException("Feed url cannot be null"));
            return;
        }
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                Parser parser = new Parser();
                getFeed();

                feed = parser.load(source.getFeedUrl());

                getTitle();
                getIcon();
                callback.onResult(source);
            }
            catch (RSSException r){
                callback.onError(r);
            }
        });

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
        // If name is already set, do nothing
        if(source != null && source.getName() != null && !source.getName().isEmpty()){
            return;
        }
        // If feed was parsed, get the title from there
        if(feed != null){
            if(!feed.getTitle().isEmpty()){
                source.setName(feed.getTitle());
            }
        }
        // If feed failed to parse, try the homepage
        else{
            try {
                URL homePage = WebUtils.getBaseUrl(source.getFeedUrl());
                if(homePage == null){
                    return;
                }
                Document doc = Jsoup.connect(homePage.toString()).get();
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

}
