package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;

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
    public void onClick() {

    }

    @Override
    public void bind(Object data) {
        if(data instanceof String[]){
            String[] notice = (String[]) data;
            TextView header = getContent().findViewById(R.id.feed_notice_header);
            TextView text = getContent().findViewById(R.id.feed_notice_text);

            header.setText(notice[0]);
            text.setText(notice[1]);
        }

    }
}
