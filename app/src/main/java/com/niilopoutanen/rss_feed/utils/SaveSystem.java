package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.util.Log;

import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rssparser.Callback;
import com.niilopoutanen.rssparser.WebUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SaveSystem {
    private static final String FILENAME = "rssfeed.content";

    /**
     * Saves a list of sources to disk
     *
     * @param context Required to get file save path
     * @param sources List of the sources to save
     */
    public static void saveContent(Context context, List<Source> sources) {
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

    /**
     * Saves a source to disk. Updates the source if it already exists
     *
     * @param context Required to get file save path
     * @param source  The source to save
     */
    public static void saveContent(Context context, Source source) {
        List<Source> sources;
        try {
            sources = loadContent(context);
            //Check if the source already exists
            sources.removeIf(sourceObj -> {
                if (source.id == 0 || sourceObj.id == 0) {
                    // If id not present
                    return sourceObj.url.equals(source.url);
                }
                return sourceObj.id == source.id;
            });

            sources.add(source);
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sources);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.d("SAVESYSTEM", "Content database not found");
        }
    }

    /**
     * Loads saved sources from disk
     *
     * @param context Required to get file save path
     */
    public static List<Source> loadContent(Context context) {
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

        return sources;
    }
}