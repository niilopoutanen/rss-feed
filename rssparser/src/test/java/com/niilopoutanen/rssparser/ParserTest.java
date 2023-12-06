package com.niilopoutanen.rssparser;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

import com.niilopoutanen.rss.Post;

class ParserTest {
    @Test
    void loadFromUrl(){
        Parser parser = new Parser();
        Feed feed;
        try {
            feed = parser.load("https://www.9to5mac.com/feed");
            Post post = feed.getItemAt(1);
            assertNotNull(post.image);
            assertFalse(feed.getPosts().isEmpty());
        }
        catch (RSSException e) {
            e.printStackTrace();
            fail();
        }
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
}