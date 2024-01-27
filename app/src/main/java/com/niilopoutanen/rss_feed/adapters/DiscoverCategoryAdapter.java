package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

import java.util.List;

public class DiscoverCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Category> categories;
    private final View.OnClickListener onClickListener;
    private final Context context;

    public DiscoverCategoryAdapter(List<Category> categories, Context context, View.OnClickListener onClickListener) {
        this.categories = categories;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.discover_category_item, parent, false);
        return new ItemViewHolder(view);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Category category = categories.get(position);

        TextView itemTitle = ((ItemViewHolder) holder).textView;
        itemTitle.setText(category.getName());

        View icon = ((ItemViewHolder) holder).icon;
        icon.setBackground(AppCompatResources.getDrawable(context, category.getIconId()));

        View container = holder.itemView;
        container.setOnClickListener(onClickListener);

        if (category.isActive()) {
            container.setBackgroundTintList(ColorStateList.valueOf(context.getColor(com.niilopoutanen.rss_feed.common.R.color.element_active)));
            itemTitle.setTextColor(context.getColor(com.niilopoutanen.rss_feed.common.R.color.textInverted));
            icon.setBackgroundTintList(ColorStateList.valueOf(context.getColor(com.niilopoutanen.rss_feed.common.R.color.textInverted)));
        } else {
            container.setBackgroundTintList(ColorStateList.valueOf(context.getColor(com.niilopoutanen.rss_feed.common.R.color.element)));
            itemTitle.setTextColor(context.getColor(com.niilopoutanen.rss_feed.common.R.color.textPrimary));
            icon.setBackgroundTintList(ColorStateList.valueOf(context.getColor(com.niilopoutanen.rss_feed.common.R.color.textPrimary)));
        }


        if (PreferencesManager.loadPreferences(context).s_animateclicks) {
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

    @Override
    public int getItemCount() {
        return categories.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final View icon;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.discover_category_title);
            icon = itemView.findViewById(R.id.discover_category_icon);
        }
    }

}
