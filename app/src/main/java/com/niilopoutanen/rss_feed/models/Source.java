package com.niilopoutanen.rss_feed.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class for saving user's sources to disk
 */
public class Source implements Serializable {
    //Saved data version
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String feedUrl;
    private String imageUrl;
    private final Boolean showInFeed;

    public Source(String name, String feedUrl, String imageUrl) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = true;
        this.id = UUID.randomUUID();
    }

    public Source(String name, String feedUrl, String imageUrl, boolean showInFeed) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = showInFeed;
        this.id = UUID.randomUUID();
    }

    public Source(String name, String feedUrl, String imageUrl, boolean showInFeed, UUID id) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.showInFeed = showInFeed;
        this.id = id;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl){
        this.feedUrl = feedUrl;
    }
    @Nullable
    public String getName() {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url){
        this.imageUrl = url;
    }
    public boolean isVisibleInFeed() {
        return this.showInFeed == null || this.showInFeed;

    }

    public UUID getId() {
        return this.id;
    }

    public void generateId() {
        this.id = UUID.randomUUID();
    }


    public static String generateOPML(List<Source> sources){
        String opmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<opml version=\"1.0\">\n" +
                  "  <head>\n" +
                  "    <title>Source List</title>\n" +
                  "  </head>\n" +
                  "  <body>\n";

        String opmlSources = sources.stream()
                  .map(source -> String.format("    <outline text=\"%s\" type=\"rss\" xmlUrl=\"%s\" />\n",
                            source.getName(), source.getFeedUrl()))
                  .collect(Collectors.joining());

        String opmlFooter = "  </body>\n" +
                  "</opml>";

        return opmlHeader + opmlSources + opmlFooter;
    }
}
