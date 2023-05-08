package com.niilopoutanen.rss_feed.rss;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niilopoutanen.rss_feed.ImageViewActivity;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.customization.Preferences;
import com.niilopoutanen.rss_feed.customization.PreferencesManager;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Date;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_TITLE = 2;
    private static final int VIEW_TYPE_HEADER = 3;
    private static final int VIEW_TYPE_FOOTER = 4;
    private final List<ArticleItem> adapterData;
    private final Preferences preferences;
    private final Context appContext;
    private final String postUrl;
    private final Date postDate;
    private final String publisher;
    private String title = "";
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
                view = inflater.inflate(R.layout.header_article, parent, false);
                LinearLayout returnBtn = view.findViewById(R.id.article_return);
                returnBtn.setOnClickListener(v -> ((Activity)appContext).finish());

                TextView publisherView = view.findViewById(R.id.article_source);
                publisherView.setText(publisher);

                TextView dateView = view.findViewById(R.id.article_publishtime);
                dateView.setText(android.text.format.DateFormat.getDateFormat(appContext).format(postDate));
                return new HeaderFooterViewHolder(view);
            case VIEW_TYPE_FOOTER:
                view = inflater.inflate(R.layout.article_footer, parent, false);
                (view.findViewById(R.id.article_viewinbrowser)).setOnClickListener(v -> appContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl))));

                (view.findViewById(R.id.article_share)).setOnClickListener(v -> {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, postUrl);
                    shareIntent.putExtra(Intent.EXTRA_TITLE, title);
                    appContext.startActivity(Intent.createChooser(shareIntent, appContext.getString(R.string.sharepost)));
                });

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
            case VIEW_TYPE_TITLE:
                TextView titleTextView = new TextView(appContext);
                titleTextView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                titleTextView.setPadding(0,0,0,PreferencesManager.dpToPx(10, appContext));
                titleTextView.setTextColor(appContext.getColor(R.color.textPrimary));
                titleTextView.setTypeface(PreferencesManager.getSavedFont(preferences, appContext));
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                return new ContentViewHolder(titleTextView, true);
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
                    RequestCreator requestCreator = Picasso.get().load(((ImageItem)item).getUrl())
                            .resize(PreferencesManager.getImageWidth(PreferencesManager.ARTICLE_IMAGE, appContext), 0)
                            .transform(new MaskTransformation(appContext, R.drawable.image_rounded))
                            .centerCrop();
                    if (!preferences.s_imagecache) {
                        requestCreator.networkPolicy(NetworkPolicy.NO_STORE);
                    }

                    requestCreator.into(imageviewHolder.imageView);

                    imageviewHolder.imageView.setOnClickListener(v -> {
                        Intent imageIntent = new Intent(appContext, ImageViewActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)appContext, imageviewHolder.imageView, "img");
                        imageIntent.putExtra("imageurl", ((ImageItem)item).getUrl());
                        appContext.startActivity(imageIntent, options.toBundle());
                    });
                    break;
                case VIEW_TYPE_TEXT:
                    ContentViewHolder textviewHolder = (ContentViewHolder) holder;
                    textviewHolder.textView.setText(((SpannedItem)item).getSpanned());
                    break;
                case VIEW_TYPE_TITLE:
                    ContentViewHolder titleViewHolder = (ContentViewHolder) holder;
                    titleViewHolder.textView.setText(((TitleItem)item).getTitle());
                    title = (((TitleItem)item).getTitle());
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
            }
            else if (item instanceof TitleItem) {
                return VIEW_TYPE_TITLE;
            }
            else if (item instanceof ImageItem) {
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
        ContentViewHolder(TextView view, boolean title) {
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

    public static class ImageItem implements ArticleItem {
        private final String url;

        public ImageItem(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }
    }
    public interface ArticleItem {}
}
