package com.niilopoutanen.rssparser;

import com.niilopoutanen.rssparser.*;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void loadFromUrl(){
        Parser parser = new Parser();
        Feed feed;
        try {
            feed = parser.load("https://www.9to5mac.com/feed");
            Item item = feed.getItemAt(1);
            assertNotNull(item.getImageUrl());
            assertFalse(feed.getItems().isEmpty());
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