package com.niilopoutanen.rss_feed.rss;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.ArticleActivity;
import com.niilopoutanen.rss_feed.ImageViewActivity;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;

import java.util.Date;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_HEADER = 2;
    private static final int VIEW_TYPE_FOOTER = 3;
    private final List<ArticleItem> adapterData;
    private final Preferences preferences;
    private final Context appContext;
    private final String postUrl;
    private final Date postDate;
    private final String publisher;
    public ArticleAdapter(List<ArticleItem> data, Preferences preferences, Context context, String postUrl, Date postDate, String publisher) {
        this.adapterData = data;
        this.preferences = preferences;
        this.appContext = context;
        this.postUrl = postUrl;
        this.postDate = postDate;
        this.publisher = publisher;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(appContext);
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.article_header, parent, false);
                LinearLayout returnBtn = view.findViewById(R.id.article_return);
                returnBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity)appContext).finish();
                    }
                });

                TextView publisherView = view.findViewById(R.id.article_source);
                publisherView.setText(publisher);

                TextView dateView = view.findViewById(R.id.article_publishtime);
                dateView.setText(android.text.format.DateFormat.getDateFormat(appContext).format(postDate));
                return new HeaderFooterViewHolder(view);
            case VIEW_TYPE_FOOTER:
                view = inflater.inflate(R.layout.article_footer, parent, false);
                LinearLayout openInBrowser = view.findViewById(R.id.article_viewinbrowser);
                openInBrowser.setOnClickListener(v -> appContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl))));
                return new HeaderFooterViewHolder(view);
            case VIEW_TYPE_IMAGE:
                ImageView imageView = new ImageView(appContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                int marginPx = PreferencesManager.dpToPx(15, appContext);
                imageView.setPadding(0, marginPx, 0, marginPx);

                return new ContentViewHolder(imageView);
            case VIEW_TYPE_TEXT:
                TextView textView = new TextView(appContext);
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(appContext.getColor(R.color.textPrimary));
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setTypeface(PreferencesManager.getSavedFont(preferences, appContext));

                return new ContentViewHolder(textView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try{
            ArticleItem item = adapterData.get(position -1);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_IMAGE:
                    ContentViewHolder imageviewHolder = (ContentViewHolder) holder;
                    imageviewHolder.imageView.setImageBitmap(((BitmapItem)item).getBitmap());

                    imageviewHolder.imageView.setOnClickListener(v -> {
                        Intent imageIntent = new Intent(appContext, ImageViewActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)appContext, imageviewHolder.imageView, "img");
                        imageIntent.putExtra("imageurl", ((BitmapItem)item).getUrl());
                        appContext.startActivity(imageIntent, options.toBundle());
                    });
                    break;
                case VIEW_TYPE_TEXT:
                    ContentViewHolder textviewHolder = (ContentViewHolder) holder;
                    textviewHolder.textView.setText(((SpannedItem)item).getSpanned());
                    break;
                case VIEW_TYPE_HEADER:
                    LinearLayout returnBtn = ((HeaderFooterViewHolder)holder).returnBtn;
                    returnBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity)appContext).finish();
                        }
                    });

                    TextView title = ((HeaderFooterViewHolder)holder).articleTitle;
                    title.setText(((TitleItem)item).getTitle());

                    TextView publisherView = ((HeaderFooterViewHolder)holder).articleTitle;
                    publisherView.setText(publisher);

                    TextView dateView = ((HeaderFooterViewHolder)holder).articleDate;
                    dateView.setText(android.text.format.DateFormat.getDateFormat(appContext).format(postDate));
                    break;
            }
        }
        catch (Exception ignored){}
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        else if (position == adapterData.size() + 1) {
            return VIEW_TYPE_FOOTER;
        }
        else {
            Object item = adapterData.get(position - 1);
            if (item instanceof SpannedItem) {
                return VIEW_TYPE_TEXT;
            } else if (item instanceof BitmapItem) {
                return VIEW_TYPE_IMAGE;
            }
            throw new IllegalArgumentException("Invalid item type");
        }
    }
    @Override
    public int getItemCount() {
        if(adapterData == null){
            return 0;
        }
        return adapterData.size() + 2; // Add two for header and footer views
    }
    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout openInBrowser;
        LinearLayout returnBtn;
        TextView articleTitle;
        TextView articleDate;
        HeaderFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
    private static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        ContentViewHolder(ImageView view) {
            super(view);
            imageView = view;
        }
        ContentViewHolder(TextView view) {
            super(view);
            textView = view;
        }
    }

    public static class TitleItem implements ArticleItem {
        private final String title;

        public TitleItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
    public static class SpannedItem implements ArticleItem {
        private final Spanned spanned;

        public SpannedItem(Spanned spanned) {
            this.spanned = spanned;
        }

        public Spanned getSpanned() {
            return spanned;
        }
    }

    public static class BitmapItem implements ArticleItem {
        private final Bitmap bitmap;
        private final String url;

        public BitmapItem(Bitmap bitmap, String url) {
            this.bitmap = bitmap;
            this.url = url;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
        public String getUrl() {
            return url;
        }
    }
    public interface ArticleItem {
    }



}
