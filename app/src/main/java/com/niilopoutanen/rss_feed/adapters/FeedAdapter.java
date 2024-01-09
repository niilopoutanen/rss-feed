package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss.Post;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.ArticleActivity;
import com.niilopoutanen.rss_feed.fragments.components.FeedCard;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewInterface {

    private final int TYPE_NOTICE = 2;
    private List<Post> posts = new ArrayList<>();
    private final Map<String, String> notices = new HashMap<>();
    private final Context context;
    private final Preferences preferences;

    public FeedAdapter(Context context, Preferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    public void update(List<Post> newPosts) {
        this.posts = new ArrayList<>(newPosts);
        notices.clear();
        notifyDataSetChanged();
    }

    public void update() {
        notices.clear();
        notifyDataSetChanged();
    }

    public void addNotification(String text, String desc) {
        if(posts != null) posts.clear();
        notices.clear();
        notices.put(text, desc);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_NOTICE) {
            return createNotice();
        } else {
            return FeedCard.create(parent, preferences, this);
        }
    }

    private RecyclerView.ViewHolder createNotice() {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setTextColor(context.getColor(R.color.textSecondary));
        textView.setTextSize(18);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.inter_semibold));

        TextView textDesc = new TextView(context);
        textDesc.setTextColor(context.getColor(R.color.textSecondary));
        textDesc.setTextSize(15);
        textDesc.setTypeface(ResourcesCompat.getFont(context, R.font.inter));

        container.addView(textView);
        container.addView(textDesc);
        return new RecyclerView.ViewHolder(container) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedCard) {
            if (posts.size() > position) {
                ((FeedCard) holder).bindData(posts.get(position));
            }

        } else {
            LinearLayout container = (LinearLayout) holder.itemView;

            Map.Entry<String, String> entry = notices.entrySet().iterator().next();

            TextView textView = (TextView) container.getChildAt(0);
            textView.setText(entry.getKey());

            TextView textDesc = (TextView) container.getChildAt(1);
            textDesc.setText(entry.getValue());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notices.size() > 0) {
            return TYPE_NOTICE;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        if (notices.size() > 0) {
            return notices.size();
        } else if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    @Override
    public void onItemClick(int position) {
        // Index out of bounds catch
        if (position >= posts.size()) {
            return;
        }
        Post clicked = posts.get(position);
        if (clicked.link != null) {
            Intent articleIntent = new Intent(context, ArticleActivity.class);
            articleIntent.putExtra("preferences", preferences);
            articleIntent.putExtra("post", posts.get(position));

            context.startActivity(articleIntent);
        } else {
            Toast.makeText(context, R.string.error_post_no_url, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
