package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AtomParser extends ParserBase {
    private final Feed feed = new Feed();


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
            Post post = new Post();
            Element titleElement = itemElement.selectFirst("title");
            if (titleElement != null) {
                post.title = titleElement.text();
            }

            Element linkElement = itemElement.selectFirst("link");
            if (linkElement != null) {
                post.link = linkElement.attr("href");
            }
            Element guid = itemElement.selectFirst("id");
            if (guid != null && post.link == null) {
                post.link = guid.text();
            }

            Element descElement = itemElement.selectFirst("description");
            if (descElement != null) {
                Element desc = Jsoup.parse(descElement.text()).body();
                post.description = desc.text();
            }
            Element summaryElement = itemElement.selectFirst("summary");
            if (summaryElement != null && post.description == null) {
                post.description = summaryElement.text();
            }
            Element contentElement = itemElement.selectFirst("content");
            if (contentElement != null && post.description == null) {
                post.description = contentElement.text();
            }

            Element pubDate = itemElement.selectFirst("published");
            if (pubDate != null) {
                post.pubDate = Parser.parseDate(pubDate.text());
            }

            Elements author = itemElement.select("author");
            if (!author.isEmpty()) {
                Element authorName = author.select("name").first();
                if(authorName != null){
                    post.author = authorName.text();
                }
            }

            Elements categories = itemElement.select("category");
            if (!categories.isEmpty()) {
                for (Element category : categories){
                    post.addCategory(category.attr("term"));
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
