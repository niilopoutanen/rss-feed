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
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Item;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class FeedCard extends RecyclerView.ViewHolder{
    private final TextView title;
    private final TextView desc;
    private final TextView author;
    private final TextView date;
    private final ImageView image;
    private final View container;

    private final Preferences preferences;
    private final Context context;
    public FeedCard(@NonNull View itemView, Preferences preferences, Context context, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.preferences = preferences;
        this.context = context;

        title = itemView.findViewById(R.id.feedcard_title);
        desc = itemView.findViewById(R.id.feedcard_description);
        author = itemView.findViewById(R.id.feedcard_author);
        date = itemView.findViewById(R.id.feedcard_date);
        image = itemView.findViewById(R.id.feedcard_image);
        container = itemView;

        itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position);
                }
            }
        });
    }

    public static FeedCard create(ViewGroup parent, Preferences preferences, RecyclerViewInterface recyclerViewInterface) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int margin = PreferencesManager.dpToPx(FeedFragment.CARDMARGIN_DP, context);
        int gap = PreferencesManager.dpToPx(FeedFragment.CARDGAP_DP, context);
        View view;

        switch (preferences.s_feedcardstyle){
            case SMALL:
                view = inflater.inflate(R.layout.feedcard_small, parent, false);
                break;
            case NONE:
                view = inflater.inflate(R.layout.feedcard_small, parent, false);
                view.findViewById(R.id.feedcard_image).setVisibility(View.GONE);
                break;

            case LARGE:
            default:
                view = inflater.inflate(R.layout.feedcard, parent, false);
                break;
        }

        setViewMargins(view, 0, 0, 0, gap);
        return new FeedCard(view, preferences, context, recyclerViewInterface);
    }

    public void bindData(Item item){
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        date.setText(item.getPubDate().toString());
        author.setText(item.getAuthor());

        setPreferences();
        loadImage(item);

    }

    private void setPreferences(){
        if (!preferences.s_feedcard_authorvisible || !preferences.s_feedcard_datevisible) {
            desc.setMaxLines(3);
        }

        author.setVisibility(preferences.s_feedcard_authorvisible ? View.VISIBLE : View.GONE);
        title.setVisibility(preferences.s_feedcard_titlevisible ? View.VISIBLE : View.GONE);
        desc.setVisibility(preferences.s_feedcard_descvisible ? View.VISIBLE : View.GONE);
        date.setVisibility(preferences.s_feedcard_datevisible ? View.VISIBLE : View.GONE);

        Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
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
    }

    private void loadImage(Item item){
        // Cancel last load call
        image.setImageDrawable(null);
        Picasso.get().cancelRequest(image);


        // Load image
        if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.NONE) {
            image.setVisibility(View.GONE);
        }
        else if (item.getImageUrl() == null) {
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layoutParams);
        }
        else {
            if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.LARGE) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                          LinearLayout.LayoutParams.MATCH_PARENT,
                          ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, PreferencesManager.dpToPx(10, context));
                image.setLayoutParams(layoutParams);
            }

            int targetHeight;
            if (preferences.s_feedcardstyle == Preferences.FeedCardStyle.SMALL) {
                targetHeight = PreferencesManager.dpToPx(100, context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                          LinearLayout.LayoutParams.WRAP_CONTENT,
                          ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, PreferencesManager.dpToPx(10, context), 0);
                image.setLayoutParams(layoutParams);

            } else {
                targetHeight = 0;
            }

            // Handle nonexistent image
            String imageUrl = item.getImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                RequestCreator requestCreator = Picasso.get().load(imageUrl)
                          .resize(getImageWidth(), targetHeight)
                          .transform(new MaskTransformation(context, R.drawable.image_rounded))
                          .centerCrop();
                if (!preferences.s_imagecache) {
                    requestCreator.networkPolicy(NetworkPolicy.NO_STORE);
                }
                requestCreator.into(image);

            }
        }
    }

    public int getImageWidth(){
        switch (preferences.s_feedcardstyle) {
            case LARGE:
            default:
                return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_LARGE, context);
            case SMALL:
                return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_SMALL, context);
            case NONE:
                return  0;
        }
    }
    private static void setViewMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
    }
}
