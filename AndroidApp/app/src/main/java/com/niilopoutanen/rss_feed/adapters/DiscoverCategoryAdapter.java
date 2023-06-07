package com.niilopoutanen.rss_feed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.MaskableFrameLayout;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Category;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscoverCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Category> categories;
    private final View.OnClickListener onClickListener;

    public DiscoverCategoryAdapter(List<Category> categories, View.OnClickListener onClickListener) {
        this.categories = categories;
        this.onClickListener = onClickListener;
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
        ImageView itemImage = ((ItemViewHolder) holder).imageView;
        TextView itemTitle = ((ItemViewHolder) holder).textView;
        itemTitle.setText(category.getName());
        if (category.getImageUrl() == null) {
            return;
        }
        Picasso.get().load(category.getImageUrl()).resize(0, PreferencesManager.dpToPx(200, holder.itemView.getContext())).into(itemImage);
        holder.itemView.setOnClickListener(onClickListener);

        //Fade the text
        ((MaskableFrameLayout) holder.itemView).setOnMaskChangedListener(maskRect -> {
            ((ItemViewHolder) holder).textView.setTranslationX(maskRect.left);
            float alpha = interpolateFade(maskRect.left);
            ((ItemViewHolder) holder).textView.setAlpha(alpha);
        });
    }

    private float interpolateFade(float value) {
        float range = 65.0f - 0.0f;
        float progress = (value - 0.0f) / range;
        return 1.0f + (0.0f - 1.0f) * progress;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.discover_category_image);
            textView = itemView.findViewById(R.id.discover_category_title);
        }
    }

}
