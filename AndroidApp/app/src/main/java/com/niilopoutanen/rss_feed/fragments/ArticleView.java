package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleView extends WebView {
    public ArticleView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init(){
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(this, "Android");
    }
    public void loadDocument(Document document){
        Elements images = document.select("img");
        for(Element image: images){
            image.attr("onclick", "Android.onImageClick(this.src)");
        }

        super.loadData( document.toString(), "text/html", "utf-8");
    }

    @JavascriptInterface
    public void onImageClick(String imageUrl) {
        Log.d("Image clicked: ", imageUrl);
    }
}
