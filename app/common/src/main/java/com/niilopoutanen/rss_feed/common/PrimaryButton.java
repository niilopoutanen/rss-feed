package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;


public class PrimaryButton extends RelativeLayout {
    public PrimaryButton(Context context) {
        super(context);
        init();
    }

    public PrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrimaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.button_background));
        setBackgroundTintList(ColorStateList.valueOf(PreferencesManager.getAccentColor(getContext())));
        if (PreferencesManager.loadPreferences(getContext()).s_animateclicks){
            setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_down));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_up));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_up));
                    view.performClick();
                }
                return true;
            });
        }

        TextView text = new TextView(getContext());
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PreferencesManager.dpToPx(50, getContext()));
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        text.setLayoutParams(textParams);
        text.setGravity(Gravity.CENTER);
        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_bold));
        text.setTextSize(14);
        text.setText("Continue");

        addView(text);
    }
}
