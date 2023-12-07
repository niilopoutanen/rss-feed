package com.niilopoutanen.rss_feed.utils;


import androidx.annotation.NonNull;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss.Source;
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


    public SourceValidator(@NonNull Source source){
        this.source = source;
    }
    public void validate(@NonNull Callback<Source> callback) {
        if(source.url == null || source.url.isEmpty()){
            callback.onError(new RSSException(R.string.error_empty_url ,"Feed url cannot be null"));
            return;
        }
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                getFeed();
                getTitle();
                getIcon();
                callback.onResult(source);
            }
            catch (RSSException r){
                callback.onError(r);
            }
        });

    }

    private void getFeed() throws RSSException{
        URL feedUrl = WebUtils.findFeed(WebUtils.formatUrl(source.url));
        if(feedUrl != null){
            source.url = feedUrl.toString();
        }
        else{
            throw new RSSException(R.string.error_invalid_url, "invalid URL");
        }
        Parser parser = new Parser();
        feed = parser.load(source.url);
    }
    private void getIcon(){
        if(feed.getImageUrl() == null){
            source.image = IconFinder.get(source.url);
        }
        else{
            source.image = IconFinder.get(source.url, new String[] {feed.getImageUrl()});
        }
    }
    private void getTitle() throws RSSException {
        // If name is already set, do nothing
        if(source.title != null && !source.title.isEmpty()){
            return;
        }
        // If feed was parsed, get the title from there
        if(feed != null && feed.getTitle() != null && !feed.getTitle().isEmpty()){
            source.title = feed.getTitle();
        }
        // If feed failed to parse, try the homepage
        else{
            try {
                URL homePage = WebUtils.getBaseUrl(source.url);
                if(homePage == null){
                    return;
                }
                Document doc = Jsoup.connect(homePage.toString()).get();
                String title = doc.title();

                // Check if the title has a separator
                if (title.contains(" | ")) {
                    source.title = title.split(" \\| ")[0];
                    return;
                } else if (title.contains(" - ")) {
                    source.title = title.split(" - ")[0];
                    return;
                }

                source.title = title;
            } catch (IOException e) {
                throw new RSSException(e.getMessage());
            }
        }
        if(source.title == null || source.title.isEmpty()){
            throw new RSSException(R.string.error_name_not_found, "Name not valid");
        }
    }

}
