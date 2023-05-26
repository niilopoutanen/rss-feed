package com.niilopoutanen.rss_feed.utils;

import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.WebCallBack;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebHelper {

    public static final String FEEDLY_ENDPOINT = "https://cloud.feedly.com/v3/search/feeds?query=";
    public static final int FEEDLY_ENDPOINT_FETCHCOUNT = 40;

    /**
     * Format's a URL to valid format. (HTTP to HTTPS, extra chars removed)
     * @param url URL to format
     * @return formatted URL object
     */
    public static URL formatUrl(String url) {
        try {
            String regex = "https?://\\S+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                url = matcher.group();
            }
            // Check if the URL already includes a protocol
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                // Add the http protocol to the URL
                url = "https://" + url;
            }

            // Upgrade http to https
            URL finalUrl = new URL(url);
            if (finalUrl.getProtocol().equalsIgnoreCase("http")) {
                String upgradedUrlString = "https" + finalUrl.toString().substring(4);
                finalUrl = new URL(upgradedUrlString);
            }

            // Remove trailing "/" if it exists
            String urlString = finalUrl.toString();
            if (urlString.endsWith("/")) {
                urlString = urlString.substring(0, urlString.length() - 1);
                finalUrl = new URL(urlString);
            }

            return finalUrl;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Finds the host of a url.
     * @param url URL to parse
     * @return URL object with host parameters
     */
    public static URL getBaseUrl(URL url) {
        try {
            return new URL(url.getProtocol() + "://" + url.getHost());
        } catch (MalformedURLException m) {
            return url;
        }
    }
    public static URL getBaseUrl(String url) {
        try {
            URL urlToParse = new URL(url);
            return new URL(urlToParse.getProtocol() + "://" + urlToParse.getHost());
        } catch (MalformedURLException m) {
            return null;
        }
    }

    /**
     * Loads RSS feed data from a URL
     * @param rssFeedUrl URL to load
     * @param callback Callback that returns the feed data
     */
    public static void getFeedData(String rssFeedUrl, final WebCallBack<String> callback) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    // Open a connection to the website URL
                    Connection.Response response = Jsoup.connect(rssFeedUrl)
                            .ignoreContentType(true)
                            .execute();
                    int statusCode = response.statusCode();
                    String responseBody = response.body();
                    if (statusCode == 200) {
                        return responseBody;
                    } else {
                        throw new Exception(String.valueOf(statusCode));
                    }
                } catch (HttpStatusException e) {
                    throw new Exception(String.valueOf(e.getStatusCode()));
                }
            }
        });
        callback.onResult(future.get());
    }



    /**
     * Loads data from a URL no matter the type
     * @param url URL to load
     * @return String data from the URL
     */
    public static String fetchUrlData(URL url) throws IOException {
        // Create a new HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("GET");

        // Set connection timeout and read timeout
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        // Follow HTTP 301 redirection
        boolean redirect = false;
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
        }

        if (redirect) {
            String newUrl = connection.getHeaderField("Location");
            connection = (HttpURLConnection) new URL(newUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        }

        // Get the input stream from the connection
        InputStream inputStream = connection.getInputStream();

        // Read the input stream and convert it to a string
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        // Close the input stream and disconnect the connection
        inputStream.close();
        connection.disconnect();

        // Return the fetched data as a string
        return result.toString();
    }

    /**
     * Tries to find icon of the website provided
     * @param url URL to check
     * @return String URL for the img icon
     */
    public static String getFaviconUrl(final URL url) throws IOException {
        // check if the URL is the homepage and return the base URL in that case
        String baseUrl = url.getProtocol() + "://" + url.getHost();
        String urlToUse = url.toString();
        if (!url.toString().equals(baseUrl + "/")) {
            urlToUse = baseUrl;
        }
        // create an HTTP connection for the website's homepage
        Document doc = Jsoup.parse(fetchUrlData(new URL(urlToUse)));

        Elements link = doc.select("link[href~=.*\\.(png|webp|jpg|jpeg)][rel~=icon|apple-touch-icon|shortcut icon]");
        String faviconUrl = link.attr("href");
        if (link.isEmpty()) {
            link = doc.select("meta[property~=og:image], meta[name~=twitter:image]");
            faviconUrl = link.attr("content");
        }
        if (link.isEmpty() || faviconUrl.isEmpty()) {
            return null;
        }

        // make sure the favicon URL is an absolute URL
        if (!faviconUrl.startsWith("http")) {
            faviconUrl = urlToUse + faviconUrl;
        }

        return faviconUrl;
    }

    /**
     * Search Feedly API with the provided query
     * @param query Query to search with
     * @param callBack Returns a list of FeedResult objects that were found
     */
    public static void fetchFeedQuery(String query, WebCallBack<List<FeedResult>> callBack) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL queryUrl = new URL(FEEDLY_ENDPOINT + query + "&count=" + FEEDLY_ENDPOINT_FETCHCOUNT + "&locale=en");
                String result = fetchUrlData(queryUrl);
                List<FeedResult> results = FeedResult.parseResult(result);
                callBack.onResult(results);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    public static boolean isErrorCode(String errorMessage) {
        if(errorMessage == null){
            return false;
        }
        String statusCodeString = errorMessage.replaceFirst("java\\.lang\\.Exception:", "").trim();
        if (!statusCodeString.isEmpty()) {
            char firstDigit = statusCodeString.charAt(0);
            return (firstDigit == '4' || firstDigit == '5' || firstDigit == '3');
        }
        return false;
    }
    public static boolean isErrorCode(int statusCode) {
        String statusCodeString = String.valueOf(statusCode);
        if (!statusCodeString.isEmpty()) {
            char firstDigit = statusCodeString.charAt(0);
            return (firstDigit == '4' || firstDigit == '5' || firstDigit == '3');
        }
        return false;
    }

}
