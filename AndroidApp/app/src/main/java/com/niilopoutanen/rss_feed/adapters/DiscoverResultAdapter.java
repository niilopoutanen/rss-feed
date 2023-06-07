package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.models.FeedResult;
import com.niilopoutanen.rss_feed.models.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.utils.WebHelper;

import java.util.List;

public class DiscoverResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FeedResult> results;

    public DiscoverResultAdapter(List<FeedResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.discover_result_item, parent, false);

        return new ItemViewHolder(view);
    }

    public void setResults(List<FeedResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedResult result = results.get(position);
        Context context = holder.itemView.getContext();

        TextView title = ((ItemViewHolder) holder).title;
        TextView desc = ((ItemViewHolder) holder).desc;
        RelativeLayout add = ((ItemViewHolder) holder).addBtn;

        View icon = add.findViewById(R.id.discover_result_add_icon);
        Drawable plus = AppCompatResources.getDrawable(context, R.drawable.icon_plus);
        icon.setBackground(plus);

        List<Source> savedSources = SaveSystem.loadContent(context);
        for (Source source : savedSources) {
            if (source.getFeedUrl().equalsIgnoreCase(WebHelper.formatUrl(result.feedId).toString())) {
                result.alreadyAdded = true;
                Drawable checkmark = AppCompatResources.getDrawable(context, R.drawable.icon_checkmark);
                icon.setBackground(checkmark);
            }
        }

        title.setText(result.title);
        desc.setText(result.description);
        add.setOnClickListener(v -> {
            if (!result.alreadyAdded) {
                SaveSystem.saveContent(v.getContext(), new Source(result.title, WebHelper.formatUrl(result.feedId).toString(), result.iconUrl));
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.sourceadded), Toast.LENGTH_LONG).show();
                Drawable checkmark = AppCompatResources.getDrawable(context, R.drawable.icon_checkmark);
                icon.setBackground(checkmark);
            } else {
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.sourcealreadyadded), Toast.LENGTH_LONG).show();
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FeedActivity.class);
            Source tempSource = new Source(result.title, WebHelper.formatUrl(result.feedId).toString(), result.visualUrl);
            intent.putExtra("source", tempSource);
            intent.putExtra("preferences", PreferencesManager.loadPreferences(v.getContext()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView desc;
        final RelativeLayout addBtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.discover_result_title);
            desc = itemView.findViewById(R.id.discover_result_desc);
            addBtn = itemView.findViewById(R.id.discover_result_add);
        }
    }
}
