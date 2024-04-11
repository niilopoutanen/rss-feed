package com.niilopoutanen.rss_feed.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;


public class PrimaryButton extends RelativeLayout {
    private TextView text;
    private ProgressBar progressBar;
    public PrimaryButton(Context context) {
        super(context);
        init(null);
    }

    public PrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PrimaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs){
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

        int padding = PreferencesManager.dpToPx(5, getContext());
        setPadding(padding,padding,padding,padding);

        text = new TextView(getContext());
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PreferencesManager.dpToPx(40, getContext()));
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        text.setLayoutParams(textParams);
        text.setGravity(Gravity.CENTER);
        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_bold));
        text.setTextSize(14);
        text.setText(getContext().getString(R.string.continua));


        progressBar = new ProgressBar(getContext());
        int size = PreferencesManager.dpToPx(40, getContext());
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(size, size);
        progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(progressParams);
        progressBar.setVisibility(GONE);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(getContext().getColor(R.color.textPrimary)));

        if(attrs != null){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrimaryButton);
            setText(a.getString(R.styleable.PrimaryButton_text));

            setIsEnabled(a.getBoolean(R.styleable.PrimaryButton_enabled, true));
            setIsLoading(a.getBoolean(R.styleable.PrimaryButton_loading, false));

            a.recycle();
        }
        addView(text);
        addView(progressBar);
    }

    public void setText(String content){
        if(text != null && content != null && !content.isEmpty()){
            text.setText(content);
        }
    }

    public void setIsEnabled(boolean isEnabled){
        if(isEnabled){
            setVisibility(VISIBLE);
        }
        else{
            setVisibility(GONE);
        }
    }
    public void setIsLoading(boolean isLoading){
        if(isLoading){
            progressBar.setVisibility(getVisibility());
            text.setAlpha(0f);
        }
        else {
            progressBar.setVisibility(GONE);
            text.setAlpha(1f);
        }
    }

}