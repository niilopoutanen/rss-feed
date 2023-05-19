package com.niilopoutanen.rss_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.Category;

import org.w3c.dom.Text;

import java.util.List;

public class DiscoverCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public List<Category> categories;

    public DiscoverCategoryAdapter(List<Category> categories){
        this.categories = categories;
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
        ImageView itemImage = ((ItemViewHolder)holder).imageView;
        TextView itemTitle = ((ItemViewHolder)holder).textView;
        itemTitle.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.discover_category_image);
            textView = itemView.findViewById(R.id.discover_category_title);
        }
    }

}
