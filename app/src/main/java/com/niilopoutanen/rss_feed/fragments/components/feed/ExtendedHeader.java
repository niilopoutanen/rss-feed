package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.niilopoutanen.rss_feed.common.IconView;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.SearchBar;
import com.niilopoutanen.rss_feed.rss.Source;

import java.util.function.Consumer;

public class ExtendedHeader extends FeedItem{
    public ExtendedHeader(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_extendedheader;
    }

    @Override
    public void onClick(Object data) {}
    @Override
    public void bind(Object data) {
        if(data instanceof Source){
            Source source = (Source) data;
            TextView title = getContent().findViewById(R.id.extended_header_title);
            TextView desc = getContent().findViewById(R.id.extended_header_desc);
            IconView icon = getContent().findViewById(R.id.extended_header_icon);

            if(source.title != null && !source.title.isEmpty()){
                title.setText(source.title);
            }
            else{
                title.setVisibility(View.GONE);
            }

            if(source.description != null && !source.description.isEmpty()){
                desc.setText(source.description);
            }
            else{
                desc.setVisibility(View.GONE);
            }

            icon.setResource(source.image);
            icon.setName(source.title);
            icon.setOnClickListener(v -> {
                if(source.home == null || source.home.isEmpty()) return;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(source.home));
                context.startActivity(browserIntent);
            });

            if(messageBridge != null){
                SearchBar searchBar = getContent().findViewById(R.id.feed_searchbar);
                searchBar.setQueryHandler(query -> messageBridge.onQueryChanged(query));
            }

            View menuButton = getContent().findViewById(R.id.feed_dropdown);
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
        }
    }
}
