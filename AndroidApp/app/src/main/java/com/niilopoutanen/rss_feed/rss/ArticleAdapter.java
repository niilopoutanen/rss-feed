package com.niilopoutanen.rss_feed.rss;

import android.graphics.Bitmap;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_TEXT = 1;
    private final List<Object> adapterData;

    public ArticleAdapter(List<Object> data) {
        this.adapterData = data;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return new ImageViewHolder(imageView);
            case VIEW_TYPE_TEXT:
                TextView textView = new TextView(parent.getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                return new TextViewHolder(textView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = adapterData.get(position);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_IMAGE:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.imageView.setImageBitmap((Bitmap) item);
                break;
            case VIEW_TYPE_TEXT:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.textView.setText((String) item);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = adapterData.get(position);
        if (item instanceof Spanned) {
            return VIEW_TYPE_TEXT;
        } else if (item instanceof Bitmap) {
            return VIEW_TYPE_IMAGE;
        }
        throw new IllegalArgumentException("Invalid item type");
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }
}
