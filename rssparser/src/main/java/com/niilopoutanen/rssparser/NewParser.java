package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rssparser.parsers.AtomParser;
import com.niilopoutanen.rssparser.parsers.RssParser;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class NewParser {
    public Source source;
    public List<Post> posts = new ArrayList<>();

    public NewParser(){

    }

    public void get(String url){
        Document document = WebUtils.connect(url);
    }

    public void parse(Document document){
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
    }
}
