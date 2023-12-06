package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Feed {
    private String title;
    private String link;
    private String description;
    private String language;
    private String copyright;
    private String managingEditor;
    private String webMaster;
    private Date pubDate;
    private Date lastBuildDate;
    private final List<String> categories = new ArrayList<>();
    private String generator;
    private String docs;
    private String cloud;
    private Integer ttl;
    private String imageUrl;
    private String rating;
    private List<Post> posts = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getManagingEditor() {
        return managingEditor;
    }

    public void setManagingEditor(String managingEditor) {
        this.managingEditor = managingEditor;
    }

    public String getWebMaster() {
        return webMaster;
    }

    public void setWebMaster(String webMaster) {
        this.webMaster = webMaster;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
    public void setPubDate(String pubDateString) {
        this.pubDate = Parser.parseDate(pubDateString);
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }
    public void setLastBuildDate(String lastBuildDateString) {
        this.lastBuildDate = Parser.parseDate(lastBuildDateString);
    }

    public void addCategory(String category){
        if(!categories.contains(category)){
            this.categories.add(category);
        }
    }
    public List<String> getCategories() {
        return categories;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = Integer.parseInt(ttl);
    }

    public String getImageUrl(){
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Post> getPosts() {
        return posts;
    }
    public int getItemCount(){
        return posts.size();
    }
    public Post getItemAt(int index) {
        return posts.get(index);
    }

    public void setPosts(List<Post> posts){
        this.posts = posts;
    }

    public void addItem(Post item){
        this.posts.add(item);
    }
}
