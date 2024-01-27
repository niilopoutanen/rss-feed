package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class Header extends FeedItem{
    public Header(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_header;
    }

    @Override
    public void onClick(Object data) {

    }

    @Override
    public void bind(Object text) {
        if(text instanceof String){
            TextView title = getContent().findViewById(R.id.feed_title);
            title.setText((String)text);
            PreferencesManager.setHeader(context, title);
        }
    }
}
