package com.niilopoutanen.rss_feed.utils;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.RSSParser.WebUtils;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.models.WebCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SourceValidator {
    /**
     * Validates user input when adding a source
     *
     * @param inputUrl       URL provided
     * @param inputName      Name provided. Autofill will be tried if empty
     * @param sourceCallback Returns the validated source
     */
    public static void validate(String inputUrl, String inputName, WebCallBack<Source> sourceCallback, Context context) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CompletableFuture.supplyAsync(() -> WebUtils.findFeed(WebUtils.formatUrl(inputUrl)), executor).thenComposeAsync(finalUrl -> {
            if (finalUrl == null) {
                return CompletableFuture.completedFuture(null);
            } else {
                CompletableFuture<String> faviconUrlFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return WebUtils.getFaviconUrl(finalUrl);
                    } catch (IOException e) {
                        return null;
                    }
                }, executor);

                CompletableFuture<String> contentNameFuture = CompletableFuture.supplyAsync(() -> {
                    if (inputName.isEmpty()) {
                        return getSiteTitle(finalUrl);
                    } else {
                        return inputName;
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
            sourceCallback.onResult(validatedContent);
            executor.shutdown();
        }, executor);
    }


    /**
     * Finds the HTML site title from a URL
     *
     * @param siteUrl URL to load
     * @return Site title in String format
     */
    public static String getSiteTitle(URL siteUrl) {
        try {
            Document doc = Jsoup.connect(WebUtils.getBaseUrl(siteUrl).toString()).get();
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
     *
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
