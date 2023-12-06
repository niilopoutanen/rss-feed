package com.niilopoutanen.rssparser;

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
            Item item = new Item();
            Element titleElement = itemElement.selectFirst("title");
            if (titleElement != null) {
                item.setTitle(titleElement.text());
            }

            Element linkElement = itemElement.selectFirst("link");
            if (linkElement != null) {
                item.setLink(linkElement.text());
            }

            Element guidElement = itemElement.selectFirst("guid");
            if (guidElement != null && item.getLink() == null) {
                item.setLink(guidElement.text());
            }

            Element idElement = itemElement.selectFirst("id");
            if (idElement != null && item.getLink() == null) {
                item.setLink(idElement.text());
            }

            Element descElement = itemElement.selectFirst("description");
            if (descElement != null) {
                item.setDescription(descElement.text());
            }

            Element pubDateElement = itemElement.selectFirst("pubDate");
            if (pubDateElement != null) {
                item.setPubDate(pubDateElement.text());
            }

            Element summaryElement = itemElement.selectFirst("description");
            if (summaryElement != null && item.getDescription() == null) {
                item.setDescription(summaryElement.text());
            }

            Element contentElement = itemElement.selectFirst("content");
            if (contentElement != null && item.getDescription() == null) {
                item.setDescription(contentElement.text());
            }

            Element authorElement = itemElement.selectFirst("author");
            if (authorElement != null) {
                item.setAuthor(authorElement.text());
            }

            Element creatorElement = itemElement.selectFirst("creator");
            if (creatorElement != null && item.getAuthor() == null) {
                item.setAuthor(creatorElement.text());
            }

            Element dcCreatorElement = itemElement.selectFirst("dc|creator");
            if (dcCreatorElement != null && item.getAuthor() == null) {
                item.setAuthor(dcCreatorElement.text());
            }

            Element mediaElement = itemElement.selectFirst("media|thumbnail");
            if (mediaElement != null) {
                item.setImageUrl(mediaElement.text());
            }

            Element contentEncoded = itemElement.selectFirst("content|encoded");
            if (contentEncoded != null && item.getImageUrl() == null) {
                int startIndex = contentEncoded.toString().indexOf("<img");
                if (startIndex != -1 && item.getImageUrl() == null) {
                    item.setImageUrl(Parser.parsePattern(contentEncoded.toString(), "src"));
                }
            }

            Element enclosure = itemElement.selectFirst("enclosure");
            if (enclosure != null && !enclosure.attr("url").isEmpty() && item.getImageUrl() == null) {
                item.setImageUrl(enclosure.attr("url"));
            }

            Element description = itemElement.selectFirst("description");
            if (description != null && item.getImageUrl() == null) {
                int startIndex = description.toString().indexOf("<img");
                if (startIndex != -1 && item.getImageUrl() == null) {
                    item.setImageUrl(Parser.parsePattern(description.toString(), "src"));
                }
            }

            Elements categories = itemElement.select("category");
            if(categories.size() > 0){
                for(Element category : categories){
                    item.addCategory(category.text());
                }
            }


            handleNullParams(item);
            feed.addItem(item);
        }
    }

    private void handleNullParams(Item item){
        if(item.getAuthor() == null){
            item.setAuthor(feed.getTitle());
        }
    }
}
