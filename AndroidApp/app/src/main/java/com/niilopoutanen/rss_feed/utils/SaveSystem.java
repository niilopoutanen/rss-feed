package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.util.Log;

import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.models.WebCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    private static final String FILENAME_CATEGORYCACHCE = "rssfeed.categorycache";
    private static final String BASEURL = "https://raw.githubusercontent.com/niilopoutanen/RSS-Feed/app-resources/";
    private static final String CATEGORIES_EN = "categories.json";
    private static final String CATEGORIES_FI = "categories-fi.json";

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

    public static void saveContent(Context context, Source source) {
        List<Source> sources;
        try {
            sources = loadContent(context);

            sources.removeIf(contentObj -> contentObj.getFeedUrl().equals(source.getFeedUrl()));

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

    public static void loadCategories(final WebCallBack<List<Category>> callBack, Context context) {
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
        final String[] result = new String[1];
        if(isFileCached(context, FILENAME_CATEGORYCACHCE)){
            result[0] = loadCachedCategories(context);
            callBack.onResult(parseJsonCategories(result[0]));
        }
        else{
            Executor executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {

                try {
                    URL url = new URL(BASEURL + selectedLocale);
                    result[0] = WebHelper.fetchUrlData(url);
                    cacheCategories(context, result[0]);
                    callBack.onResult(parseJsonCategories(result[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

    }
    private static List<Category> parseJsonCategories(String result){
        List<Category> categories = new ArrayList<>();
        try{
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
        }
        catch (Exception ignored){}

        return categories;
    }
    public static boolean isFileCached(Context context, String fileName) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, fileName);
        return file.exists();
    }
    public static void cacheCategories(Context context, String json) {
        File cacheDir = context.getCacheDir();

        File file = new File(cacheDir, FILENAME_CATEGORYCACHCE);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String loadCachedCategories(Context context) {
        File cacheDir = context.getCacheDir();

        File file = new File(cacheDir, FILENAME_CATEGORYCACHCE);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}