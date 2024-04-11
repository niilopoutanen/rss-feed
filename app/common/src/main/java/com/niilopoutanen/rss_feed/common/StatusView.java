package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.ArrayList;
import java.util.List;


public class StatusView extends LinearLayoutCompat {
    private List<String> statusQueue = new ArrayList<>();
    private Handler queueHandler = new Handler();
    private final int ADD_STATUS_DELAY = 1000;
    public StatusView(@NonNull Context context) {
        super(context);
        init();
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        setOrientation(VERTICAL);
        queueHandler.postDelayed(this::processQueue, ADD_STATUS_DELAY);
    }

    public void addStatus(String status){
        statusQueue.add(status);
    }

    private void processQueue() {
        if (!statusQueue.isEmpty()) {
            String status = statusQueue.get(0);
            addToLayout(status);
            statusQueue.remove(0);
        }
        queueHandler.postDelayed(this::processQueue, ADD_STATUS_DELAY);
    }
    private void addToLayout(String status){
        TextView statusText = new TextView(getContext());
        statusText.setText(status + "...");
        statusText.setTextColor(getContext().getColor(R.color.textSecondary));
        statusText.setGravity(Gravity.CENTER_HORIZONTAL);
        statusText.setTextSize(17);
        statusText.setTypeface(getContext().getResources().getFont(R.font.inter_medium));
        super.addView(statusText);
    }
}
