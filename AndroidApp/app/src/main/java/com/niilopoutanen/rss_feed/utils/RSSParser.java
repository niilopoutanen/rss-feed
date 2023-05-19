package com.niilopoutanen.rss_feed.utils;

import com.niilopoutanen.rss_feed.models.RSSPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

            Elements contentEncoded = itemElement.select("content\\:encoded");
            if (!contentEncoded.isEmpty()) {
                String contentHtml = contentEncoded.first().html();
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

}
