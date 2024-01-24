package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.widget.TextView;

import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.R;

public class SourceHeader extends FeedItem{
    public SourceHeader(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_sourceheader;
    }

    @Override
    public void bind(Object data) {
        if(data instanceof Source){
            Source source = (Source) data;
            TextView title = getContent().findViewById(R.id.source_header_title);
            title.setText(source.title);
        }
    }
}
