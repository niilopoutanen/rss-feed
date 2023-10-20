package com.niilopoutanen.rss_feed.utils;

import android.content.Context;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.RSSPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RSSParser {
    /**
     * Finds posts from a RSS Feed.
     *
     * @param outputData String data of the RSS output
     */
    public static List<RSSPost> parseRssFeed(String outputData) {
        List<RSSPost> itemList = new ArrayList<>();
        Document doc = Jsoup.parse(outputData, "", Parser.xmlParser());
        Elements itemElements = doc.select("item");
        for (Element itemElement : itemElements) {
            RSSPost post = new RSSPost();
            Element titleElement = itemElement.selectFirst("title");
            if (titleElement != null) {
                post.setTitle(titleElement.text());
            }
            Element linkElement = itemElement.selectFirst("link");
            if (linkElement != null) {
                post.setPostLink(linkElement.text());
            }

            Element descElement = itemElement.selectFirst("description");
            if (descElement != null) {
                String htmlDescription = descElement.html();
                String plainDescription = Jsoup.parse(htmlDescription).text().replaceAll("\\<.*?>", "");
                plainDescription = plainDescription.replaceAll("\n", "");
                plainDescription = org.jsoup.parser.Parser.unescapeEntities(plainDescription, true); // unescape HTML entities
                post.setDescription(plainDescription);
                try {
                    post.setImageUrl(parseImageURL(htmlDescription));
                } catch (Exception ignored) {
                }
            }


            Element pubDate = itemElement.selectFirst("pubDate");
            if (pubDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
                try {
                    post.setPublishTime(dateFormat.parse(pubDate.text()));
                } catch (Exception ignored) {

                }
            }
            Elements author = itemElement.select("creator, dc|creator");
            if (!author.isEmpty()) {
                post.setAuthor(author.first().text());
            }

            Element mediaThumbnail = itemElement.selectFirst("media|thumbnail");
            if (mediaThumbnail != null) {
                String imageUrl = mediaThumbnail.attr("url");
                post.setImageUrl(imageUrl);
            }

            Elements contentEncoded = itemElement.getElementsByTag("content:encoded");
            if (!contentEncoded.isEmpty()) {
                String contentHtml = contentEncoded.first().html();
                contentHtml = contentHtml
                        .replace("&lt;", "<")
                        .replace("&gt;", ">");

                String cdataRegex = "<!\\[CDATA\\[(.*?)\\]\\]>";
                Pattern pattern = Pattern.compile(cdataRegex, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(contentHtml);
                if (matcher.find()) {
                    contentHtml = matcher.group(1);
                }
                Document inlineDoc = Jsoup.parse(contentHtml);
                Elements imgElements = inlineDoc.select("img");
                if (!imgElements.isEmpty()) {
                    String imageUrl = imgElements.first().attr("src");
                    post.setImageUrl(imageUrl);
                }
            }


            itemList.add(post);
        }
        return itemList;
    }

    /**
     * Tries to find img url from a HTML String
     *
     * @param description String data where the img url can be found
     * @return returns the parsed url. If nothing is found then returns null
     */
    private static String parseImageURL(String description) {
        int startIndex = description.indexOf("<img");
        if (startIndex == -1) {
            // no image tag found, return null
            return null;
        }
        startIndex = description.indexOf("src=\"", startIndex) + 5;
        int endIndex = description.indexOf("\"", startIndex);
        return description.substring(startIndex, endIndex);
    }

    /**
     * Tries to find a valid RSS feed from a given URL
     *
     * @return first encountered URL that is valid
     */
    public static URL feedFinder(String baseUrl, Context context) {
        //Popular RSS url paths
        List<String> urlPaths = Arrays.asList(
                "",
                "/feed",
                "/rss",
                "/blog",
                "/atom",

                "/rss/" + context.getString(R.string.rsslocale_news) + ".xml",
                "/rss/" + context.getString(R.string.rsslocale_news),
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
            String urlStr = baseUrl + path;
            URL url = WebHelper.formatUrl(urlStr);

            try {
                boolean urlExists = urlExists(url);
                boolean rssExists = rssExists(url);
                if (rssExists && urlExists) {
                    return url;
                }
            } catch (IOException ignore) {
                // Ignore exceptions and try the next URL
            }
        }
        return null;
    }


    /**
     * Checks if the provided URL is valid/exists
     *
     * @param url URL to check
     * @return true if yes, false if no
     */
    private static boolean urlExists(URL url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
        httpURLConnection.setRequestMethod("HEAD");
        int responseCode = httpURLConnection.getResponseCode();
        return !WebHelper.isErrorCode(responseCode);
    }

    /**
     * Checks if the provided URL is a RSS url
     *
     * @param url URL to check
     * @return true if yes, false if no
     */
    private static boolean rssExists(URL url) throws IOException {
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
        String tagname = rootElement.tagName();
        return tagname.equals("xml") || tagname.equals("rss");
    }
}
