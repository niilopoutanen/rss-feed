package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.niilopoutanen.rss_feed.activities.ImageViewActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class ArticleView extends WebView {
    private final Context context;
    private Document document;
    public ArticleView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }
    public ArticleView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(this, "Android");
        setWebContentsDebuggingEnabled(true);
    }
    public void loadDocument(Document documentToLoad){
        this.document = documentToLoad;
        Elements images = document.select("img");
        for(Element image: images){
            image.attr("onclick", "event.preventDefault(); Android.onImageClick(this.src);");
        }

        super.loadDataWithBaseURL(null, document.html(), "text/html", "charset=utf-8", "");
    }

    @JavascriptInterface
    public void onImageClick(String imageUrl) {
        Intent imageIntent = new Intent(context, ImageViewActivity.class);
        imageIntent.putExtra("imageurl", imageUrl);
        context.startActivity(imageIntent);
    }
}
