package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.models.Content;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.WebHelper;

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
        RelativeLayout add = ((ItemViewHolder)holder).addBtn;

        title.setText(result.title);
        desc.setText(result.description);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.itemView.setOnClickListener(v -> {
            Context context = title.getContext();
            Intent intent = new Intent(context, FeedActivity.class);
            Content tempSource = new Content(result.title, WebHelper.formatUrl(result.feedId).toString(), result.visualUrl);
            intent.putExtra("source", tempSource);
            intent.putExtra("preferences", PreferencesManager.loadPreferences(context));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        RelativeLayout addBtn;
        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.discover_result_title);
            desc = itemView.findViewById(R.id.discover_result_desc);
            addBtn = itemView.findViewById(R.id.discover_result_add);
        }
    }
}
