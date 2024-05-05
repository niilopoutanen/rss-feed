package com.niilopoutanen.rss_feed.parser;

import android.graphics.Path;

import androidx.annotation.NonNull;

import com.niilopoutanen.rss_feed.parser.parsers.AtomParser;
import com.niilopoutanen.rss_feed.parser.parsers.RssParser;
import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;
import com.niilopoutanen.rss_feed.resources.R;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParserGen2 {
    public Source source;
    public List<Post> posts;
    private final StatusBridge statusBridge;

    public ParserGen2(){

        this.statusBridge = new StatusBridge() {
            @Override
            public void onProgress(String msg) {}

            @Override
            public void onProgress(int stringRes) {}

            @Override
            public void onSuccess(String msg) {}

            @Override
            public void onSuccess(int stringRes) {}

            @Override
            public void onFailure(String msg) {}

            @Override
            public void onFailure(int stringRes) {}
        };
    }
    public ParserGen2(@NonNull StatusBridge statusBridge){
        this.statusBridge = statusBridge;
    }

    public void get(String url){
        if(url == null || url.isEmpty()) return;
        statusBridge.onProgress(R.string.status_url_check);
        if(!FeedFinder.isValidFeed(url)) {
            statusBridge.onFailure(R.string.error_url);
            return;
        }

        statusBridge.onProgress(R.string.status_connecting_url);
        Document document = WebUtils.connect(url);
        if(document != null){
            parse(document);
        }
        else{
            statusBridge.onFailure(R.string.error_url_empty);
        }
    }

    private void parse(@NonNull Document document){
        statusBridge.onProgress(R.string.status_parsing);
        if(WebUtils.isRss(document)){
            RssParser rssParser = new RssParser();
            rssParser.parse(document);
            source = rssParser.getSource();
            posts = rssParser.getPosts();
        }
        else if(WebUtils.isAtom(document)){
            AtomParser atomParser = new AtomParser();
            atomParser.parse(document);
            source = atomParser.getSource();
            posts = atomParser.getPosts();
        }
        else{
            statusBridge.onFailure(R.string.error_feed_format);
        }
    }

    public static List<Post> loadMultiple(List<Source> sources){
        List<Post> posts = new ArrayList<>();
        for(Source source : sources){
            if(source == null || !source.visible) continue;
            ParserGen2 parser = new ParserGen2();
            parser.get(source.url);
            posts.addAll(parser.posts);
        }
        Collections.sort(posts);
        return posts;
    }

    public static Date parseDate(String dateString){
        List<DateTimeFormatter> formats = new ArrayList<>();
        formats.add(DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH));
        formats.add(DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss zzz", Locale.ENGLISH));
        formats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        formats.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

        for(DateTimeFormatter formatter : formats){
            try{
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                return Date.from(zonedDateTime.toInstant());
            }
            catch (DateTimeParseException ignored){}
        }

        return null;
    }
    public static String parsePattern(String raw, String attribute){
        String regexPattern = attribute + "=\"(.*?)\"";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(raw);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public static String trim(String original, int maxLength) {
        if (original == null || original.length() <= maxLength) {
            return original;
        } else {
            return original.substring(0, maxLength - 3) + "...";
        }
    }
}
