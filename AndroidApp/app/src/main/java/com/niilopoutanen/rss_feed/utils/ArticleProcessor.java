package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.niilopoutanen.RSSParser.Callback;
import com.niilopoutanen.RSSParser.RSSException;
import com.niilopoutanen.RSSParser.WebUtils;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ArticleProcessor {
    private String html;
    private final List<View> views = new ArrayList<>();
    private final Context context;

    public ArticleProcessor(Context context){
        this.context = context;
    }

    public void process(String url, Callback<List<View>> callback){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                load(url);
                parse();
                callback.onResult(views);
            }
            catch (RSSException e) {
                callback.onError(e);
            }
        });
    }
    public String load(String url) throws RSSException {
        try {
            URL urlObject = new URL(url);
            String rawHtml = WebUtils.connect(urlObject).toString();

            Readability4J readability = new Readability4J(url, rawHtml);
            Article article = readability.parse();

            html = article.getContent();
            return html;
        }
        catch (RSSException r){
            throw r;
        }
        catch (Exception ignored) {}

        return "";
    }

    public void parse(){
        if(html.isEmpty()){
            return;
        }

        Document doc = Jsoup.parse(html);

        for (Element element : doc.getAllElements()) {
            if (element.tagName().equals("img")) {
                addImage(element);
            } else {
                addText(element.ownText());
            }
        }
    }

    public List<View> getViews(){
        return this.views;
    }
    private void addImage(Element element){
        ImageView imageView = new ImageView(context);
        
        views.add(imageView);
    }

    private void addText(String text){
        TextView textView = new TextView(context);

        textView.setText(text);

        views.add(textView);
    }
}
