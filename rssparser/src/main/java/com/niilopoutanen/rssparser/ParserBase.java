package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss.Source;

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

    private void parseSource(Document document){

    }
    private void parsePosts(Document document){

    }
}
