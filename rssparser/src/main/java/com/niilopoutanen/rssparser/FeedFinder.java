package com.niilopoutanen.rssparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FeedFinder {
    private URL url;
    private URL result;
    public URL find(String urlStr) throws MalformedURLException, RSSException {
        this.url = new URL(urlStr);
        lookup();

        return result;
    }

    private void lookup() throws RSSException{
        try{
            Document document = WebUtils.connect(url);
            Elements links = document.head().select("link[type=application/rss+xml]");
            if(links.size() > 0){
                result = new URL(links.get(0).attr("href"));
            }
            else{
                fallback();
            }
        }
        catch (IOException e) {
            fallback();
        }
    }

    private void fallback(){
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
                String urlStr = url + path;
                URL url = new URL(urlStr);
                Document document = WebUtils.connect(url);
                boolean urlExists = document.hasText();
                boolean feedExists = WebUtils.isRss(document) || WebUtils.isAtom(document);
                if (feedExists && urlExists) {
                    this.result = url;
                }
            }
            catch (Exception ignored) {
                // Ignore exceptions and try the next URL
            }
        }
    }
}
