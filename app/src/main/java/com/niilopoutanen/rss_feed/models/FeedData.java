package com.niilopoutanen.rss_feed.models;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;
import com.niilopoutanen.rss_feed.fragments.components.Notice;

import java.util.ArrayList;
import java.util.List;

public class FeedData {
    private List<Post> posts = new ArrayList<>();
    private final List<Notice.NoticeData> notices = new ArrayList<>();
    private Source sourceHeader;
    private String header;
    public int count(){
        int count;
        if(notices.size() > 0){
            count = notices.size();
        }
        else{
            count = posts.size();
        }

        int headerCount = 0;
        if(sourceHeader != null || header != null){
            headerCount = 1;
        }
        return count + headerCount;
    }
    public void addPost(Post post){
        this.posts.add(post);
    }
    public void setPosts(List<Post> posts){
        if(posts == null) return;
        this.posts = new ArrayList<>(posts);
        this.clearNotices();
    }
    public void clearPosts(){
        this.posts.clear();
    }
    public void addNotice(String title, String desc){
        this.notices.add(new Notice.NoticeData(title, desc));
    }
    public void clearNotices(){
        this.notices.clear();
    }
    public void setHeader(String title){
        this.header = title;
        this.sourceHeader = null;
    }
    public void setHeader(Source header){
        this.sourceHeader = header;
        this.header = null;
    }
    public Object get(int index){
        if (index == 0){
            if(sourceHeader != null){
                return sourceHeader;
            }
            else if(header != null){
                return header;
            }
        }
        else{
            if(notices.size() > 0 && notices.size() > index){
                return notices.get(index);
            }
            else if(posts.size() > index){
                return posts.get(index);
            }
        }

        return null;
    }

    public int getItemType(int index){
        if (index == 0){
            if(sourceHeader != null){
                return Types.HEADER_SOURCE;
            }
            else if(header != null){
                return Types.HEADER;
            }
        }
        else{
            if(notices.size() > 0 && notices.size() > index){
                return Types.NOTICE;
            }
            else if(posts != null && posts.size() > index){
                return Types.POST;
            }
        }
        return -1;
    }


    public static class Types{
        public static final int HEADER = 0;
        public static final int HEADER_SOURCE = 1;
        public static final int POST = 2;
        public static final int NOTICE = 3;
    }
}

