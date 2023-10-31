package com.niilopoutanen.rss_feed.utils;

import android.util.Log;
import android.view.View;

import com.niilopoutanen.RSSParser.Callback;
import com.niilopoutanen.RSSParser.RSSException;
import com.niilopoutanen.RSSParser.WebUtils;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ArticleProcessor {
    public static void process(String url, Callback<List<View>> callback) throws RSSException{
        List<View> views = new ArrayList<>();
        parse(load(url));
    }

    private static String load(String url) throws RSSException {
        try {
            URL urlObject = new URL(url);
            String html = WebUtils.connect(urlObject).toString();

            Readability4J readability = new Readability4J(url, html);
            Article article = readability.parse();
            return article.getContent();
        }
        catch (RSSException r){
            throw r;
        }
        catch (Exception ignored) {}

        return "";
    }

    private static void parse(String html){
        Log.d("HTML", html);
    }
}
