package com.niilopoutanen.rss_feed.common;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FadeContainer extends FrameLayout {
    private final List<View> views = new ArrayList<>();
    private final Handler handler = new Handler();
    private int activeIndex = 0;
    private final int FADE_DURATION = 500;
    private final int DELAY = 1000;

    public FadeContainer(@NonNull Context context) {
        super(context);
    }

    public void add(View view){
        super.addView(view);
        views.add(view);
        view.setAlpha(0f);
    }
    public void start(){
        fade();
        Runnable sequencer = new Runnable() {
            @Override
            public void run() {
                fade();
                handler.postDelayed(this, FADE_DURATION + DELAY);
            }
        };
        handler.postDelayed(sequencer, DELAY);
    }

    private void fade() {
        int nextIndex = activeIndex + 1;
        if(nextIndex >= views.size()){
            nextIndex = 0;
        }
        View currentView = views.get(activeIndex);
        View nextView = views.get(nextIndex);

        nextView.setAlpha(0f);
        nextView.animate().alpha(1f).setDuration(FADE_DURATION);
        currentView.animate().alpha(0f).setDuration(FADE_DURATION);
        activeIndex = nextIndex;
    }

}
