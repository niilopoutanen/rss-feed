package com.niilopoutanen.rss_feed.rss;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "post", foreignKeys = {@ForeignKey(entity = Source.class, parentColumns = "id", childColumns = "sourceId", onDelete = ForeignKey.CASCADE)})
public class Post implements Comparable<Post>, Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sourceId;
    public String title;
    public String link;
    public String description;
    public String author;
    private final List<String> categories = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    public String image;
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

    public void addCategory(String category) {
        if (category != null && !category.isEmpty() && !this.categories.contains(category)) {
            this.categories.add(category.substring(0, 1).toUpperCase() + category.substring(1));
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
