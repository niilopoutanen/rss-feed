package com.niilopoutanen.rss_feed.fragments.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.activities.MainActivity;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

public class NewFeedCard extends FeedItem{
    public NewFeedCard(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feedcard;
    }

    @Override
    public void bind(Object data) {
        if(data instanceof Post){
            Post post = (Post)data;
            TextView title = getContent().findViewById(R.id.feedcard_title);
            TextView description = getContent().findViewById(R.id.feedcard_description);
            TextView author = getContent().findViewById(R.id.feedcard_author);
            TextView date = getContent().findViewById(R.id.feedcard_date);
            ImageView image = getContent().findViewById(R.id.feedcard_image);

            if (post.title != null && !post.title.isEmpty() && preferences.s_feedcard_titlevisible) {
                title.setText(post.title);
            } else {
                title.setVisibility(View.GONE);
            }

            if (post.description != null && !post.description.isEmpty() && preferences.s_feedcard_descvisible) {
                description.setText(post.description);
            } else {
                description.setVisibility(View.GONE);
            }

            if (post.pubDate != null && preferences.s_feedcard_datevisible) {
                date.setText(PreferencesManager.formatDate(post.pubDate, preferences.s_feedcard_datestyle, context));
            } else {
                date.setVisibility(View.GONE);
            }

            if (post.author != null && !post.author.isEmpty() && preferences.s_feedcard_authorvisible) {
                author.setText(post.author);
            } else {
                author.setVisibility(View.GONE);
            }

            if (post.image != null && !post.image.isEmpty() && preferences.s_feedcardstyle != Preferences.FeedCardStyle.NONE) {
                Picasso.get().load(post.image).into(image);
            } else {
                image.setVisibility(View.GONE);
            }

            if(preferences.s_feedcardstyle == Preferences.FeedCardStyle.SMALL){
                setHorizontalStyle();
            }
        }
    }


    private void setHorizontalStyle(){
        ImageView image = getContent().findViewById(R.id.feedcard_image);
        int imageSize = PreferencesManager.dpToPx(100, context);
        image.setLayoutParams(new FrameLayout.LayoutParams(imageSize, ViewGroup.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        CardView imageContainer = getContent().findViewById(R.id.feedcard_image_container);
        LinearLayout.MarginLayoutParams cardParams = (LinearLayout.MarginLayoutParams) imageContainer.getLayoutParams();
        cardParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        cardParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        cardParams.setMargins(0,0,PreferencesManager.dpToPx(10, context),0);
        imageContainer.setLayoutParams(cardParams);

        LinearLayout container = getContent().findViewById(R.id.feedcard_container);
        container.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = getContent().findViewById(R.id.feedcard_title);
        title.setMaxLines(2);
    }
}
