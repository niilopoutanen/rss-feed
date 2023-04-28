package com.niilopoutanen.rss_feed.rss;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RSSParser {
    public static List<RSSPost> parseRssFeed(String rssFeedCode) {
        List<RSSPost> itemList = new ArrayList<>();
        Document doc = Jsoup.parse(rssFeedCode, "", Parser.xmlParser());
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
                post.setDescription(plainDescription);
                try {
                    post.setImageUrl(parseImageURL(htmlDescription));
                } catch (Exception ignored) {}
            }

            Element pubDate = itemElement.selectFirst("pubDate");
            if(pubDate != null){
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
                try{
                    post.setPublishTime(dateFormat.parse(pubDate.text()));
                }
                catch (Exception ignored){

                }
            }
            Elements author = itemElement.select("creator, dc|creator");
            if (!author.isEmpty()) {
                post.setAuthor(author.first().text());
            }
            itemList.add(post);
        }
        return itemList;
    }
    private static String parseImageURL(String description) {
        int startIndex = description.indexOf("src=\"") + 5;
        int endIndex = description.indexOf("\"", startIndex);
        return description.substring(startIndex, endIndex);
    }
}
