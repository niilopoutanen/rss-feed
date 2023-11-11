package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rssparser.Item;

public class FeedCard extends RecyclerView.ViewHolder{
    private final TextView title;
    private final TextView desc;
    private final TextView author;
    private final TextView date;
    private final ImageView image;
    private final View container;
    public FeedCard(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.feedcard_title);
        desc = itemView.findViewById(R.id.feedcard_description);
        author = itemView.findViewById(R.id.feedcard_author);
        date = itemView.findViewById(R.id.feedcard_date);
        image = itemView.findViewById(R.id.feedcard_image);
        container = itemView;
    }

    public static FeedCard create(ViewGroup parent, Preferences preferences) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        switch (preferences.s_feedcardstyle){
            case SMALL:
                view = inflater.inflate(R.layout.feedcard_small, parent, false);
                break;
            case NONE:
                view = inflater.inflate(R.layout.feedcard_small, parent, false);
                view.findViewById(R.id.feedcard_image).setVisibility(View.GONE);
                break;

            case LARGE:
            default:
                view = inflater.inflate(R.layout.feedcard, parent, false);
                break;
        }
        return new FeedCard(view);
    }

    public void bindData(Item item){
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        date.setText(item.getPubDate().toString());
        author.setText(item.getAuthor());
    }
}
