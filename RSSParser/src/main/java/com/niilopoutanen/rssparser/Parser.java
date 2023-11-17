package com.niilopoutanen.rssparser;

import org.jsoup.nodes.Document;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private Feed feed = new Feed();

    public Parser(){ }
    public Parser(String url){
        load(url, null);
    }


    public Feed load(String url) throws RSSException {
        try{
            Document document = WebUtils.connect(new URL(url));
            parse(document);
        }
        catch (RSSException r){
            throw r;
        }
        catch (Exception e){
            throw new RSSException(e.getMessage());
        }

        return feed;
    }
    public void load(String url, Callback<Feed> callback){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                Document document = WebUtils.connect(new URL(url));
                parse(document);
                if (callback != null) callback.onResult(feed);
            }
            catch (Exception e){
                if (callback != null) callback.onError(new RSSException(e.getMessage()));
            }
        });
    }

    private void parse(Document document){
        if(WebUtils.isRss(document)){
            RssParser rssParser = new RssParser().init();
            this.feed = rssParser.parse(document);
        }
        else if(WebUtils.isAtom(document)){
            AtomParser atomParser = new AtomParser().init();
            this.feed = atomParser.parse(document);
        }
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
}
