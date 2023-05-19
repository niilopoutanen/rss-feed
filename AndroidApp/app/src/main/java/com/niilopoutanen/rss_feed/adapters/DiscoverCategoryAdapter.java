package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Category;

import java.util.List;

public class DiscoverCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public List<Category> categories;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public DiscoverCategoryAdapter(List<Category> categories){
        this.categories = categories;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.header_discover, parent, false);
                return new HeaderViewHolder(view);
            case VIEW_TYPE_ITEM:
                view = inflater.inflate(R.layout.discover_category_item, parent, false);
                return new ItemViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            return;
        }
        Category category = categories.get(position - 1);
        TextView itemTitle = ((ItemViewHolder)holder).title;
        itemTitle.setText(category.getName());
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
    public int getItemCount() {
        if (categories == null) {
            return 0;
        }
        return categories.size() + 1; // Add 1 for header view
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.discover_category_name);
        }
    }

}
