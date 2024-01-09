package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;

public class Notice extends FeedItem{
    public Notice(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_notice;
    }

    @Override
    public void bind(Post post) {
        TextView header = getContent().findViewById(R.id.feed_notice_header);
        TextView text = getContent().findViewById(R.id.feed_notice_text);

        header.setText(post.title);
        text.setText(post.description);
    }
}
