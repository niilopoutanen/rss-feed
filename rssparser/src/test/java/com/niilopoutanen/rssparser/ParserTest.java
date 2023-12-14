package com.niilopoutanen.rssparser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.niilopoutanen.rss.Post;

import org.junit.jupiter.api.Test;

import java.net.URL;

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