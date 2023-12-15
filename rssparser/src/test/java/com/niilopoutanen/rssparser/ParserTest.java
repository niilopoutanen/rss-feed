package com.niilopoutanen.rssparser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.niilopoutanen.rss.Post;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

class ParserTest {
    @Test
    void loadFromUrl(){
        assertTrue(true);
        Parser parser = new Parser();
        parser.load("https://www.9to5mac.com/feed");
        Post post = parser.posts.get(1);
        assertNotNull(post.image);
    }

    @Test
    void isRss(){
        String rss = "https://www.9to5mac.com/feed";
        try{
            assertTrue(WebUtils.isRss(WebUtils.connect(new URL(rss))));
        }
        catch (Exception e){
            fail(e);
        }
    }

    @Test
    void findFeeds() throws RSSException {
        String[] urls = new String[]{"www.9to5mac.com", "https://www.youtube.com/@Apple"};
        for(String url : urls){
            FeedFinder feedFinder = new FeedFinder();
            URL result = feedFinder.find(url);
            assertNotNull(result);
        }
    }
}