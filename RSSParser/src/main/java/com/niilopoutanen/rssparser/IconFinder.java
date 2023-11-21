package com.niilopoutanen.rssparser;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;

public class IconFinder {

    public static String get(URL url){
        URL homePage = getHomePage(url);

        // create an HTTP connection to the website's homepage
        try{
            Document doc = WebUtils.connect(homePage);
            Elements links = doc.select("link[href~=.*\\.(png|webp|jpg|jpeg)][rel~=icon|apple-touch-icon|shortcut icon]");
            if (links.isEmpty()) {
                links = doc.select("meta[property~=og:image], meta[name~=twitter:image]");
            }
            if (links.isEmpty()) {
                return null;
            }

            return getLargestSize(links);
        }
        catch (Exception e) {
            return null;
        }
    }


    private static URL getHomePage(URL url){
        try{
            URL homeUrl = new URL(url.getProtocol() + "://" + url.getHost());
            if (!url.toString().equals(homeUrl + "/")) {
                return homeUrl;
            }
            return url;
        }
        catch (Exception e){
            return url;
        }
    }

    private static String getLargestSize(Elements elements){
        String largestUrl = null;
        int maxRes = 0;

        for(Element element : elements){
            Attributes attributes = element.attributes();
            String sizes = attributes.get("sizes");

            int size = extractSize(sizes);

            if(size > maxRes){
                maxRes = size;
                largestUrl = attributes.get("href");
            }
        }
        return largestUrl;
    }


    private static int extractSize(String attr){
        String[] sizeTokens = attr.split("x");
        if(sizeTokens.length == 2){
            try{
                return Integer.parseInt(sizeTokens[0]);
            }
            catch (Exception ignored){ }
        }

        return 0;
    }
}
