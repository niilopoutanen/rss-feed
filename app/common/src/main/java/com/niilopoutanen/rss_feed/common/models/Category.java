package com.niilopoutanen.rss_feed.common.models;

import com.niilopoutanen.rss_feed.common.R;

import java.util.ArrayList;
import java.util.List;

public class Category {
    public static final String CATEGORY_RECOMMENDED = "$RSSFEED_RECOMMENDED";
    public static final List<Category> categories = new ArrayList<>();

    static {
        categories.add(new Category("Recommended", R.drawable.icon_star, CATEGORY_RECOMMENDED));
        categories.add(new Category("News", R.drawable.icon_news, "News"));
        categories.add(new Category("Tech", R.drawable.icon_mouse, "Tech"));
        categories.add(new Category("Sports", R.drawable.icon_baseball, "Sports"));
        categories.add(new Category("Cars", R.drawable.icon_car, "Cars"));
        categories.add(new Category("Politics", R.drawable.icon_briefcase, "Politics"));
        categories.add(new Category("Movies", R.drawable.icon_movies, "Movies"));
        categories.add(new Category("Marketing", R.drawable.icon_megaphone, "Marketing"));
        categories.add(new Category("Science", R.drawable.icon_microscope, "Science"));
        categories.add(new Category("Stocks", R.drawable.icon_graph, "Stocks"));
        categories.add(new Category("Food", R.drawable.icon_food, "Food"));
        categories.add(new Category("Gaming", R.drawable.icon_controller, "Gaming"));
    }

    public static final List<Category> categoriesFI = new ArrayList<>();

    static {
        categoriesFI.add(new Category("Suositeltu", R.drawable.icon_star, CATEGORY_RECOMMENDED));
        categoriesFI.add(new Category("Uutiset", R.drawable.icon_news, "Uutiset"));
        categoriesFI.add(new Category("Teknologia", R.drawable.icon_mouse, "Tech"));
        categoriesFI.add(new Category("Urheilu", R.drawable.icon_baseball, "Urheilu"));
        categoriesFI.add(new Category("Autot", R.drawable.icon_car, "Cars"));
        categoriesFI.add(new Category("Politiikka", R.drawable.icon_briefcase, "Politiikka"));
        categoriesFI.add(new Category("Elokuvat", R.drawable.icon_movies, "Movies"));
        categoriesFI.add(new Category("Markkinointi", R.drawable.icon_megaphone, "Marketing"));
        categoriesFI.add(new Category("Tiede", R.drawable.icon_microscope, "Tiede"));
        categoriesFI.add(new Category("Pörssi", R.drawable.icon_graph, "Pörssi"));
        categoriesFI.add(new Category("Ruoka", R.drawable.icon_food, "Food"));
        categoriesFI.add(new Category("Videopelit", R.drawable.icon_controller, "Gaming"));
    }

    private final String name;
    private final int iconId;
    private final String query;

    private boolean isActive;

    public boolean isActive() {

        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

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

    public static List<Category> getCategories(Country locale) {
        switch (locale) {
            case FI:
                return categoriesFI;
            case EN:
                return categories;
        }
        return null;
    }

    public enum Country {
        FI, EN
    }
}

