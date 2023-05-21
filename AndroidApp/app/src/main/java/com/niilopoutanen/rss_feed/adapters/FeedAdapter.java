package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.RSSPost;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.LargeViewHolder> {

    private static final int IMAGE_MARGIN_PX = 20;
    private static int imageWidth;
    private final RecyclerViewInterface recyclerViewInterface;
    private final List<RSSPost> feed;
    private final Preferences preferences;
    private final Context appContext;

    public FeedAdapter(Preferences preferences, List<RSSPost> posts, Context context, RecyclerViewInterface recyclerViewInterface) {
        feed = posts;
        this.recyclerViewInterface = recyclerViewInterface;
        this.preferences = preferences;
        this.appContext = context;
        setImageWidth(appContext);
    }

    public void setImageWidth(Context context) {
        switch (preferences.s_feedcardstyle) {
            case LARGE:
                imageWidth = PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_LARGE, context);
                break;
            case SMALL:
                imageWidth = PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_SMALL, context);
                break;
            case NONE:
                imageWidth = 0;
                break;
        }
    }

    @NonNull
    @Override
    public LargeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        int margin = PreferencesManager.dpToPx(FeedFragment.CARDMARGIN_DP, context);
        int gap = PreferencesManager.dpToPx(FeedFragment.CARDGAP_DP, context);

        view = inflater.inflate(preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE ? R.layout.feedcard : R.layout.feedcard_small, parent, false);
        setViewMargins(view, margin, 0, margin, gap);
        return new LargeViewHolder(view, recyclerViewInterface);
    }

    private void setViewMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void onBindViewHolder(@NonNull LargeViewHolder holder, int position) {
        RSSPost post = feed.get(position);
        TextView title = holder.titleTextView;
        TextView desc = holder.descTextView;
        TextView author = holder.author;
        TextView date = holder.date;
        View container = holder.container;
        ImageView image = holder.image;

        if (!preferences.s_feedcard_authorvisible || !preferences.s_feedcard_datevisible) {
            desc.setMaxLines(3);
        }

        author.setVisibility(preferences.s_feedcard_authorvisible ? View.VISIBLE : View.GONE);
        title.setVisibility(preferences.s_feedcard_titlevisible ? View.VISIBLE : View.GONE);
        desc.setVisibility(preferences.s_feedcard_descvisible ? View.VISIBLE : View.GONE);
        date.setVisibility(preferences.s_feedcard_datevisible ? View.VISIBLE : View.GONE);

        date.setText(PreferencesManager.formatDate(post.getPublishTime(), preferences.s_feedcard_datestyle, holder.titleTextView.getContext()));
        title.setText(post.getTitle());
        desc.setText(post.getDescription());
        if (post.getAuthor() != null && !preferences.s_feedcard_authorname) {
            author.setText(post.getAuthor());
        } else {
            author.setText(post.getSourceName());
        }
        image.setImageDrawable(null);

        Picasso.get().cancelRequest(image);

        if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.NONE) {
            image.setVisibility(View.GONE);
        } else if (post.getImageUrl() == null) {
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            image.setLayoutParams(layoutParams);
        } else {
            if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, IMAGE_MARGIN_PX);
                image.setLayoutParams(layoutParams);
            }

            int targetHeight = 0;
            if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.SMALL) {
                targetHeight = PreferencesManager.dpToPx(100, holder.titleTextView.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, IMAGE_MARGIN_PX, 0);
                image.setLayoutParams(layoutParams);

            }
            RequestCreator requestCreator = Picasso.get().load(post.getImageUrl())
                    .resize(imageWidth, targetHeight)
                    .transform(new MaskTransformation(container.getContext(), R.drawable.image_rounded))
                    .centerCrop();

            if (!preferences.s_imagecache) {
                requestCreator.networkPolicy(NetworkPolicy.NO_STORE);
            }

            requestCreator.into(image);
        }
    }


    @Override
    public int getItemCount() {
        return feed.size();
    }

    public static class LargeViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView descTextView;
        public TextView author;
        public TextView date;
        public ImageView image;
        public View container;

        public LargeViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });

            titleTextView = itemView.findViewById(R.id.feedcard_title);
            descTextView = itemView.findViewById(R.id.feedcard_description);
            author = itemView.findViewById(R.id.feedcard_author);
            date = itemView.findViewById(R.id.feedcard_date);
            image = itemView.findViewById(R.id.feedcard_image);
            container = itemView;

        }

    }

}