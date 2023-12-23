package com.niilopoutanen.rss_feed.activities;

import static com.niilopoutanen.rss_feed.models.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.models.Preferences.SP_SHOW_CHANGELOG;

import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;


import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

public class OnboardingActivity extends AppCompatActivity {
    private static final String COVER_URL = "https://raw.githubusercontent.com/niilopoutanen/RSS-Feed/app-resources/version.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_onboarding);

        initVersion();

    }


    private void initVersion(){
        TextView whatsNew = findViewById(R.id.onboarding_version_title);
        String htmlVersion = "<font color='" + PreferencesManager.getAccentColor(this) + "'> v" + BuildConfig.VERSION_NAME + "</font>";
        String headerText = getString(R.string.whats_new_in, "<br>") + htmlVersion + "?";
        whatsNew.setText(Html.fromHtml(headerText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        View continueButton = findViewById(R.id.onboarding_version_continue);
        continueButton.setOnClickListener(v -> {
            finish();
        });
        continueButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                continueButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
                view.performClick();
            }
            return true;
        });

        View dismissButton = findViewById(R.id.onboarding_version_do_not_show);
        dismissButton.setOnClickListener(v -> {
            PreferencesManager.saveBooleanPreference(SP_SHOW_CHANGELOG, PREFS_FUNCTIONALITY, false, OnboardingActivity.this);
            finish();
        });
    }

    @Override
    public void finish(){
        PreferencesManager.setLatestVersion(OnboardingActivity.this);
        super.finish();
    }
}