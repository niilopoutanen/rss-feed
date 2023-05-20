package com.niilopoutanen.rss_feed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.FeedResult;

import java.util.List;

public class DiscoverResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FeedResult> results;
    public DiscoverResultAdapter(List<FeedResult> results){
        this.results = results;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.discover_result_item, parent, false);

        return new ItemViewHolder(view);
    }
    public void setResults(List<FeedResult> results){
        this.results = results;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedResult result = results.get(position);

        TextView title = ((ItemViewHolder)holder).title;
        TextView desc = ((ItemViewHolder)holder).desc;

        title.setText(result.title);
        desc.setText(result.description);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.discover_result_title);
            desc = itemView.findViewById(R.id.discover_result_desc);
        }
    }
}
