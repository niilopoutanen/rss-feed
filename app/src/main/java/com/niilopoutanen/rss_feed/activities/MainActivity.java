package com.niilopoutanen.rss_feed.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationBarView;
import com.niilopoutanen.rss_feed.common.PreferencesManager;
import com.niilopoutanen.rss_feed.common.R;
import com.niilopoutanen.rss_feed.common.SeasonTheming;
import com.niilopoutanen.rss_feed.common.models.Preferences;
import com.niilopoutanen.rss_feed.database.AppViewModel;
import com.niilopoutanen.rss_feed.database.compatibility.SourceMigration;
import com.niilopoutanen.rss_feed.fragments.DiscoverFragment;
import com.niilopoutanen.rss_feed.fragments.FeedFragment;
import com.niilopoutanen.rss_feed.fragments.SettingsFragment;
import com.niilopoutanen.rss_feed.fragments.SourceFragment;
import com.niilopoutanen.rss_feed.splash.SplashActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Preferences preferences;
    private Fragment currentFragment;
    private AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        preferences = PreferencesManager.loadPreferences(this);
        PreferencesManager.setSavedTheme(this, preferences);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        String locale = Locale.getDefault().getLanguage();
        // Restore the state of the currently opened fragment
        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
        }
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);


        // Navigation bar init
        NavigationBarView bottomNav = findViewById(R.id.bottom_nav_menu);
        // If the current fragment is null, set the default fragment to be loaded
        if (currentFragment == null) {
            switch (preferences.s_launchwindow) {
                case SETTINGS:
                    currentFragment = SettingsFragment.newInstance();
                    bottomNav.setSelectedItemId(R.id.nav_settings);
                    break;
                case FEED:
                    currentFragment = FeedFragment.newInstance();
                    bottomNav.setSelectedItemId(R.id.nav_feed);
                    break;
                case SOURCES:
                    currentFragment = SourceFragment.newInstance(preferences);
                    bottomNav.setSelectedItemId(R.id.nav_content);
                    break;
                case DISCOVER:
                    currentFragment = DiscoverFragment.newInstance(preferences);
                    bottomNav.setSelectedItemId(R.id.nav_discover);
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                      .replace(R.id.frame_container, currentFragment)
                      .commit();
        }
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == bottomNav.getSelectedItemId()) {
                // The selected item is already active, do nothing
                return true;
            }
            preferences = PreferencesManager.loadPreferences(MainActivity.this);
            if (itemId == R.id.nav_settings) {
                currentFragment = SettingsFragment.newInstance();
            } else if (itemId == R.id.nav_feed) {
                currentFragment = FeedFragment.newInstance();
            } else if (itemId == R.id.nav_content) {
                currentFragment = SourceFragment.newInstance(preferences);
            } else if (itemId == R.id.nav_discover) {
                currentFragment = DiscoverFragment.newInstance(preferences);
            }

            return loadFragment(currentFragment);
        });

        bottomNav.findViewById(R.id.nav_feed).setOnLongClickListener(v -> {
            if (currentFragment instanceof FeedFragment) {
                FeedFragment feedFragment = (FeedFragment) currentFragment;
                feedFragment.scrollToTop();
                return true; // Event has been consumed
            }
            return false; // Event has not been consumed
        });

        int orientation = getResources().getConfiguration().orientation;
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            bottomNav.setPadding(insets.left, 0, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setNavigationBarColor(getColor(R.color.navbarBg));
        }


        if (SeasonTheming.isSeason()){
            FrameLayout seasonContainer = findViewById(R.id.season_resource);
            if(seasonContainer != null){
                ImageView graphic = SeasonTheming.inflate(this);
                if(graphic != null){
                    seasonContainer.addView(graphic);
                    seasonContainer.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void init(){
        boolean isFirstLaunch = PreferencesManager.isFirstLaunch(this);
        if (isFirstLaunch && !isFinishing() && !isDestroyed()) {
            Intent splashIntent = new Intent(this, SplashActivity.class);
            startActivity(splashIntent);
        }

        if(SourceMigration.needed(this)){
            SourceMigration.migrate(this);
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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }


}