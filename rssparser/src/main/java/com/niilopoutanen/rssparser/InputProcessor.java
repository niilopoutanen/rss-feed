package com.niilopoutanen.rssparser;

import com.niilopoutanen.rss.Source;

public class InputProcessor {
    private Source input;
    private Source result = new Source();
    public void validate(Source input) throws RSSException{
        this.input = input;

        locateAddress();
        parseDetails();
        loadIcon();
    }
    public Source getResult(){
        return this.result;
    }
    private void loadIcon(){
        if(result.image == null || result.image.isEmpty()){
            result.image = IconFinder.get(input.url);
        }
    }
    private void locateAddress() throws RSSException {
        FeedFinder feedFinder = new FeedFinder();
        feedFinder.find(input.url);

        if(feedFinder.getResult() != null && !feedFinder.getResult().toString().isEmpty()){
            input.url = feedFinder.getResult().toString();
        }
    }
    private void parseDetails(){
        Parser parser = new Parser();
        parser.load(input.url);
        this.result = parser.source;
    }
}
