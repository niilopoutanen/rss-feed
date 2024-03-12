package com.niilopoutanen.rss_feed.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.common.models.FeedResult;
import com.niilopoutanen.rss_feed.fragments.components.SourceItem;

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
        if (holder instanceof SourceItem) {
            ((SourceItem) holder).bindData(result);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

}
