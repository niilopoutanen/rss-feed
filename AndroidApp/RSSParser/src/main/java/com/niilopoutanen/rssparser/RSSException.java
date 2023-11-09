package com.niilopoutanen.rssparser;

import java.net.HttpURLConnection;

public class RSSException extends Exception{
    private final int errorType;
    public RSSException(int errorType, String message){
        super(message);
        this.errorType = errorType;
    }

    public RSSException(String message){
        super(message);
        this.errorType = 0;
    }


    public int getErrorType() {
        return errorType;
    }
}
