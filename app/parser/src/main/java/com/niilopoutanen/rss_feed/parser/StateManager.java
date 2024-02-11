package com.niilopoutanen.rss_feed.parser;

public class StateManager {
    public static void notify(StateCallback callback, StatusMessage statusMessage){
        if(callback != null && statusMessage != null){
            callback.onStatusUpdate(statusMessage);
        }
    }
    public static class StatusMessage{
        public enum Type{
            SUCCESS, NEUTRAL, ERROR
        }

        public String msg;
        public Type type;

        public StatusMessage(String msg, Type type){
            this.type = type;
            this.msg = msg;
        }
    }
}
