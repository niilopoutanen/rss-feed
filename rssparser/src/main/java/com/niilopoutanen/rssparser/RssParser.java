package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RssParser {
    private final Feed feed = new Feed();

    public Feed parse(Document document){
        parseFeed(document);
        parseItems(document.select("item"));
        return this.feed;
    }

    private void parseFeed(Document document){
        Element channel = document.selectFirst("channel");
        if(channel == null){
            return;
        }
        Element titleElement = channel.selectFirst("title");
        if(titleElement != null){
            feed.setTitle(titleElement.text());
        }

        Element linkElement = channel.selectFirst("link");
        if (linkElement != null) {
            feed.setLink(linkElement.text());
        }

        Element descElement = channel.selectFirst("description");
        if (descElement != null) {
            feed.setDescription(descElement.text());
        }

        Element languageElement = channel.selectFirst("language");
        if (languageElement != null) {
            feed.setLanguage(languageElement.text());
        }

        Element copyrightElement = channel.selectFirst("copyright");
        if (copyrightElement != null) {
            feed.setCopyright(copyrightElement.text());
        }

        Element pubDateElement = channel.selectFirst("pubDate");
        if (pubDateElement != null) {
            feed.setPubDate(pubDateElement.text());
        }

        Element lastBuildDateElement = channel.selectFirst("lastBuildDate");
        if (lastBuildDateElement != null) {
            feed.setLastBuildDate(lastBuildDateElement.text());
        }

        Elements categories = channel.select("category");
        if (categories.size() > 0) {
            for(Element category : categories){
                feed.addCategory(category.text());
            }
        }
    }
    private void parseItems(Elements itemObjects){
        for (Element itemElement : itemObjects) {
            Post post = new Post();
            Element titleElement = itemElement.selectFirst("title");
            if (titleElement != null) {
                post.title = titleElement.text();
            }

            Element linkElement = itemElement.selectFirst("link");
            if (linkElement != null) {
                post.link = linkElement.text();
            }

            Element guidElement = itemElement.selectFirst("guid");
            if (guidElement != null && post.link == null) {
                post.link = guidElement.text();
            }

            Element idElement = itemElement.selectFirst("id");
            if (idElement != null && post.link == null) {
                post.link = idElement.text();
            }

            Element descElement = itemElement.selectFirst("description");
            if (descElement != null) {
                Element desc = Jsoup.parse(descElement.text()).body();
                post.description = desc.text();
            }

            Element pubDateElement = itemElement.selectFirst("pubDate");
            if (pubDateElement != null) {
                post.pubDate = Parser.parseDate(pubDateElement.text());
            }

            Element summaryElement = itemElement.selectFirst("summary");
            if (summaryElement != null && post.description == null) {
                post.description = summaryElement.text();
            }

            Element contentElement = itemElement.selectFirst("content");
            if (contentElement != null && post.description == null) {
                post.description = contentElement.text();
            }

            Element authorElement = itemElement.selectFirst("author");
            if (authorElement != null) {
                post.author = authorElement.text();
            }

            Element creatorElement = itemElement.selectFirst("creator");
            if (creatorElement != null && post.author == null) {
                post.author = creatorElement.text();
            }

            Element dcCreatorElement = itemElement.selectFirst("dc|creator");
            if (dcCreatorElement != null && post.author == null) {
                post.author = dcCreatorElement.text();
            }

            Element mediaElement = itemElement.selectFirst("media|thumbnail");
            if (mediaElement != null) {
                post.image = mediaElement.text();
            }

            Element contentEncoded = itemElement.selectFirst("content|encoded");
            if (contentEncoded != null && post.image == null) {
                int startIndex = contentEncoded.toString().indexOf("<img");
                if (startIndex != -1 && post.image == null) {
                    post.image = Parser.parsePattern(contentEncoded.toString(), "src");
                }
            }

            Element enclosure = itemElement.selectFirst("enclosure");
            if (enclosure != null && !enclosure.attr("url").isEmpty() && post.image == null) {
                post.image = enclosure.attr("url");
            }

            if (descElement != null && post.image == null) {
                int startIndex = descElement.toString().indexOf("<img");
                if (startIndex != -1 && post.image == null) {
                    post.image = Parser.parsePattern(descElement.toString(), "src");
                }
            }

            Elements categories = itemElement.select("category");
            if(categories.size() > 0){
                for(Element category : categories){
                    post.addCategory(category.text());
                }
            }

            handleNullParams(post);
            feed.addItem(post);
        }
    }

    private void handleNullParams(Post post){
        if(post.author == null){
            post.author = feed.getTitle();
        }
    }
}
