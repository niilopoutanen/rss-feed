package com.niilopoutanen.rss_feed.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.R;
import com.niilopoutanen.rss_feed.fragments.SourcesFragment;
import com.niilopoutanen.rss_feed.models.Preferences;
import com.niilopoutanen.rss_feed.utils.PreferencesManager;
import com.niilopoutanen.rss_feed.utils.SaveSystem;
import com.niilopoutanen.rss_feed.fragments.SettingsFragment;
import com.niilopoutanen.rss_feed.fragments.UpdateDialog;
import com.niilopoutanen.rss_feed.fragments.DiscoverFragment;
import com.niilopoutanen.rss_feed.models.Source;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Preferences preferences;
    List<Source> sources = new ArrayList<>();
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUpdate();
        preferences = PreferencesManager.loadPreferences(this);
        PreferencesManager.setSavedTheme(this, preferences);

        setContentView(R.layout.activity_main);

        // Restore the state of the currently opened fragment
        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
        }
        sources = SaveSystem.loadSources(MainActivity.this);


        // Navigation bar init
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        // If the current fragment is null, set the default fragment to be loaded
        if (currentFragment == null) {
            switch (preferences.s_launchwindow) {
                case SETTINGS:
                    currentFragment = new SettingsFragment(this);
                    bottomNav.setSelectedItemId(R.id.nav_settings);
                    break;
                case FEED:
                    currentFragment = new FeedFragment(sources, preferences);
                    bottomNav.setSelectedItemId(R.id.nav_feed);
                    break;
                case SOURCES:
                    currentFragment = new SourcesFragment(this, preferences);
                    bottomNav.setSelectedItemId(R.id.nav_sources);
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, currentFragment)
                    .commit();
        }
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == bottomNav.getSelectedItemId()) {
                    // The selected item is already active, do nothing
                    return true;
                }

                if (itemId == R.id.nav_settings) {
                    currentFragment = new SettingsFragment(MainActivity.this);
                } else if (itemId == R.id.nav_feed) {
                    sources = SaveSystem.loadSources(MainActivity.this);
                    preferences = PreferencesManager.loadPreferences(MainActivity.this);
                    currentFragment = new FeedFragment(sources, preferences);
                } else if (itemId == R.id.nav_sources) {
                    currentFragment = new SourcesFragment(MainActivity.this, preferences);
                }
                else if (itemId == R.id.nav_discover) {
                    currentFragment = new DiscoverFragment(MainActivity.this, preferences);
                }

                return loadFragment(currentFragment);
            }
        });

        bottomNav.findViewById(R.id.nav_feed).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentFragment instanceof FeedFragment) {
                    FeedFragment feedFragment = (FeedFragment) currentFragment;
                    feedFragment.scrollToTop();
                    return true; // Event has been consumed
                }
                return false; // Event has not been consumed
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }

    private void checkUpdate() {
        boolean isFirstLaunch = PreferencesManager.isFirstLaunch(this);
        if (isFirstLaunch) {
            UpdateDialog dialog = new UpdateDialog(this);
            dialog.show();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            currentFragment = fragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


}