package com.niilopoutanen.rss_feed.activities;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        RelativeLayout continueBtn = findViewById(R.id.onboarding_version_continue);
        continueBtn.setOnClickListener(v -> {
            finish();
        });
    }
}