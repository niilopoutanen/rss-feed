package com.niilopoutanen.rss_feed.manager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.niilopoutanen.rss_feed.common.StatusView;

public class ImportActivity extends AppCompatActivity {

    private StatusView statusView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opmlactivity);


        statusView = findViewById(R.id.activity_import_status_view);

        statusView.addStatus("Test 1");
        statusView.addStatus("Test 2");
    }

}