package com.niilopoutanen.rss_feed.splash;

import static com.niilopoutanen.rss_feed.common.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.common.models.Preferences.SP_SHOW_CHANGELOG;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.niilopoutanen.rss_feed.common.PreferencesManager;

public class OnBoardingFragment extends SplashFragment {
    public OnBoardingFragment() {}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
        initVersion(rootView);
        return rootView;
    }

    private void initVersion(View rootView){
        TextView whatsNew = rootView.findViewById(R.id.onboarding_version_title);
        String htmlVersion = "<font color='" + PreferencesManager.getAccentColor(context) + "'> v" + PreferencesManager.getVersionName(context) + "</font>";
        String headerText = getString(com.niilopoutanen.rss_feed.splash.R.string.whats_new_in, "<br>") + htmlVersion + "?";
        whatsNew.setText(Html.fromHtml(headerText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        View continueButton = rootView.findViewById(R.id.onboarding_version_continue);
        continueButton.setOnClickListener(v -> {
            super.next();
        });
        continueButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, com.niilopoutanen.rss_feed.common.R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, com.niilopoutanen.rss_feed.common.R.anim.scale_up));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(context, com.niilopoutanen.rss_feed.common.R.anim.scale_up));
                view.performClick();
            }
            return true;
        });

        View dismissButton = rootView.findViewById(R.id.onboarding_version_do_not_show);
        dismissButton.setOnClickListener(v -> {
            PreferencesManager.saveBooleanPreference(SP_SHOW_CHANGELOG, PREFS_FUNCTIONALITY, false, context);
            super.cancel();
        });
    }
}
