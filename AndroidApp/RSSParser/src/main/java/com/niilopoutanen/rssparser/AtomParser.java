package com.niilopoutanen.rssparser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AtomParser {
    private final Feed feed = new Feed();
    public AtomParser init(){

        return this;
    }
    public Feed parse(Document document){
        parseFeed(document);
        parseItems(document.select("entry"));
        return this.feed;
    }

    private void parseFeed(Document document){
        Element channel = document.selectFirst("feed");
        if(channel == null){
            return;
        }
        Element titleElement = channel.selectFirst("title");
        if(titleElement != null){
            feed.setTitle(titleElement.text());
        }

        Element lastBuildDate = channel.selectFirst("updated");
        if(lastBuildDate != null){
            feed.setLastBuildDate(Parser.parseDate(lastBuildDate.text()));
        }

        Element link = channel.selectFirst("link");
        if(link != null){
            feed.setLink(link.attr("href"));
        }

        Element category = channel.selectFirst("category");
        if(category != null){
            feed.addCategory(category.attr("term"));
        }

        Element guid = channel.selectFirst("id");
        if(guid != null && feed.getLink() == null){
            feed.setLink(guid.text());
        }

        Element image = channel.selectFirst("logo");
        if(image != null){
            feed.setImageUrl(image.text());
        }

        Element icon = channel.selectFirst("icon");
        if(icon != null && feed.getImageUrl() == null){
            feed.setImageUrl(icon.text());
        }

        Element copyright = channel.selectFirst("rights");
        if(copyright != null){
            feed.setCopyright(copyright.text());
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
                item.setLink(linkElement.attr("href"));
            }
            Element guid = itemElement.selectFirst("id");
            if (guid != null) {
                item.setGuid(guid.text());
            }

            Element descElement = itemElement.selectFirst("description");
            if (descElement != null) {
                item.setDescription(descElement.text());
            }
            Element summaryElement = itemElement.selectFirst("summary");
            if (summaryElement != null && item.getDescription() == null) {
                item.setDescription(summaryElement.text());
            }
            Element contentElement = itemElement.selectFirst("content");
            if (contentElement != null && item.getDescription() == null) {
                item.setDescription(contentElement.text());
            }

            Element pubDate = itemElement.selectFirst("published");
            if (pubDate != null) {
                item.setPubDate(Parser.parseDate(pubDate.text()));
            }

            Elements author = itemElement.select("author");
            if (!author.isEmpty()) {
                Element authorName = author.select("name").first();
                if(authorName != null){
                    item.setAuthor(authorName.text());
                }
            }

            Elements categories = itemElement.select("category");
            if (!categories.isEmpty()) {
                for (Element category : categories){
                    item.addCategory(category.attr("term"));
                }
            }

            Element source = itemElement.selectFirst("source");
            if (source != null) {
                Element originalSource = source.selectFirst("id");
                if(originalSource != null){
                    item.setSource(originalSource.text());
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
