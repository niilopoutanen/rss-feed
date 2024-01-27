package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.models.FeedData;

import java.util.List;

import kotlin.NotImplementedError;

public class Notice extends FeedItem{
    public Notice(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_notice;
    }

    @Override
    public void onClick(Object data) {

    }

    @Override
    public void bind(Object data) {
        if(data instanceof NoticeData){
            NoticeData notice = (NoticeData) data;
            TextView header = getContent().findViewById(R.id.feed_notice_header);
            TextView text = getContent().findViewById(R.id.feed_notice_text);

            header.setText(notice.title);
            text.setText(notice.desc);
        }

    }
    public static class NoticeData{
        public final String title;
        public final String desc;
        public NoticeData(String title, String desc){
            this.title = title;
            this.desc = desc;
        }
    }
}
