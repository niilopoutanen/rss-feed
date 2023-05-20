package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.util.Log;

import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.Content;
import com.niilopoutanen.rss_feed.models.WebCallBack;

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
    private static final String BASEURL = "https://raw.githubusercontent.com/niilopoutanen/RSS-Feed/app-resources/";
    private static final String CATEGORIES_EN = "categories.json";
    private static final String CATEGORIES_FI = "categories-fi.json";

    public static void saveContent(Context context, List<Content> contents) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(contents);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveContent(Context context, Content content) {
        List<Content> contents;
        try {
            contents = loadContent(context);

            contents.removeIf(contentObj -> contentObj.getFeedUrl().equals(content.getFeedUrl()));

            contents.add(content);
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(contents);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.d("SAVESYSTEM", "Content database not found");
        }
    }

    public static List<Content> loadContent(Context context) {
        List<Content> contents = new ArrayList<>();
        try {
            File file = context.getFileStreamPath(FILENAME);
            if (file != null && file.exists()) {
                FileInputStream fis = context.openFileInput(FILENAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                contents = (List<Content>) ois.readObject();
                ois.close();
                fis.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public static void loadCategories(final WebCallBack<List<Category>> callBack) {
        String locale = Locale.getDefault().getLanguage();
        String selectedLocale;
        switch (locale){
            default:
                selectedLocale = CATEGORIES_EN;
                break;
            case "fi":
                selectedLocale = CATEGORIES_FI;
                break;
        }
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            List<Category> categories = new ArrayList<>();
            try {
                URL url = new URL(BASEURL + selectedLocale);
                String result = WebHelper.fetchUrlData(url);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonCategory = jsonArray.getJSONObject(i);
                    String categoryName = jsonCategory.getString("name");
                    String categoryQuery = jsonCategory.getString("query");
                    String categoryImgUrl = null;
                    try{
                        categoryImgUrl = jsonCategory.getString("img");
                    }
                    catch (Exception ignored){}
                    Category category = new Category(categoryName, categoryImgUrl, categoryQuery);
                    categories.add(category);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            callBack.onResult(categories);
        });
    }
}