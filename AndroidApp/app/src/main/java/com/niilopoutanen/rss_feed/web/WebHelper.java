package com.niilopoutanen.rss_feed.web;

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class WebHelper {

    public static URL formatUrl(String url){
        try {
            // check if the URL already includes a protocol
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                // add the http protocol to the URL
                url = "https://" + url;
            }
            return new URL(url);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
    public static URL getBaseUrl(URL url){
        try{
            return new URL(url.getProtocol() + "://" + url.getHost());
        }
        catch (MalformedURLException m){
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
            throw new RuntimeException(e);
        }
    }
    public static String fetchUrlData(URL url) throws IOException{
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
            // Get the new URL from the location header field
            String newUrl = connection.getHeaderField("Location");

            // Open a connection to the new URL
            connection = (HttpURLConnection) new URL(newUrl).openConnection();

            // Set request method
            connection.setRequestMethod("GET");


            // Connect to the new URL
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

        Elements link = doc.select("link[href~=.*\\.(png)][rel~=icon|apple-touch-icon|shortcut icon]");
        String faviconUrl = link.attr("href");

        // make sure the favicon URL is an absolute URL
        if (!faviconUrl.startsWith("http")) {
            faviconUrl = urlToUse + faviconUrl;
        }

        return faviconUrl;
    }
}
