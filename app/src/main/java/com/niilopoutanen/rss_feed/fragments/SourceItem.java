package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.activities.MainActivity;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Item;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class SourceItem extends RecyclerView.ViewHolder{
    private final TextView title;
    private final TextView desc;
    private final ImageView icon;

    private final Preferences preferences;
    private final Context context;
    public SourceItem(@NonNull View itemView, Preferences preferences, Context context, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.preferences = preferences;
        this.context = context;

        /*
        title = itemView.findViewById(R.id.feedcard_title);
        desc = itemView.findViewById(R.id.feedcard_description);
        icon = itemView.findViewById(R.id.feedcard_image);
        */

        itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position);
                }
            }
        });
    }

    public static SourceItem create(ViewGroup parent, Preferences preferences, RecyclerViewInterface recyclerViewInterface) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        return new SourceItem(view, preferences, context, recyclerViewInterface);
    }

    public void bindData(Source source){
        title.setText(source.getName());
        desc.setVisibility(View.GONE);
    }
    public void bindData(FeedResult result){
        title.setText(result.title);
        desc.setText(result.description);
    }


}