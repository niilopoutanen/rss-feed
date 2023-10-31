package com.niilopoutanen.rss_feed.adapters;

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

import com.niilopoutanen.RSSParser.Item;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
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
    private final List<Item> items;
    private final String viewTitle;
    private final Preferences preferences;
    private final Context appContext;
    private boolean headerVisible = false;
    private Animation scaleUp, scaleDown;

    public FeedAdapter(Preferences preferences, List<Item> items, Context context, String viewTitle, RecyclerViewInterface recyclerViewInterface) {
        this.items = items;
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

    public void complete(boolean empty) {
        this.notifyDataSetChanged();
        this.notifyItemChanged(0);
        if (empty) {
            this.headerVisible = true;
        }

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);

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
            bindHeader(holder);
        } else {
            bindItem(holder, items.get(position - 1));
        }
    }

    private void bindItem(ItemViewHolder holder, Item item) {
        TextView title = holder.titleTextView;
        TextView desc = holder.descTextView;
        TextView author = holder.author;
        TextView date = holder.date;
        View container = holder.container;
        ImageView image = holder.image;


        //region Touch listener
        container.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                container.startAnimation(scaleDown);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                container.startAnimation(scaleUp);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                container.startAnimation(scaleUp);
                view.performClick();
            }
            return true;
        });
        //endregion

        //region Set element visibilities
        if (!preferences.s_feedcard_authorvisible || !preferences.s_feedcard_datevisible) {
            desc.setMaxLines(3);
        }

        author.setVisibility(preferences.s_feedcard_authorvisible ? View.VISIBLE : View.GONE);
        title.setVisibility(preferences.s_feedcard_titlevisible ? View.VISIBLE : View.GONE);
        desc.setVisibility(preferences.s_feedcard_descvisible ? View.VISIBLE : View.GONE);
        date.setVisibility(preferences.s_feedcard_datevisible ? View.VISIBLE : View.GONE);
        //endregion

        //region Set data
        date.setText(PreferencesManager.formatDate(item.getPubDate(), preferences.s_feedcard_datestyle, holder.titleTextView.getContext()));
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        if (item.getAuthor() != null && !preferences.s_feedcard_authorname) {
            author.setText(item.getAuthor());
        } else {
            author.setText(item.getAuthor());
        }
        image.setImageDrawable(null);

        Picasso.get().cancelRequest(image);
        //endregion


        //region Load image
        if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.NONE) {
            image.setVisibility(View.GONE);
        } else if (item.getImageUrl() == null) {
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layoutParams);
        } else {
            loadImage(image, holder, item);
        }
        //endregion
    }

    private void loadImage(ImageView image, ItemViewHolder holder, Item item) {
        if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                      LinearLayout.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, IMAGE_MARGIN_PX);
            image.setLayoutParams(layoutParams);
        }

        int targetHeight;
        if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.SMALL) {
            targetHeight = PreferencesManager.dpToPx(100, holder.titleTextView.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                      LinearLayout.LayoutParams.WRAP_CONTENT,
                      ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, IMAGE_MARGIN_PX, 0);
            image.setLayoutParams(layoutParams);

        } else {
            targetHeight = 0;
        }

        // Handle nonexistent image
        String imageUrl = item.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            RequestCreator requestCreator = Picasso.get().load(imageUrl)
                      .resize(imageWidth, targetHeight)
                      .transform(new MaskTransformation(appContext, R.drawable.image_rounded))
                      .centerCrop();
            if (!preferences.s_imagecache) {
                requestCreator.networkPolicy(NetworkPolicy.NO_STORE);
            }
            requestCreator.into(image);
        }
    }

    private void bindHeader(ItemViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.header.setText(viewTitle);
        if (items.isEmpty() & !headerVisible) {
            headerViewHolder.itemView.setVisibility(View.GONE);
        } else {
            headerViewHolder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (items.isEmpty()) {
            return 1;
        } else {
            return items.size() + 1;
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