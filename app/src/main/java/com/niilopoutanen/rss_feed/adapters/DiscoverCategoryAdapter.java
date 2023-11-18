package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.CategoryView;
import com.niilopoutanen.rss_feed.models.Category;

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

        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.discover_category_title);
        }
    }

}
