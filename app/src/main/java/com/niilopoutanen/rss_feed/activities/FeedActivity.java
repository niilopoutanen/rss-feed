package com.niilopoutanen.rss_feed.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.NewFeedFragment;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss.Source;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;

/**
 * Used when viewing a single feed source only.
 */
public class FeedActivity extends AppCompatActivity {

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int sourceId = bundle.getInt("source_id");
            preferences = (Preferences) bundle.getSerializable("preferences");
            if(preferences == null){
                preferences = PreferencesManager.loadPreferences(this);
            }
            NewFeedFragment feedFragment = new NewFeedFragment(sourceId, preferences);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.feedactivity_container, feedFragment);
            transaction.commit();
        }

        PreferencesManager.setSavedTheme(this, preferences);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_feed);
    }
}