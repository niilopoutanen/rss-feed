package com.niilopoutanen.rss_feed.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {
    public static Document connect(URL url) throws IOException, RSSException {
        String result = fetch(url);
        // Return the fetched data as a JSoup document
        return Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
    }

    public static String connectRaw(URL url) throws IOException, RSSException {
        return fetch(url);
    }
    public static Document connect(String urlStr){
        try{
            URL url = new URL(urlStr);
            String result = fetch(url);
            return Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String fetch(URL url) throws IOException, RSSException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);
        // Set connection timeout and read timeout
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);


        // Follow HTTP 301 redirection
        if (isRedirect(connection)) {
            String newUrl = connection.getHeaderField("Location");
            connection = (HttpURLConnection) new URL(newUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        }

        // Get the input stream from the connection and parse it
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

        return result.toString();
    }
    public static boolean rssExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
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
        String tagName = rootElement.tagName();
        return tagName.equals("xml") || tagName.equals("rss");
    }
    public static boolean urlExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
        httpURLConnection.setRequestMethod("HEAD");
        int responseCode = httpURLConnection.getResponseCode();
        return !isErrorCode(responseCode);
    }
    public static URL findFeed(URL baseUrl){
        //Popular RSS url paths
        List<String> urlPaths = Arrays.asList(
                "",
                "/feed",
                "/rss",
                "/.rss",
                "/blog",
                "/atom",


                "/rss/uutiset.xml",
                "/rss/uutiset",
                "/rss/news.xml",
                "/rss.xml",
                "/rss/rss.xml",
                "/rss/feed",

                "/atom.xml",

                "/feed/home",
                "/feed/rss",
                "/feed/rss.xml",
                "/feed/news",

                "/news/rss.xml"
        );

        for (String path : urlPaths) {
            try {
                String urlStr = baseUrl + path;
                URL url = new URL(urlStr);
                Document document = connect(url);
                boolean urlExists = document.hasText();
                boolean feedExists = isRss(document) || isAtom(document);
                if (feedExists && urlExists) {
                    return url;
                }
            }
            catch (Exception ignore) {
                // Ignore exceptions and try the next URL
            }
        }
        return null;
    }
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
    public static boolean isRedirect(HttpURLConnection connection) throws IOException, RSSException {
        boolean redirect = false;
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
            else{
                throw new RSSException(status, connection.getResponseMessage());
            }
        }
        return redirect;
    }
    public static boolean isErrorCode(String errorMessage) {
        if (errorMessage == null) {
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

    public static boolean isRss(Document document){
        return !document.select("channel").isEmpty();
    }

    public static boolean isAtom(Document document){
        return !document.select("feed").isEmpty();
    }

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
}
