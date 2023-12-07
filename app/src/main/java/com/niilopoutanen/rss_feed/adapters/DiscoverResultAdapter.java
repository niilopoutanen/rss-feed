package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.fragments.FeedCard;
import com.niilopoutanen.rss_feed.fragments.SourceItem;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rssparser.WebUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscoverResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FeedResult> results;

    public DiscoverResultAdapter(@NonNull List<FeedResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SourceItem.create(parent);
    }

    public void setResults(List<FeedResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedResult result = results.get(position);
        if(holder instanceof SourceItem){
            ((SourceItem)holder).bindData(result);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

}
