package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.activities.FeedActivity;
import com.niilopoutanen.rss_feed.activities.MainActivity;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.models.RecyclerViewInterface;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Item;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Date;

public class FeedCard extends RecyclerView.ViewHolder{
    private final TextView title;
    private final TextView desc;
    private final TextView author;
    private final TextView date;
    private final ImageView image;
    private final View container;
    private final LinearLayout iconContainer;

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

        iconContainer = itemView.findViewById(R.id.feedcard_title_container);
        container = itemView;

        itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position);
                }
            }
        });
    }

    public static FeedCard create(ViewGroup parent, Preferences preferences, RecyclerViewInterface recyclerViewInterface) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

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

        setCardSpacing(view, context);
        return new FeedCard(view, preferences, context, recyclerViewInterface);
    }

    public void bindData(Item item){
        setPreferences();

        title.setVisibility(item.getTitle() == null ? View.GONE : View.VISIBLE);
        title.setText(item.getTitle());

        desc.setVisibility(item.getDescription() == null ? View.GONE : View.VISIBLE);
        desc.setText(item.getDescription());

        date.setVisibility(item.getPubDate() == null ? View.GONE : View.VISIBLE);
        date.setText(PreferencesManager.formatDate(item.getPubDate(), preferences.s_feedcard_datestyle, context));

        author.setVisibility(item.getAuthor() == null ? View.GONE : View.VISIBLE);
        author.setText(item.getAuthor());

        loadImage(item);
        loadIcons(item);
    }

    private void setPreferences(){
        if(preferences == null){
            return;
        }
        
        if (!preferences.s_feedcard_authorvisible || !preferences.s_feedcard_datevisible) {
            desc.setMaxLines(3);
        }

        author.setVisibility(preferences.s_feedcard_authorvisible ? View.VISIBLE : View.GONE);
        title.setVisibility(preferences.s_feedcard_titlevisible ? View.VISIBLE : View.GONE);
        desc.setVisibility(preferences.s_feedcard_descvisible ? View.VISIBLE : View.GONE);
        date.setVisibility(preferences.s_feedcard_datevisible ? View.VISIBLE : View.GONE);

        if(preferences.s_animateclicks){
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
                layoutParams.setMargins(0, 0, 0, PreferencesManager.dpToPx(5, context));
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

    private void loadIcons(Item item){
        if(item.getLink() == null){
            createIcon(R.drawable.icon_no_article);
        }

    }
    private void createIcon(@DrawableRes int resource){
        if(iconContainer == null){
            return;
        }

        View icon = new View(context);
        int size = PreferencesManager.dpToPx(15, context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(size, size);
        layoutParams.setMargins(PreferencesManager.dpToPx(5, context),0,0,0);
        icon.setLayoutParams(layoutParams);

        Drawable drawable = AppCompatResources.getDrawable(context, resource);
        icon.setBackground(drawable);

        icon.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.textPrimary)));

        String tag = String.valueOf(resource);

        if (iconContainer.findViewWithTag(tag) == null) {
            icon.setTag(tag);
            iconContainer.addView(icon);
        }
    }
    public int getImageWidth(){
        switch (preferences.s_feedcardstyle) {
            case LARGE:
                if(context instanceof MainActivity){
                    return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_LARGE, context);
                }
                else if(context instanceof FeedActivity){
                    return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_LARGE_FULLSCREEN, context);
                }
            case SMALL:
                return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_SMALL, context);
            case NONE:
                return  0;
            default:
                return PreferencesManager.getImageWidth(PreferencesManager.FEED_IMAGE_LARGE, context);
        }
    }
    private static void setCardSpacing(View view, Context context) {
        int margin = PreferencesManager.dpToPx(10, context);
        int gap = PreferencesManager.dpToPx(FeedFragment.CARDGAP_DP, context);

        boolean hasSideGap = context.getResources().getInteger(R.integer.feed_columns) > 1;

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if(!hasSideGap){
            layoutParams.setMargins(0, 0, 0, gap);
        }
        else {
            layoutParams.setMargins(0, 0, margin, margin);
        }

        view.setLayoutParams(layoutParams);
    }
}
