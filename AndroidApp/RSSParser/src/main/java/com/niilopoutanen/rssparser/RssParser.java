package com.niilopoutanen.rssparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class RssParser {
    private final HashMap<String, BiConsumer<Feed, String>> feedElements = new HashMap<>();
    private final HashMap<String, BiConsumer<Item, String>> itemElements = new HashMap<>();
    private final Feed feed = new Feed();
    public RssParser init(){
        matchFeedElements();
        matchItemElements();
        return this;
    }
    public Feed parse(Document document){
        parseFeed(document);
        return this.feed;
    }
    private void parseFeed(Document document){
        Element channel = document.select("channel").first();

        if (channel != null) {
            for (String tagName : feedElements.keySet()) {
                Element element = channel.select(tagName).first();
                if (element != null) {
                    String text = element.text();
                    feedElements.get(tagName).accept(feed, text);
                }
            }

            Elements items = channel.select("item");
            parseItems(items);
        }
    }

    private void parseItems(Elements itemObjects){
        for (Element itemElement : itemObjects) {
            Item item = new Item();
            for (String tagName : itemElements.keySet()) {
                Element element = itemElement.select(tagName).first();
                if (element != null) {
                    if(element.tagName().matches("enclosure|source")){
                        itemElements.get(tagName).accept(item, element.toString());
                        continue;
                    }
                    String text = element.text();
                    itemElements.get(tagName).accept(item, text);
                }
            }


            handleNullParams(item);
            handleSpecialCases(itemElement, item);
            feed.addItem(item);
        }
    }
    private void handleSpecialCases(Element itemElement, Item item){
        Element sourceElement = itemElement.selectFirst("source");
        if (sourceElement != null) {
            String parsed = Parser.parsePattern(sourceElement.toString(), "url");
            if (!parsed.isEmpty()) {
                item.setSource(parsed);
            }
        }

        Element contentEncoded = itemElement.selectFirst("content|encoded");
        if (contentEncoded != null) {
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
    }
    private void handleNullParams(Item item){
        if(item.getAuthor() == null){
            item.setAuthor(feed.getTitle());
        }
    }
    private void matchFeedElements() {
        feedElements.put("title", Feed::setTitle);
        feedElements.put("link", Feed::setLink);
        feedElements.put("description", Feed::setDescription);
        feedElements.put("language", Feed::setLanguage);
        feedElements.put("copyright", Feed::setCopyright);
        feedElements.put("managingEditor", Feed::setManagingEditor);
        feedElements.put("webMaster", Feed::setWebMaster);
        feedElements.put("pubDate", Feed::setPubDate);
        feedElements.put("lastBuildDate", Feed::setLastBuildDate);
        feedElements.put("category", Feed::addCategory);
        feedElements.put("generator", Feed::setGenerator);
        feedElements.put("docs", Feed::setDocs);
        feedElements.put("cloud", Feed::setCloud);
        feedElements.put("ttl", Feed::setTtl);
        feedElements.put("rating", Feed::setRating);
    }

    private void matchItemElements(){
        itemElements.put("title", Item::setTitle);
        itemElements.put("link", Item::setLink);
        itemElements.put("description", Item::setDescription);
        itemElements.put("summary", Item::setDescription);
        itemElements.put("content", Item::setDescription);
        itemElements.put("author", Item::setAuthor);
        itemElements.put("creator", Item::setAuthor);
        itemElements.put("dc|creator", Item::setAuthor);
        itemElements.put("category", Item::addCategory);
        itemElements.put("comments", Item::setComments);
        itemElements.put("guid", Item::setGuid);
        itemElements.put("id", Item::setGuid);
        itemElements.put("pubDate", Item::setPubDate);
        itemElements.put("media|thumbnail", Item::setImageUrl);
    }
}
