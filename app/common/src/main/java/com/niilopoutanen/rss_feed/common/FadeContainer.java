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

public class FadeContainer extends FrameLayout {
    private final Handler handler = new Handler();
    private int activeIndex = 0;
    private final int FADE_DURATION = 500;
    private final int DELAY = 1000;

    public FadeContainer(@NonNull Context context) {
        super(context);
        handler.postDelayed(sequencer, DELAY);

    }

    private final Runnable sequencer = new Runnable() {
        @Override
        public void run() {
            fade();
            handler.postDelayed(this, DELAY + FADE_DURATION);
        }
    };

    private void fade() {
        int nextIndex = activeIndex + 1;
        if(nextIndex >= getChildCount()){
            nextIndex = 0;
        }
        View currentView = getChildAt(activeIndex);
        View nextView = getChildAt(nextIndex);

        nextView.bringToFront();
        nextView.setAlpha(0f);
        nextView.animate().alpha(1f).setDuration(FADE_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                currentView.setAlpha(0f);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        activeIndex = nextIndex;
    }

}
