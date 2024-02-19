package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class IconView extends RelativeLayout {
    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private static double calculateShapeRadius(int width){
        double radiusPercentage = 22.50;
        double radius = width * (radiusPercentage / 100.0);
        return radius;
    }
}
