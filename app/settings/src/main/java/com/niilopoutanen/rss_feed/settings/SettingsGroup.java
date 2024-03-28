package com.niilopoutanen.rss_feed.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.niilopoutanen.rss_feed.common.PreferencesManager;

import java.util.List;

import kotlin.collections.ArrayDeque;

public class SettingsGroup extends LinearLayoutCompat {
    private final List<SettingsControl> controls = new ArrayDeque<>();

    private TextView title;
    private LinearLayoutCompat container;

    public SettingsGroup(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SettingsGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context){
        setOrientation(VERTICAL);

        title = new TextView(context);
        title.setText("Group heading");
        title.setMaxLines(1);
        ViewGroup.MarginLayoutParams titleParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = PreferencesManager.dpToPx(10, context);
        titleParams.setMargins(margin, 0, 0 ,margin);
        title.setLayoutParams(titleParams);

        container = new LinearLayoutCompat(context);
        container.setOrientation(VERTICAL);
        int padding = PreferencesManager.dpToPx(10, context);
        container.setPadding(padding, padding, padding, padding);
        container.setBackgroundResource(com.niilopoutanen.rss_feed.common.R.drawable.element_background);

        super.addView(title);
        super.addView(container);
    }


    @Override
    public void addView(View child) {
        if (child.getClass().isAssignableFrom(SettingsControl.class)){
            if(container != null){
                container.addView(child);
            }
        }

    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child.getClass().isAssignableFrom(SettingsControl.class)) {
            if (container != null) {
                container.addView(child);
            }
        }
    }
}
