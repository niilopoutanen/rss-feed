package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.content.Context;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.SearchBar;

import java.util.function.Consumer;

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

    public void setQueryHandler(Consumer<String> queryHandler){
        SearchBar searchBar = getContent().findViewById(R.id.feed_searchbar);
        searchBar.setQueryHandler(queryHandler);
    }
    @Override
    public void bind(Object text) {
        if(text instanceof String){
            TextView title = getContent().findViewById(R.id.feed_title);
            title.setText((String)text);
            PreferencesManager.setHeader(context, title);
        }

        RelativeLayout menuButton = getContent().findViewById(R.id.feed_dropdown);
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.feed_options, popup.getMenu());
            popup.show();
        });
    }
}
