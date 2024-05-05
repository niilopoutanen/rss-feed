package com.niilopoutanen.rss_feed.fragments.components.feed;

import static com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.niilopoutanen.rss_feed.resources.R;

import java.util.List;

public class AdItem extends FeedItem{
    NativeAdView adView;
    CardView container;
    TextView title, desc, cta;
    IconView icon;
    ImageView image;
    public AdItem(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_ad;
    }

    @Override
    public void onClick(Object data) {

    }

    @Override
    public void bind(Object data) {
        adView = getContent().findViewById(R.id.ad_view);
        container = getContent().findViewById(R.id.ad_card);
        title = getContent().findViewById(R.id.ad_title);
        desc = getContent().findViewById(R.id.ad_desc);
        cta = getContent().findViewById(R.id.ad_cta);
        icon = getContent().findViewById(R.id.ad_icon);
        image = getContent().findViewById(R.id.ad_image);

        adView.setHeadlineView(title);
        adView.setIconView(icon);
        NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder()
                  .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                  .setRequestMultipleImages(false)
                  .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                  .build();
        final AdLoader adLoader = new AdLoader.Builder(this.context, "ca-app-pub-2347063544693669/5674529662")
                .forNativeAd(this::displayNativeAd)
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }
                })
                .withNativeAdOptions(nativeAdOptions)
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    private void displayNativeAd(NativeAd nativeAd){
        container.setVisibility(View.VISIBLE);
        title.setText(nativeAd.getHeadline());
        desc.setText(nativeAd.getBody());
        cta.setText(nativeAd.getCallToAction());
        if(nativeAd.getIcon() != null){
            icon.setResource(nativeAd.getIcon().getDrawable());
        }

        MediaContent mediaContent = nativeAd.getMediaContent();
        if(mediaContent != null){
            image.setImageDrawable(mediaContent.getMainImage());
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mediaContent.getMainImage();
            if(bitmapDrawable == null) return;

            Palette palette = Palette.from(bitmapDrawable.getBitmap()).generate();
            Palette.Swatch dominant = palette.getDominantSwatch();
            Palette.Swatch vibrant = palette.getVibrantSwatch();
            if(dominant == null) return;

            ConstraintLayout bottomView = getContent().findViewById(R.id.ad_bottom_container);
            bottomView.setBackgroundColor(dominant.getRgb());

            if(vibrant == null) return;
            LinearLayout ctaButton = getContent().findViewById(R.id.ad_cta_button);
            ctaButton.setBackgroundTintList(ColorStateList.valueOf(vibrant.getRgb()));

        }



    }
}
