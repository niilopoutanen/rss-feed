package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.niilopoutanen.rss_feed.common.IconView;

import java.util.List;

public class AdItem extends FeedItem{
    NativeAdView adView;
    CardView container;
    TextView title, desc;
    IconView icon;
    ImageView image;
    public AdItem(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return com.niilopoutanen.rss_feed.common.R.layout.feed_ad;
    }

    @Override
    public void onClick(Object data) {

    }

    @Override
    public void bind(Object data) {
        adView = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_view);
        container = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_card);
        title = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_title);
        desc = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_desc);
        icon = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_icon);
        image = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_image);

        adView.setHeadlineView(title);
        adView.setIconView(icon);
        final AdLoader adLoader = new AdLoader.Builder(this.context, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(this::displayNativeAd)
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    private void displayNativeAd(NativeAd nativeAd){
        title.setText(nativeAd.getHeadline());
        desc.setText(nativeAd.getBody());
        if(nativeAd.getIcon() != null){
            icon.setResource(nativeAd.getIcon().getDrawable());
        }

        MediaContent mediaContent = nativeAd.getMediaContent();
        if(mediaContent != null){
            image.setImageDrawable(mediaContent.getMainImage());
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mediaContent.getMainImage();
            if(bitmapDrawable == null) return;

            Palette palette = Palette.from(bitmapDrawable.getBitmap()).generate();
            Palette.Swatch main = palette.getDominantSwatch();
            if(main == null) return;

            ConstraintLayout bottomView = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_bottom_container);
            bottomView.setBackgroundColor(main.getRgb());

        }



    }
}
