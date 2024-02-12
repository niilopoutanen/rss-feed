package com.niilopoutanen.rss_feed.parser.parsers;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class ParserBase {
    protected final Source source = new Source();
    protected final List<Post> posts = new ArrayList<>();


    public Source getSource(){
        return this.source;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void parse(Document document){
        parseSource(document);
        parsePosts(document);
    }

    protected abstract void parseSource(Document document);
    protected abstract void parsePosts(Document document);
}
