package com.niilopoutanen.rss_feed.parser;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;

public class IconFinder {

    public static String get(String urlStr, String[] existingURLs){
        try{
            URL url = new URL(urlStr);
            return load(url, existingURLs);
        }
        catch (Exception e){
            return null;
        }
    }
    public static String get(URL url){
        return load(url, null);
    }
    public static String get(String urlStr){
        try{
            URL url = new URL(urlStr);
            return load(url, null);
        }
        catch (Exception e){
            return null;
        }
    }


    public static String load(URL url, String[] existingURLs){
        URL homePage = getHomePage(url);

        // create an HTTP connection to the website's homepage
        try{
            Document doc = WebUtils.connect(homePage);
            Elements links = doc.select("link[href~=.*\\.(png|webp|jpg|jpeg)][rel~=icon|apple-touch-icon|shortcut icon]");
            if (links.isEmpty()) {
                links = doc.select("meta[property~=og:image], meta[name~=twitter:image]");
            }
            if(existingURLs != null){
                for (String iconUrl : existingURLs){
                    Element link = new Element("link");
                    link.attr("href", iconUrl);
                    links.add(link);
                }
            }

            if (links.isEmpty()) {
                return null;
            }

            String iconUrl = getLargestSize(links);

            // Make sure the icon url is not relative
            if(!iconUrl.startsWith("http")){
                iconUrl = homePage.toString() + iconUrl;
            }

            return iconUrl;
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

            if(size >= maxRes){
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
