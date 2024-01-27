package com.niilopoutanen.rss_feed.parser;

public class RSSException extends Exception{
    private final int errorType;
    public RSSException(int errorType, String message){
        super(message);
        this.errorType = errorType;
    }

    public RSSException(String message){
        super(message);
        this.errorType = -1;
    }


    public int getErrorType() {
        return errorType;
    }
}
