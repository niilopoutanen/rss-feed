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

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RSSPost;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ItemViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int IMAGE_MARGIN_PX = 20;
    private static int imageWidth;
    private final RecyclerViewInterface recyclerViewInterface;
    private final List<RSSPost> feed;
    private final String viewTitle;
    private final Preferences preferences;
    private final Context appContext;

    public FeedAdapter(Preferences preferences, List<RSSPost> posts, Context context, String viewTitle, RecyclerViewInterface recyclerViewInterface) {
        feed = posts;
        this.viewTitle = viewTitle;
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
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        int margin = PreferencesManager.dpToPx(FeedFragment.CARDMARGIN_DP, context);
        int gap = PreferencesManager.dpToPx(FeedFragment.CARDGAP_DP, context);
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.header_feed, parent, false);
                setViewMargins(view, margin, margin, margin, margin);
                return new HeaderViewHolder(view);
            case VIEW_TYPE_ITEM:
                view = inflater.inflate(preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE ? R.layout.feedcard : R.layout.feedcard_small, parent, false);
                setViewMargins(view, margin, 0, margin, gap);
                return new ItemViewHolder(view, recyclerViewInterface);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    private void setViewMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header.setText(viewTitle);
            return;
        }
        RSSPost post = feed.get(position - 1);
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
        if (feed.isEmpty()) {
            return 1;
        } else {
            return feed.size() + 1;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTextView;
        public final TextView descTextView;
        public final TextView author;
        public final TextView date;
        public final ImageView image;
        public final View container;

        public ItemViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition() - 1;
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

    public static class HeaderViewHolder extends ItemViewHolder {

        public final TextView header;

        public HeaderViewHolder(View itemView) {
            super(itemView, null);

            header = itemView.findViewById(R.id.feed_header);
        }
    }
}