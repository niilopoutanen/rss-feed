package com.niilopoutanen.rss_feed.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SettingsControl extends RelativeLayout {
    String key;
    String title = "Control name";


    public SettingsControl(Context context) {
        super(context);
        init(context);
    }

    public SettingsControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context){
        setGravity(CENTER_VERTICAL);

        TextView name = new TextView(context);
        name.setText(title);

        addView(name);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {}

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {}

    @Override
    public void addView(View child, int width, int height) {}

    @Override
    public void addView(View child, int index) {}

    @Override
    public void addView(View child) {}
}
