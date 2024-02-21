package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.widget.Filter;
import android.widget.Filterable;

import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.rss.Source;


import java.util.ArrayList;
import java.util.List;

public class FeedData implements Filterable{
    private List<Post> posts = new ArrayList<>();
    private List<Post> filteredPosts = new ArrayList<>();
    private final List<Notice.NoticeData> notices = new ArrayList<>();
    private Source sourceHeader;
    private String header;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence query) {
                FilterResults results = new FilterResults();
                List<Post> filteredList = new ArrayList<>();

                if(query == null || query.toString().length() == 0){
                    filteredList.addAll(posts);
                }
                else{
                    for (Post post : posts) {
                        if (post.title.toLowerCase().contains(query.toString().toLowerCase())) {
                            filteredList.add(post);
                        }
                    }
                }


                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPosts.clear();
                filteredPosts.addAll((List<Post>) results.values);
            }
        };
    }

    public int count(){
        int count;
        if(notices.size() > 0){
            count = notices.size();
        }
        else{
            count = filteredPosts.size();
        }

        int headerCount = 0;
        if(sourceHeader != null || header != null){
            headerCount = 1;
        }
        return count + headerCount;
    }
    public void setPosts(List<Post> posts){
        if(posts == null) return;
        this.posts = new ArrayList<>(posts);
        this.filteredPosts = new ArrayList<>(this.posts);
        this.clearNotices();
    }
    public void clearPosts(){
        this.posts.clear();
    }
    public void addNotice(String title, String desc){

        for(Notice.NoticeData notice : notices){
            if(notice.title.equals(title)){
                return;
            }
        }
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
            index--;
            if(notices.size() > 0 && notices.size() > index){
                return notices.get(index);
            }
            else if(filteredPosts.size() > index){
                return filteredPosts.get(index);
            }
        }

        return null;
    }

    public int getItemType(int index) {
        Object item = get(index);

        if (item instanceof Source) {
            return Types.HEADER_EXTENDED;
        }
        else if (item instanceof String) {
            return Types.HEADER;
        }
        else if (item instanceof Notice.NoticeData) {
            return Types.NOTICE;
        }
        else if (item instanceof Post) {
            return Types.POST;
        }

        return -1;
    }




    public static class Types{
        public static final int HEADER = 0;
        public static final int HEADER_EXTENDED = 1;
        public static final int POST = 2;
        public static final int NOTICE = 3;
    }
}

