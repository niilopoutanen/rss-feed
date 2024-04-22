package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;
import com.niilopoutanen.rss_feed.resources.R;


public class IconView extends CardView {
    private ImageView icon;
    private TextView name;
    public IconView(Context context) {
        super(context);
        init();
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setCardBackgroundColor(getContext().getColor(R.color.surface));
        setClipChildren(true);

        setElevation(0);

        icon = new ImageView(getContext());
        FrameLayout.LayoutParams iconParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        icon.setLayoutParams(iconParams);
        addView(icon);

        name = new TextView(getContext());
        name.setTextSize(30);
        FrameLayout.LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER;
        name.setLayoutParams(textParams);
        name.setTextColor(PreferencesManager.getAccentColor(getContext()));
        name.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_black));
        addView(name);
    }



    public void setResource(Drawable drawable){
        if(drawable == null) return;
        if(icon == null) return;
        icon.setBackground(drawable);

        onResourceSet();
    }
    public void setResource(String url){
        if(url == null || url.isEmpty()) return;
        if(icon == null) return;
        Picasso.get().load(url).into(icon);

        onResourceSet();
    }
    public void setName(String nameStr){
        if(nameStr == null || nameStr.isEmpty()) return;
        if(icon == null || icon.getDrawable() != null) return;
        if(name == null) return;

        name.setText(nameStr.substring(0,1));
    }

    private void onResourceSet(){
        if(name != null){
            removeView(name);
            name = null;
        }
        setCardBackgroundColor(getContext().getColor(android.R.color.transparent));
    }
    private static double calculateShapeRadius(int width){
        double radiusPercentage = 25;
        return width * (radiusPercentage / 100.0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setRadius((float) calculateShapeRadius(w));
    }
}
