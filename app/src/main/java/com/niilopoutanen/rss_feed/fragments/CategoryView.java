package com.niilopoutanen.rss_feed.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.ColorUtils;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

public class CategoryView extends RelativeLayout{
    private ImageView iconView;
    public CategoryView(Context context) {
        super(context);
        init(context, null);
    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        int iconSize = PreferencesManager.dpToPx(40, context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        iconView.setLayoutParams(layoutParams);

        addView(iconView);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CategoryView);
            Drawable customIcon = a.getDrawable(R.styleable.CategoryView_icon);
            if (customIcon != null) {
                setIcon(customIcon);
            }

            int backgroundColor = a.getColor(R.styleable.CategoryView_backgroundColor, context.getColor(android.R.color.transparent));
            int backgroundColorSecondary = a.getColor(R.styleable.CategoryView_backgroundColorSecondary, context.getColor(android.R.color.transparent));
            int iconColor = a.getColor(R.styleable.CategoryView_iconColor, context.getColor(android.R.color.transparent));
            setBackgroundGradient(backgroundColor, backgroundColorSecondary);
            setIconColor(iconColor);
            a.recycle();
        }
    }

    private void setBackgroundGradient(int color1, int color2) {;

        GradientDrawable gradientDrawable = new GradientDrawable(
                  GradientDrawable.Orientation.TOP_BOTTOM,
                  new int[]{color1, color2});

        setBackground(gradientDrawable);
    }
    private int darkenColor(int color) {
        return ColorUtils.blendARGB(color, Color.BLACK, 0.2f);
    }
    public void setIconColor(int color){
        iconView.setColorFilter(color);
    }
    public void setIcon(Drawable icon){
        iconView.setImageDrawable(icon);
    }
}
