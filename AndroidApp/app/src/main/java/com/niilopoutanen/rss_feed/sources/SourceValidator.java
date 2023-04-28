package com.niilopoutanen.rss_feed.sources;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.web.WebCallBack;
import com.niilopoutanen.rss_feed.web.WebHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SourceValidator {
    public static void validate(String sourceUrl, String sourceName, WebCallBack<Source> sourceCallBack) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<URL> urlsToCheck = new ArrayList<>();
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/feed"));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/rss"));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/rss.xml"));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/rss/rss.xml"));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/atom"));
        urlsToCheck.add(WebHelper.formatUrl(sourceUrl + "/atom.xml"));


        final URL[] finalUrl = new URL[1];
        CompletableFuture<Boolean> urlCheckFuture = CompletableFuture.supplyAsync(() -> {
            for (URL url : urlsToCheck) {
                try {
                    boolean urlExists = urlExists(url);
                    boolean rssExists = rssExists(url);
                    if (rssExists && urlExists) {
                        finalUrl[0] = url;
                        return true;
                    }
                } catch (IOException e) {
                    // Ignore exceptions and try the next URL
                }
            }
            return false;
        }, executor);

        Boolean validUrl = urlCheckFuture.join();

        if (validUrl) {
            CompletableFuture<String> faviconUrlFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return WebHelper.getFaviconUrl(finalUrl[0]);
                } catch (IOException e) {
                    return null;
                }
            }, executor);

            CompletableFuture<String> sourceNameFuture = CompletableFuture.supplyAsync(() -> {
                if (sourceName.isEmpty()) {
                    return getSiteTitle(finalUrl[0]);
                } else {
                    return sourceName;
                }
            }, executor);

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(faviconUrlFuture, sourceNameFuture);
            allFutures.thenRunAsync(() -> {
                String validatedIcon = faviconUrlFuture.join();
                String validatedName = sourceNameFuture.join();
                if (validatedName.isEmpty()) {
                    sourceCallBack.onResult(null);
                    executor.shutdown();
                    return;
                }
                Source validatedSource = new Source(validatedName, finalUrl[0].toString(), validatedIcon);
                sourceCallBack.onResult(validatedSource);
                executor.shutdown();
            }, executor);

            allFutures.join();
        } else {
            sourceCallBack.onResult(null);
            executor.shutdown();
        }


    }



    private static boolean urlExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");
        int responseCode = httpURLConnection.getResponseCode();
        return responseCode != HttpURLConnection.HTTP_NOT_FOUND &&
                responseCode != HttpURLConnection.HTTP_GONE &&
                responseCode != HttpURLConnection.HTTP_FORBIDDEN;
    }
    private static boolean rssExists(URL url) throws IOException{
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");
        String contentType = httpURLConnection.getContentType();
        if(contentType != null){
            boolean hasRssHeader = contentType.startsWith("application/rss+xml") || contentType.startsWith("application/xml");
            if(hasRssHeader){
                //if rss headers are detected
                return true;
            }
        }

        Document document = Jsoup.connect(url.toString()).ignoreContentType(true).get();
        Element rootElement = document.select(":root").first();
        if(rootElement == null){
            return false;
        }
        String tagname = rootElement.tagName();
        return tagname.equals("xml") || tagname.equals("rss");
    }
    public static String getSiteTitle(URL siteUrl) {
        try{
            Document doc = Jsoup.connect(WebHelper.getBaseUrl(siteUrl).toString()).get();
            String title = doc.title();

            // Check if the title has a separator
            if (title.contains(" | ")) {
                return title.split(" \\| ")[0];
            }
            else if (title.contains(" - ")) {
                return title.split(" - ")[0];
            }

            return title;
        }
        catch (IOException e){
            return siteUrl.toString();
        }
    }
    public static TextView createErrorMessage(Context context, String errorMessage){
        TextView errorText = new TextView(context);
        errorText.setText(errorMessage);
        errorText.setTextColor(context.getColor(R.color.textSecondary));
        errorText.setTag("error-message");
        return  errorText;
    }

}
