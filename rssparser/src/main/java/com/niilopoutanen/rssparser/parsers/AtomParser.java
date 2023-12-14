package com.niilopoutanen.rssparser.parsers;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rssparser.NewParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AtomParser extends ParserBase {


    private void parseSource(Document document){
        Element channel = document.selectFirst("feed");
        if(channel == null){
            return;
        }
        Element titleElement = channel.selectFirst("title");
        if(titleElement != null){
            source.title = titleElement.text();
        }

        Element link = channel.selectFirst("link");
        if(link != null){
            source.url = link.attr("href");
        }

        Element guid = channel.selectFirst("id");
        if(guid != null && source.url == null){
            source.url = guid.text();
        }

        Element image = channel.selectFirst("logo");
        if(image != null){
            source.image = image.text();
        }

        Element icon = channel.selectFirst("icon");
        if(icon != null && source.image == null){
            source.image = icon.text();
        }
    }
    private void parsePosts(Document document){
        Elements itemObjects = document.select("entry");
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
                post.pubDate = NewParser.parseDate(pubDate.text());
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


            if(post.author == null){
                post.author = source.title;
            }

            posts.add(post);
        }
    }

}
