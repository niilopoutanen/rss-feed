package com.niilopoutanen.rss_feed.fragments.components.feed;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.niilopoutanen.rss_feed.R;

public class AdItem extends FeedItem{
    TextView title;
    TextView desc;
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
        title = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_title);
        desc = getContent().findViewById(com.niilopoutanen.rss_feed.common.R.id.ad_desc);
        final AdLoader adLoader = new AdLoader.Builder(this.context, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {
                    title.setText(nativeAd.getHeadline());
                    desc.setText(nativeAd.getBody());
                })
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
}
