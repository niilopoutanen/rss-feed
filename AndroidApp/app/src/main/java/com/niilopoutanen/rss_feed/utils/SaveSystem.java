package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.util.Log;

import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveSystem {
    private static final String FILENAME = "rssfeed.sources";

    public static void saveSources(Context context, List<Source> sources) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sources);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSources(Context context, Source source) {
        List<Source> sources;
        try {
            sources = loadSources(context);

            sources.removeIf(sourceObj -> sourceObj.getFeedUrl().equals(source.getFeedUrl()));

            sources.add(source);
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sources);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.d("SAVESYSTEM", "Source database not found");
        }
    }

    public static List<Source> loadSources(Context context) {
        List<Source> sources = new ArrayList<>();
        try {
            File file = context.getFileStreamPath(FILENAME);
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(FILENAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                sources = (List<Source>) ois.readObject();
                ois.close();
                fis.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        ParseCategories();
        return sources;
    }


    public static void ParseCategories() {
        Category tech = new Category(Category.c_TECH);
        Category sports = new Category(Category.c_SPORTS);

        JSONObject json = new JSONObject();
        try {
            json.put("tech", tech.toJson());
            json.put("sports", sports.toJson());

            String jsonString = json.toString();
            Log.d("json", jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}