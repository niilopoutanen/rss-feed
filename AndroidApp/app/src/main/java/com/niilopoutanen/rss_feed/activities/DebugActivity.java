package com.niilopoutanen.rss_feed.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niilopoutanen.rss_feed.BuildConfig;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rssparser.Item;

import java.util.Arrays;

public class DebugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager.setSavedTheme(this, PreferencesManager.loadPreferences(this));
        setContentView(R.layout.activity_debug);

        initControls();
    }

    private void initControls(){
        findViewById(R.id.debug_terminate).setOnClickListener(v -> finishAffinity());

        String version = "v" + BuildConfig.VERSION_NAME + ", BuildCode " + BuildConfig.VERSION_CODE;
        ((TextView)findViewById(R.id.debug_version)).setText(version);

        String deviceDetails = "device: " + Build.DEVICE
                  + "\n OS version: " + Build.VERSION.SDK_INT
                  + "\n model: " + Build.MODEL
                  + "\n brand: " + Build.BRAND
                  + "\n Supported ABIs: " + Arrays.toString(Build.SUPPORTED_ABIS)
                  + "\n product: " + Build.PRODUCT;
        ((TextView)findViewById(R.id.debug_device_details)).setText(deviceDetails);


        findViewById(R.id.debug_open_article_with_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(DebugActivity.this);

                new MaterialAlertDialogBuilder(DebugActivity.this)
                          .setTitle("Enter URL to open")
                          .setView(input)
                          .setPositiveButton("OK", (dialog, which) -> {
                              Intent articleIntent = new Intent(DebugActivity.this, ArticleActivity.class);
                              articleIntent.putExtra("preferences", PreferencesManager.loadPreferences(DebugActivity.this));
                              Item item = new Item();
                              item.setLink(input.getText().toString());
                              articleIntent.putExtra("item", item);

                              startActivity(articleIntent);
                          })
                          .setNegativeButton("Cancel", null)
                          .show();
            }
        });
    }
}