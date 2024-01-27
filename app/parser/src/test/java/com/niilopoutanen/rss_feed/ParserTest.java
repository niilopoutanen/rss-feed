package com.niilopoutanen.rss_feed;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.niilopoutanen.rss_feed.parser.FeedFinder;
import com.niilopoutanen.rss_feed.parser.Parser;
import com.niilopoutanen.rss_feed.parser.RSSException;
import com.niilopoutanen.rss_feed.parser.WebUtils;
import com.niilopoutanen.rss_feed.rss.Post;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

class ParserTest {
    @Test
    void loadFromUrl(){
        Parser parser = new Parser();
        parser.load("https://www.9to5mac.com/feed");
        Post post = parser.posts.get(1);
        Assertions.assertNotNull(post.image);
    }

    @Test
    void isRss(){
        String rss = "https://www.9to5mac.com/feed";
        try{
            Assertions.assertTrue(WebUtils.isRss(WebUtils.connect(new URL(rss))));
        }
        catch (Exception e){
            Assertions.fail(e);
        }
    }

    @Test
    void findFeeds() throws RSSException {
        String[] urls = new String[]{"www.9to5mac.com", "https://www.youtube.com/@Apple", "iltalehti.fi", "hs.fi"};
        for(String url : urls){
            FeedFinder feedFinder = new FeedFinder();
            URL result = feedFinder.find(url);
            Assertions.assertNotNull(result);
        }
    }
}