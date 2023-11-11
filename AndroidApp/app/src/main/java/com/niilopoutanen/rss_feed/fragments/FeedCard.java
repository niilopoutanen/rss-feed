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
import com.niilopoutanen.rss_feed.models.Preferences.FeedCardStyle;
import com.niilopoutanen.rssparser.Item;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class FeedCard extends RecyclerView.ViewHolder{
    private final TextView titleTextView;
    private final TextView descTextView;
    private final TextView author;
    private final TextView date;
    private final ImageView image;
    private final View container;
    public FeedCard(@NonNull View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.feedcard_title);
        descTextView = itemView.findViewById(R.id.feedcard_description);
        author = itemView.findViewById(R.id.feedcard_author);
        date = itemView.findViewById(R.id.feedcard_date);
        image = itemView.findViewById(R.id.feedcard_image);
        container = itemView;
    }

    public static FeedCard create(ViewGroup parent, Preferences preferences) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        view = inflater.inflate(preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE ? R.layout.feedcard : R.layout.feedcard_small, parent, false);
        return new FeedCard(view);
    }

    public void bindData(Item item){
        titleTextView.setText(item.getTitle());
        descTextView.setText(item.getDescription());
        
    }
}
