package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.content.Context;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.SearchBar;

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

        RelativeLayout menuButton = getContent().findViewById(R.id.feed_dropdown);
        menuButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.RSSFeedStyle_PopupMenu);
            PopupMenu popup = new PopupMenu(wrapper, v);
            popup.getMenuInflater().inflate(R.menu.feed_options, popup.getMenu());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true);
            }
            popup.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.feed_menu_sort_new){
                    if(messageBridge != null){
                        messageBridge.onSortingChanged(true);
                        return true;
                    }
                }
                else if (item.getItemId() == R.id.feed_menu_sort_old){
                    if(messageBridge != null){
                        messageBridge.onSortingChanged(false);
                        return true;
                    }
                }
                return false;
            });
            popup.show();
        });

        if(messageBridge != null){
            SearchBar searchBar = getContent().findViewById(R.id.feed_searchbar);
            searchBar.setQueryHandler(query -> messageBridge.onQueryChanged(query));
        }
    }

}
