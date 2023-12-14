package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rssparser.parsers.AtomParser;
import com.niilopoutanen.rssparser.parsers.RssParser;

import org.jsoup.nodes.Document;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.NotImplementedError;

public class Parser {
    public Source source;
    public List<Post> posts = new ArrayList<>();

    public Parser(){

    }
    public void get(String url){
        Document document = WebUtils.connect(url);
        parse(document);
    }
    public void get(List<Source> sources){
        throw new NotImplementedError();
    }

    public void parse(Document document){
        if(WebUtils.isRss(document)){
            RssParser rssParser = new RssParser();
            rssParser.parse(document);
            source = (rssParser.getSource());
            posts = rssParser.getPosts();
        }
        else if(WebUtils.isAtom(document)){
            AtomParser atomParser = new AtomParser();
            atomParser.parse(document);
            source = (atomParser.getSource());
            posts = atomParser.getPosts();
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
