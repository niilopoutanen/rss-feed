package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.models.WebCallBack;

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
    /**
     * Validates user input when adding a source
     * @param contentUrl URL provided
     * @param contentName Name provided. Autofill will be tried if empty
     * @param contentCallBack Returns the validated source
     */
    public static void validate(String contentUrl, String contentName, WebCallBack<Source> contentCallBack, Context context) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        //List of RSS URLs to check. Also in localized format
        List<URL> urlsToCheck = new ArrayList<>();
        urlsToCheck.add(WebHelper.formatUrl(contentUrl));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/feed"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/rss"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/rss/" + context.getString(R.string.rsslocale_news) + ".xml"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/rss/news.xml"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/rss.xml"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/rss/rss.xml"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/atom"));
        urlsToCheck.add(WebHelper.formatUrl(contentUrl + "/atom.xml"));

        CompletableFuture.supplyAsync(() -> {
            for (URL url : urlsToCheck) {
                try {
                    boolean urlExists = urlExists(url);
                    boolean rssExists = rssExists(url);
                    if (rssExists && urlExists) {
                        return url;
                    }
                } catch (IOException e) {
                    // Ignore exceptions and try the next URL
                }
            }
            return null;
        }, executor).thenComposeAsync(finalUrl -> {
            if (finalUrl == null) {
                return CompletableFuture.completedFuture(null);
            } else {
                CompletableFuture<String> faviconUrlFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return WebHelper.getFaviconUrl(finalUrl);
                    } catch (IOException e) {
                        return null;
                    }
                }, executor);

                CompletableFuture<String> contentNameFuture = CompletableFuture.supplyAsync(() -> {
                    if (contentName.isEmpty()) {
                        return getSiteTitle(finalUrl);
                    } else {
                        return contentName;
                    }
                }, executor);

                return faviconUrlFuture.thenCombineAsync(contentNameFuture, (validatedIcon, validatedName) -> {
                    if (validatedName.isEmpty()) {
                        return null;
                    } else {
                        return new Source(validatedName, finalUrl.toString(), validatedIcon);
                    }
                }, executor);
            }
        }, executor).whenCompleteAsync((validatedContent, throwable) -> {
            contentCallBack.onResult(validatedContent);
            executor.shutdown();
        }, executor);
    }

    /**
     * Checks if the provided URL is valid/exists
     * @param url URL to check
     * @return true if yes, false if no
     */
    private static boolean urlExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");
        int responseCode = httpURLConnection.getResponseCode();
        return responseCode != HttpURLConnection.HTTP_NOT_FOUND &&
                responseCode != HttpURLConnection.HTTP_GONE &&
                responseCode != HttpURLConnection.HTTP_FORBIDDEN;
    }

    /**
     * Checks if the provided URL is a RSS url
     * @param url URL to check
     * @return true if yes, false if no
     */
    private static boolean rssExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("HEAD");
        String contentType = httpURLConnection.getContentType();
        if (contentType != null) {
            boolean hasRssHeader = contentType.startsWith("application/rss+xml") || contentType.startsWith("application/xml");
            if (hasRssHeader) {
                //if rss headers are detected
                return true;
            }
        }

        Document document = Jsoup.connect(url.toString()).ignoreContentType(true).get();
        Element rootElement = document.select(":root").first();
        if (rootElement == null) {
            return false;
        }
        String tagname = rootElement.tagName();
        return tagname.equals("xml") || tagname.equals("rss");
    }

    /**
     * Finds the HTML site title from a URL
     * @param siteUrl URL to load
     * @return Site title in String format
     */
    public static String getSiteTitle(URL siteUrl) {
        try {
            Document doc = Jsoup.connect(WebHelper.getBaseUrl(siteUrl).toString()).get();
            String title = doc.title();

            // Check if the title has a separator
            if (title.contains(" | ")) {
                return title.split(" \\| ")[0];
            } else if (title.contains(" - ")) {
                return title.split(" - ")[0];
            }

            return title;
        } catch (IOException e) {
            return siteUrl.toString();
        }
    }
    /**
     * Creates a error message that can be show to the user
     * @param errorMessage Message to show
     * @return Returns a TextView with the error message
     */
    public static TextView createErrorMessage(Context context, String errorMessage) {
        TextView errorText = new TextView(context);
        errorText.setText(errorMessage);
        errorText.setTextColor(context.getColor(R.color.textSecondary));
        errorText.setTag("error-message");
        return errorText;
    }

}
