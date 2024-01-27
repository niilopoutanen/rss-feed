package com.niilopoutanen.rss_feed.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.rss.Post;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.splash.SplashActivity;

import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;

public class DebugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_debug);

        initControls();
    }

    private void initControls() {
        LinearLayout container = findViewById(R.id.debug_container);

        PreferencesManager.setHeader(this, findViewById(R.id.debug_header));

        findViewById(R.id.debug_terminate).setOnClickListener(v -> finishAffinity());


        String details = "v" + BuildConfig.VERSION_NAME + ", BuildCode " + BuildConfig.VERSION_CODE +
                  "\n Device: " + Build.DEVICE
                  + "\n OS version: " + Build.VERSION.SDK_INT
                  + "\n Model: " + Build.MODEL
                  + "\n Brand: " + Build.BRAND
                  + "\n Supported ABIs: " + Arrays.toString(Build.SUPPORTED_ABIS)
                  + "\n Bootloader: " + Build.BOOTLOADER
                  + "\n Display: " + Build.DISPLAY
                  + "\n Product: " + Build.PRODUCT
                  + "\n DateTime: " + Date.from(Instant.now()).toString();
        ((TextView) findViewById(R.id.debug_device_details)).setText(details);


        findViewById(R.id.debug_open_article_with_url).setOnClickListener(v -> {
            final EditText input = new EditText(DebugActivity.this);

            new MaterialAlertDialogBuilder(DebugActivity.this)
                      .setTitle("Enter URL to open")
                      .setView(input)
                      .setPositiveButton("OK", (dialog, which) -> {
                          Intent articleIntent = new Intent(DebugActivity.this, ArticleActivity.class);
                          articleIntent.putExtra("preferences", PreferencesManager.loadPreferences(DebugActivity.this));
                          Post post = new Post();
                          post.link = input.getText().toString();
                          articleIntent.putExtra("post", post);

                          startActivity(articleIntent);
                      })
                      .setNegativeButton("Cancel", null)
                      .show();
        });

        findViewById(R.id.debug_showchangelog).setOnClickListener(v -> {
            Intent onBoardingIntent = new Intent(DebugActivity.this, SplashActivity.class);
            startActivity(onBoardingIntent);
        });


    }
}