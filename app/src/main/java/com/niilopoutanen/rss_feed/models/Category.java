package com.niilopoutanen.rss_feed.models;

import com.niilopoutanen.rss_feed.R;

import java.util.ArrayList;
import java.util.List;

public class Category {
    public static final List<Category> categories = new ArrayList<>();
    static {
        categories.add(new Category("News", R.drawable.icon_content, "News"));
        categories.add(new Category("Tech", R.drawable.icon_content, "Tech"));
        categories.add(new Category("Sports", R.drawable.icon_content, "Sports"));
        categories.add(new Category("Cars", R.drawable.icon_content, "Cars"));
        categories.add(new Category("Politics", R.drawable.icon_content, "Politics"));
        categories.add(new Category("Movies", R.drawable.icon_content, "Movies"));
        categories.add(new Category("Science", R.drawable.icon_content, "Science"));
        categories.add(new Category("Stocks", R.drawable.icon_content, "Stocks"));
        categories.add(new Category("Gaming", R.drawable.icon_content, "Gaming"));
    }

    public static final List<Category> categoriesFI = new ArrayList<>();
    static {
        categoriesFI.add(new Category("Uutiset", R.drawable.icon_content, "Uutiset"));
        categoriesFI.add(new Category("Teknologia", R.drawable.icon_content, "Tech"));
        categoriesFI.add(new Category("Urheilu", R.drawable.icon_content, "Urheilu"));
        categoriesFI.add(new Category("Autot", R.drawable.icon_content, "Cars"));
        categoriesFI.add(new Category("Politiikka", R.drawable.icon_content, "Politiikka"));
        categoriesFI.add(new Category("Elokuvat", R.drawable.icon_content, "Movies"));
        categoriesFI.add(new Category("Tiede", R.drawable.icon_content, "Tiede"));
        categoriesFI.add(new Category("Pörssi", R.drawable.icon_content, "Pörssi"));
        categoriesFI.add(new Category("Videopelit", R.drawable.icon_content, "Gaming"));
    }
    private final String name;
    private final int iconId;
    private final String query;

    public Category(String name, int iconId, String query) {
        this.name = name;
        this.iconId = iconId;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public int getIconId() {
        return iconId;
    }

    public static List<Category> getCategories(Country locale){
        switch (locale){
            case FI:
                return categoriesFI;
            case EN:
                return categories;
        }
        return null;
    }

    public enum Country{
        FI, EN
    }
}

