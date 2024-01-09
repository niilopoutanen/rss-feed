package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.activities.MainActivity;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class NewFeedCard extends FeedItem{
    public NewFeedCard(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        switch (preferences.s_feedcardstyle) {
            case SMALL:
            case NONE:
                return R.layout.feedcard_small;
            case LARGE:
            default:
                return R.layout.feedcard;
        }
    }

    @Override
    public void bind(Post post) {
        TextView title = getContent().findViewById(R.id.feedcard_title);
        TextView description = getContent().findViewById(R.id.feedcard_description);

        title.setText(post.title);
        description.setText(post.description);
    }
}
