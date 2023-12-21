package com.niilopoutanen.rss_feed.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.models.MaskTransformation;
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
        ImageView coverView = findViewById(R.id.onboarding_version_cover);
        Picasso.get().load(COVER_URL)
                  .transform(new MaskTransformation(this, R.drawable.image_rounded))
                  .into(coverView);

        TextView versionTitle = findViewById(R.id.onboarding_version_title);
        String headerText = getString(R.string.whatsnew) + " v" + BuildConfig.VERSION_NAME + "?";
        versionTitle.setText(headerText);
    }
}