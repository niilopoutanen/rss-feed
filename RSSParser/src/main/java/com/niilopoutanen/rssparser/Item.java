package com.niilopoutanen.rssparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Item implements Comparable<Item>, Serializable {
    private String title;
    private String link;
    private String description;
    private String author;
    private final List<String> categories = new ArrayList<>();
    private String comments;
    private boolean isPermaLink;
    private String imageUrl;
    private String guid;
    private Date pubDate;
    private String source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        if(this.link != null){
            return this.link;
        }
        else{
            return this.guid;
        }
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Element desc = Jsoup.parse(description).body();
        this.description = desc.text();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String category){
        if(!this.categories.contains(category)){
            this.categories.add(category);
        }
    }

    public String getComments(){
        return this.comments;
    }

    public void setComments(String comments){
        this.comments = comments;
    }

    public boolean isPermaLink() {
        return isPermaLink;
    }

    public void setPermaLink(boolean isPermaLink) {
        this.isPermaLink = isPermaLink;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getPubDate() {
        return this.pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void setPubDate(String pubDateString) {
        this.pubDate = Parser.parseDate(pubDateString);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int compareTo(Item item) {
        if (item == null || item.getPubDate() == null) {
            // If the other item or its pubDate is null, place this item on top
            return (getPubDate() == null) ? 0 : 1;
        } else if (getPubDate() == null) {
            // If this item's pubDate is null, place it on top
            return -1;
        } else {
            return item.getPubDate().compareTo(getPubDate());
        }
    }

}