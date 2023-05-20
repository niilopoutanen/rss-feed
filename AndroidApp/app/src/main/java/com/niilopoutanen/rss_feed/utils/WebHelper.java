package com.niilopoutanen.rss_feed.utils;

import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.WebCallBack;

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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebHelper {

    public static final String FEEDLY_ENDPOINT = "https://cloud.feedly.com/v3/search/feeds?query=";
    public static final int FEEDLY_ENDPOINT_FETCHCOUNT = 40;
    public static URL formatUrl(String url) {
        try {
            String regex = "https?://\\S+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                url = matcher.group();
            }
            // check if the URL already includes a protocol
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                // add the http protocol to the URL
                url = "https://" + url;
            }

            //upgrade http to https
            URL finalUrl = new URL(url);
            if(finalUrl.getProtocol().equalsIgnoreCase("http")) {
                String upgradedUrlString = "https" + finalUrl.toString().substring(4);
                finalUrl = new URL(upgradedUrlString);
            }
            return finalUrl;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static URL getBaseUrl(URL url) {
        try {
            return new URL(url.getProtocol() + "://" + url.getHost());
        } catch (MalformedURLException m) {
            return url;
        }

    }

    public static void getFeedData(String rssFeedUrl, final WebCallBack<String> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                // Open a connection to the website URL
                return Jsoup.connect(rssFeedUrl).ignoreContentType(true).get().toString();

            }
        });
        try {
            callback.onResult(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public static void fetchFeedQuery(String query, WebCallBack<String> callBack) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                List<FeedResult> results = new ArrayList<>();
                URL queryUrl = new URL(FEEDLY_ENDPOINT + query + "&count=" + FEEDLY_ENDPOINT_FETCHCOUNT);
                String result = fetchUrlData(queryUrl);
                callBack.onResult(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
