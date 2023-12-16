package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Source;

public class InputProcessor {
    private Source input;
    private Source result = new Source();
    public void validate(Source input){
        this.input = input;
    }
    public Source getResult(){
        return this.result;
    }
    private void fetchIcon(){
        IconFinder.get(input.url);
    }
    private void fetchFeed() throws RSSException {
        FeedFinder feedFinder = new FeedFinder();
        feedFinder.find(input.url);

        if(feedFinder.getResult() != null && !feedFinder.getResult().toString().isEmpty()){
            result.url = feedFinder.getResult().toString();
        }
    }
}
