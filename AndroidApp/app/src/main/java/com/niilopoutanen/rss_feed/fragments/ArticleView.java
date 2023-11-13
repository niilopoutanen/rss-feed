package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.niilopoutanen.rss_feed.activities.ImageViewActivity;
import com.niilopoutanen.rss_feed.adapters.ArticleAdapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleView extends WebView {
    private final Context context;
    private Document document;
    public ArticleView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init(){
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(this, "Android");
    }
    public void loadDocument(Document document){
        this.document = document;
        Elements images = document.select("img");
        for(Element image: images){
            image.attr("onclick", "Android.onImageClick(this.src)");
        }

        super.loadData( document.toString(), "text/html", "utf-8");
    }

    public void scrollTo(String id){
        if(document == null){
            return;
        }
        Elements withTag = document.select("#" + id);
        if(!withTag.isEmpty()){
            Element el = withTag.first();
            loadUrl("javascript:document.getElementById('"+ id +"').scrollIntoView()");
        }


    }
    @JavascriptInterface
    public void onImageClick(String imageUrl) {
        Intent imageIntent = new Intent(context, ImageViewActivity.class);
        imageIntent.putExtra("imageurl", imageUrl);
        context.startActivity(imageIntent);
    }
}
