package com.niilopoutanen.rss_feed.sources;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Random;

public class Source implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String feedUrl;
    private final String imageUrl;
    private Color color;

    public Source(String name, String feedUrl, String imageUrl) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
    }
    public Source(String name, String feedUrl, String imageUrl, Color color) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.imageUrl = imageUrl;
        this.color = color;
    }

    public String getFeedUrl() {
        return feedUrl;
    }
    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public Color getColor(){
        if(color != null){
            return color;
        }
        else{
            Random random = new Random();
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            return Color.valueOf(red,green,blue);
        }
    }

}
