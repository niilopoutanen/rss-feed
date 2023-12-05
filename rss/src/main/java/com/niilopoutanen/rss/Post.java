package com.niilopoutanen.rss;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Post implements Comparable<Post>, Serializable {
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "link")
    public String link;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "author")
    public String author;
    @ColumnInfo(name = "categories")
    private final List<String> categories = new ArrayList<>();
    @ColumnInfo(name = "comments")
    private final List<String> comments = new ArrayList<>();
    @ColumnInfo(name = "image")
    public String image;
    @ColumnInfo(name = "pubDate")
    public Date pubDate;

    public void addComment(String comment){
        this.comments.add(comment);
    }
    public void setComments(List<String> comments){
        this.comments.clear();
        this.comments.addAll(comments);
    }
    public List<String> getComments(){
        return this.comments;
    }

    public void addCategory(String category){
        if(!this.categories.contains(category)){
            categories.add(category);
        }
    }
    public void setCategories(List<String> categories){
        this.categories.clear();
        this.categories.addAll(categories);
    }
    public List<String> getCategories(){
        return this.categories;
    }

    @Override
    public int compareTo(Post post) {
        if (post == null || post.pubDate == null) {
            // If the other item or its pubDate is null, place this item on top
            return (pubDate == null) ? 0 : 1;
        } else if (pubDate == null) {
            // If this item's pubDate is null, place it on top
            return -1;
        } else {
            return post.pubDate.compareTo(pubDate);
        }
    }
}
