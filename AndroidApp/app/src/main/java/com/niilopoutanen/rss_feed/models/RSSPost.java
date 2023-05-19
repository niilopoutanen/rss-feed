package com.niilopoutanen.rss_feed.models;

import java.util.Date;

public class RSSPost implements Comparable<RSSPost> {
    private String postLink;
    private String author;
    private String sourceName;
    private String imageUrl;
    private String title;
    private String description;
    private Date publishTime;


    public RSSPost(String postLink, String author, String sourceName, String imageUrl, String title, String description, Date publishTime) {
        this.postLink = postLink;
        this.author = author;
        this.sourceName = sourceName;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.publishTime = publishTime;
    }

    public RSSPost() {

    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public int compareTo(RSSPost post) {
        return post.getPublishTime().compareTo(getPublishTime());
    }
}
